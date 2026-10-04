package hu.maci.mapynav.service

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import hu.maci.mapynav.model.NavState
import hu.maci.mapynav.parser.MapyNotificationParser
import hu.maci.mapynav.server.LocalNavigationServer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapyNotificationListenerService : NotificationListenerService() {

  override fun onCreate() {
    super.onCreate()
    Log.i(TAG, "MapyNotificationListenerService created")
    server.start()
    _isServiceRunning.value = true
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.i(TAG, "MapyNotificationListenerService destroyed")
    _isServiceRunning.value = false
  }

  override fun onListenerConnected() {
    super.onListenerConnected()
    Log.i(TAG, "Notification listener connected")
    _isListenerConnected.value = true
  }

  override fun onListenerDisconnected() {
    super.onListenerDisconnected()
    Log.i(TAG, "Notification listener disconnected")
    _isListenerConnected.value = false
  }

  override fun onNotificationPosted(sbn: StatusBarNotification?) {
    if (sbn == null) return
    val pkg = sbn.packageName

    // Also allow testing or Google Maps if desired, but prioritize Mapy.cz
    if (pkg == MAPY_PACKAGE || pkg == GOOGLE_MAPS_PACKAGE) {
      val notification = sbn.notification ?: return
      val extras = notification.extras ?: return

      val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
      val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
      val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()

      Log.d(TAG, "Notification from $pkg: title='$title', text='$text', subText='$subText'")

      val state = MapyNotificationParser.parse(title, text, subText)
      _latestNavState.value = state
      server.currentState = state
    }
  }

  override fun onNotificationRemoved(sbn: StatusBarNotification?) {
    if (sbn == null) return
    if (sbn.packageName == MAPY_PACKAGE || sbn.packageName == GOOGLE_MAPS_PACKAGE) {
      Log.d(TAG, "Navigation notification removed: ${sbn.packageName}")
      val idle = NavState.IDLE
      _latestNavState.value = idle
      server.currentState = idle
    }
  }

  companion object {
    private const val TAG = "MapyNavService"
    const val MAPY_PACKAGE = "cz.seznam.mapy"
    const val GOOGLE_MAPS_PACKAGE = "com.google.android.apps.maps"

    val server = LocalNavigationServer(8088)

    private val _latestNavState = MutableStateFlow(NavState.IDLE)
    val latestNavState: StateFlow<NavState> = _latestNavState.asStateFlow()

    private val _isServiceRunning = MutableStateFlow(false)
    val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

    private val _isListenerConnected = MutableStateFlow(false)
    val isListenerConnected: StateFlow<Boolean> = _isListenerConnected.asStateFlow()

    fun updateSimulatedState(state: NavState) {
      _latestNavState.value = state
      server.currentState = state
    }
  }
}
