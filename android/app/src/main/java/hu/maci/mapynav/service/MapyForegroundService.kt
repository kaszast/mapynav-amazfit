package hu.maci.mapynav.service
 
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import hu.maci.mapynav.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapyForegroundService : Service() {

  private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
  private var wakeLock: PowerManager.WakeLock? = null
  private lateinit var notificationManager: NotificationManager

  override fun onCreate() {
    super.onCreate()
    Log.i(TAG, "MapyForegroundService created")
    notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    createNotificationChannel()
    startAsForegroundService()
    acquireWakeLock()

    // Ensure Local HTTP Server is running
    MapyNotificationListenerService.server.start()
    _isForegroundRunning.value = true

    // Observe navigation state and update notification in real time
    serviceScope.launch {
      MapyNotificationListenerService.latestNavState.collect { state ->
        updateNotification(
          if (state.isActive) {
            val dist = state.distanceText.ifEmpty { "${state.distanceMeters} m" }
            val street = state.streetName.ifEmpty { state.directionText }
            "$dist • $street"
          } else {
            getString(hu.maci.mapynav.R.string.fgs_idle_desc)
          }
        )
      }
    }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Log.i(TAG, "MapyForegroundService onStartCommand")
    MapyNotificationListenerService.server.start()
    return START_STICKY
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.i(TAG, "MapyForegroundService destroyed")
    releaseWakeLock()
    serviceScope.cancel()
    _isForegroundRunning.value = false
  }

  override fun onBind(intent: Intent?): IBinder? = null

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        getString(hu.maci.mapynav.R.string.fgs_channel_name),
        NotificationManager.IMPORTANCE_LOW
      ).apply {
        description = getString(hu.maci.mapynav.R.string.fgs_channel_desc)
        setShowBadge(false)
      }
      notificationManager.createNotificationChannel(channel)
    }
  }

  private fun buildNotification(contentText: String): Notification {
    val pendingIntent = PendingIntent.getActivity(
      this,
      0,
      Intent(this, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
      },
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    return NotificationCompat.Builder(this, CHANNEL_ID)
      .setContentTitle(getString(hu.maci.mapynav.R.string.app_name))
      .setContentText(contentText)
      .setSmallIcon(android.R.drawable.ic_menu_compass)
      .setContentIntent(pendingIntent)
      .setOngoing(true)
      .setPriority(NotificationCompat.PRIORITY_LOW)
      .build()
  }

  private fun startAsForegroundService() {
    val notification = buildNotification(getString(hu.maci.mapynav.R.string.fgs_running_desc))
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      startForeground(
        NOTIFICATION_ID,
        notification,
        ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
      )
    } else {
      startForeground(NOTIFICATION_ID, notification)
    }
  }

  private fun updateNotification(contentText: String) {
    val notification = buildNotification(contentText)
    notificationManager.notify(NOTIFICATION_ID, notification)
  }

  private fun acquireWakeLock() {
    try {
      val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
      wakeLock = powerManager.newWakeLock(
        PowerManager.PARTIAL_WAKE_LOCK,
        "MapyNav::BridgeWakeLock"
      ).apply {
        setReferenceCounted(false)
        acquire(24 * 60 * 60 * 1000L) // Safe 24 hours max
      }
      Log.i(TAG, "Partial WakeLock acquired")
    } catch (e: Exception) {
      Log.e(TAG, "Failed to acquire WakeLock", e)
    }
  }

  private fun releaseWakeLock() {
    try {
      wakeLock?.let {
        if (it.isHeld) {
          it.release()
          Log.i(TAG, "WakeLock released")
        }
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error releasing WakeLock", e)
    } finally {
      wakeLock = null
    }
  }

  companion object {
    private const val TAG = "MapyForeground"
    private const val CHANNEL_ID = "mapynav_foreground_channel"
    private const val NOTIFICATION_ID = 2001

    private val _isForegroundRunning = MutableStateFlow(false)
    val isForegroundRunning: StateFlow<Boolean> = _isForegroundRunning.asStateFlow()

    fun start(context: Context) {
      val intent = Intent(context, MapyForegroundService::class.java)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
      } else {
        context.startService(intent)
      }
    }

    fun stop(context: Context) {
      val intent = Intent(context, MapyForegroundService::class.java)
      context.stopService(intent)
    }
  }
}
