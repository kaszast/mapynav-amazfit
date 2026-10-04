package hu.maci.mapynav

import hu.maci.mapynav.model.ManeuverType
import hu.maci.mapynav.model.NavState
import hu.maci.mapynav.server.LocalNavigationServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class LocalNavigationServerTest {

  @Test
  fun testServerReturnsNavJson() {
    val testPort = 18088
    val server = LocalNavigationServer(testPort)
    server.start()

    try {
      Thread.sleep(100) // allow start

      val state = NavState(
        isActive = true,
        maneuver = ManeuverType.TURN_RIGHT,
        directionText = "Jobbra",
        distanceText = "120 m",
        distanceMeters = 120,
        streetName = "Fő utca"
      )
      server.currentState = state

      val url = URL("http://127.0.0.1:$testPort/api/nav")
      val connection = url.openConnection() as HttpURLConnection
      connection.requestMethod = "GET"
      connection.connectTimeout = 2000
      connection.readTimeout = 2000

      val code = connection.responseCode
      assertEquals(200, code)

      val responseText = connection.inputStream.bufferedReader().use { it.readText() }
      assertTrue(responseText.contains("TURN_RIGHT"))
      assertTrue(responseText.contains("120 m"))
      assertTrue(responseText.contains("Fő utca"))
    } finally {
      server.stop()
    }
  }

  @Test
  fun testHealthEndpoint() {
    val testPort = 18089
    val server = LocalNavigationServer(testPort)
    server.start()

    try {
      Thread.sleep(100)
      val url = URL("http://127.0.0.1:$testPort/api/health")
      val connection = url.openConnection() as HttpURLConnection
      assertEquals(200, connection.responseCode)
      val responseText = connection.inputStream.bufferedReader().use { it.readText() }
      assertTrue(responseText.contains("ok"))
    } finally {
      server.stop()
    }
  }
}
