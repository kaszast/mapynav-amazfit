package hu.maci.mapynav.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import hu.maci.mapynav.service.MapyForegroundService
import hu.maci.mapynav.service.MapyNotificationListenerService
import hu.maci.mapynav.ui.theme.MapyNavTheme
import hu.maci.mapynav.util.LocaleHelper

class MainActivity : ComponentActivity() {

  private var isListenerPermissionGranted by mutableStateOf(false)
  private var isBatteryOptimizationIgnored by mutableStateOf(false)
  private var currentLanguage by mutableStateOf(LocaleHelper.LANG_SYSTEM)

  private val requestNotificationPermissionLauncher =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
      // Notification permission handled
    }

  override fun attachBaseContext(newBase: Context) {
    super.attachBaseContext(LocaleHelper.applyLocale(newBase))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    currentLanguage = LocaleHelper.getSavedLanguage(this)
    requestPostNotificationPermission()
    checkPermissions()

    // Always start Foreground Service and Server on app start
    MapyForegroundService.start(this)

    setContent {
      MapyNavTheme {
        NavScreen(
          isPermissionGranted = isListenerPermissionGranted,
          isBatteryOptimizationIgnored = isBatteryOptimizationIgnored,
          currentLanguage = currentLanguage,
          onSelectLanguage = { selectLanguage(it) },
          onOpenPermissionSettings = { openNotificationListenerSettings() },
          onRequestIgnoreBatteryOptimizations = { requestIgnoreBatteryOptimizations() },
          onOpenAppDetailsSettings = { openAppDetailsSettings() }
        )
      }
    }
  }

  override fun onResume() {
    super.onResume()
    currentLanguage = LocaleHelper.getSavedLanguage(this)
    checkPermissions()
    // Keep Foreground Service alive
    MapyForegroundService.start(this)
  }

  private fun selectLanguage(langCode: String) {
    LocaleHelper.setLanguage(this, langCode)
    currentLanguage = langCode
    recreate()
  }

  private fun checkPermissions() {
    // 1. Notification Listener Permission
    val cn = ComponentName(this, MapyNotificationListenerService::class.java)
    val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
    isListenerPermissionGranted = flat != null && flat.contains(cn.flattenToString())

    // 2. Battery Optimization Exemption
    val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
    isBatteryOptimizationIgnored = pm.isIgnoringBatteryOptimizations(packageName)
  }

  private fun requestPostNotificationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED
      ) {
        requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }
  }

  private fun openNotificationListenerSettings() {
    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
    startActivity(intent)
  }

  private fun requestIgnoreBatteryOptimizations() {
    try {
      val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
        data = Uri.parse("package:$packageName")
      }
      startActivity(intent)
    } catch (_: Exception) {
      openAppDetailsSettings()
    }
  }

  private fun openAppDetailsSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
      data = Uri.parse("package:$packageName")
    }
    startActivity(intent)
  }
}
