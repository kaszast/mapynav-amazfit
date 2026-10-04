package hu.maci.mapynav.server

import android.util.Log
import hu.maci.mapynav.model.NavState
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class LocalNavigationServer(
  private val port: Int = 8088
) {
  private val tag = "LocalNavServer"
  private var serverSocket: ServerSocket? = null
  private val isRunning = AtomicBoolean(false)
  private val executor = Executors.newCachedThreadPool()

  @Volatile
  var currentState: NavState = NavState.IDLE
    set(value) {
      field = value
      lastStateUpdateTime = System.currentTimeMillis()
    }

  @Volatile
  var lastRequestTime: Long = 0L
    private set

  @Volatile
  var lastStateUpdateTime: Long = 0L
    private set

  var onClientConnectedListener: ((String) -> Unit)? = null

  fun start() {
    if (isRunning.getAndSet(true)) return

    try {
      serverSocket = ServerSocket(port, 50, InetAddress.getByName("127.0.0.1"))
      try { Log.i(tag, "LocalNavigationServer listening on 127.0.0.1:$port") } catch (_: Throwable) {}
    } catch (e: Exception) {
      isRunning.set(false)
      throw e
    }

    executor.execute {
      try {
        while (isRunning.get()) {
          val socket = serverSocket?.accept() ?: break
          executor.execute { handleClient(socket) }
        }
      } catch (e: Exception) {
        if (isRunning.get()) {
          try { Log.e(tag, "Server loop error: ${e.message}", e) } catch (_: Throwable) {}
        }
      } finally {
        stop()
      }
    }
  }

  fun stop() {
    if (!isRunning.getAndSet(false)) return
    try {
      serverSocket?.close()
    } catch (_: Exception) {}
    serverSocket = null
  }

  private fun handleClient(socket: Socket) {
    try {
      socket.soTimeout = 3000
      val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
      val firstLine = reader.readLine() ?: return

      lastRequestTime = System.currentTimeMillis()
      onClientConnectedListener?.invoke(firstLine)

      val parts = firstLine.split(" ")
      val method = if (parts.isNotEmpty()) parts[0].uppercase() else "GET"
      val path = if (parts.size > 1) parts[1] else "/"

      val output: OutputStream = socket.getOutputStream()

      when {
        method == "OPTIONS" -> {
          sendResponse(output, 204, "No Content", "text/plain", "")
        }
        path.startsWith("/api/nav") -> {
          val json = currentState.toJson()
          sendResponse(output, 200, "OK", "application/json; charset=utf-8", json)
        }
        path.startsWith("/api/health") -> {
          sendResponse(output, 200, "OK", "application/json", """{"status":"ok","time":${System.currentTimeMillis()}}""")
        }
        else -> {
          sendResponse(output, 404, "Not Found", "text/plain", "Not Found")
        }
      }
    } catch (e: Exception) {
      Log.d(tag, "Client handling exception: ${e.message}")
    } finally {
      try {
        socket.close()
      } catch (_: Exception) {}
    }
  }

  private fun sendResponse(
    out: OutputStream,
    statusCode: Int,
    statusText: String,
    contentType: String,
    body: String
  ) {
    val bodyBytes = body.toByteArray(Charsets.UTF_8)
    val header = "HTTP/1.1 $statusCode $statusText\r\n" +
      "Content-Type: $contentType\r\n" +
      "Content-Length: ${bodyBytes.size}\r\n" +
      "Access-Control-Allow-Origin: *\r\n" +
      "Access-Control-Allow-Methods: GET, OPTIONS\r\n" +
      "Access-Control-Allow-Headers: Content-Type\r\n" +
      "Connection: close\r\n\r\n"

    out.write(header.toByteArray(Charsets.US_ASCII))
    if (bodyBytes.isNotEmpty()) {
      out.write(bodyBytes)
    }
    out.flush()
  }
}
