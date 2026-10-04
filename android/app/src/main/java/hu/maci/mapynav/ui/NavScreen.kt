package hu.maci.mapynav.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hu.maci.mapynav.R
import hu.maci.mapynav.model.ManeuverType
import hu.maci.mapynav.model.NavState
import hu.maci.mapynav.service.MapyForegroundService
import hu.maci.mapynav.service.MapyNotificationListenerService
import hu.maci.mapynav.ui.theme.AccentRed
import hu.maci.mapynav.ui.theme.AccentYellow
import hu.maci.mapynav.ui.theme.DarkBackground
import hu.maci.mapynav.ui.theme.GreenMapy
import hu.maci.mapynav.ui.theme.SurfaceBorder
import hu.maci.mapynav.ui.theme.SurfaceDark
import hu.maci.mapynav.ui.theme.SurfaceDarkElevated
import hu.maci.mapynav.ui.theme.TextLight
import hu.maci.mapynav.ui.theme.TextMuted
import hu.maci.mapynav.ui.theme.TextSubtle
import hu.maci.mapynav.util.LocaleHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavScreen(
  isPermissionGranted: Boolean,
  isBatteryOptimizationIgnored: Boolean,
  currentLanguage: String,
  onSelectLanguage: (String) -> Unit,
  onOpenPermissionSettings: () -> Unit,
  onRequestIgnoreBatteryOptimizations: () -> Unit,
  onOpenAppDetailsSettings: () -> Unit
) {
  val navState by MapyNotificationListenerService.latestNavState.collectAsState()
  val isForegroundRunning by MapyForegroundService.isForegroundRunning.collectAsState()
  val server = MapyNotificationListenerService.server
  val scrollState = rememberScrollState()

  Scaffold(
    containerColor = DarkBackground,
    topBar = {
      NavTopBar(
        currentLanguage = currentLanguage,
        onSelectLanguage = onSelectLanguage
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(scrollState)
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Live Navigation HUD Card
      LiveHudCard(navState = navState)

      // 2. System & Watch Bridge Status Card
      SystemBridgeCard(
        isPermissionGranted = isPermissionGranted,
        isForegroundRunning = isForegroundRunning,
        isBatteryOptimizationIgnored = isBatteryOptimizationIgnored,
        lastRequestTime = server.lastRequestTime,
        onOpenPermissionSettings = onOpenPermissionSettings,
        onRequestIgnoreBatteryOptimizations = onRequestIgnoreBatteryOptimizations,
        onOpenAppDetailsSettings = onOpenAppDetailsSettings
      )

      // 3. Test Simulator Card
      TestSimulatorCard()

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavTopBar(
  currentLanguage: String,
  onSelectLanguage: (String) -> Unit
) {
  var menuExpanded by remember { mutableStateOf(false) }

  TopAppBar(
    windowInsets = WindowInsets.statusBars,
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = GreenMapy.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, GreenMapy.copy(alpha = 0.3f)),
          modifier = Modifier.size(38.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.NearMe,
              contentDescription = null,
              tint = GreenMapy,
              modifier = Modifier.size(22.dp)
            )
          }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = stringResource(R.string.app_name),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextLight,
            maxLines = 1
          )
          Text(
            text = stringResource(R.string.header_subtitle),
            fontSize = 11.sp,
            color = TextMuted,
            maxLines = 1
          )
        }
      }
    },
    actions = {
      Box(modifier = Modifier.padding(end = 12.dp)) {
        val langLabel = when (currentLanguage) {
          LocaleHelper.LANG_HU -> "HU"
          LocaleHelper.LANG_EN -> "EN"
          else -> "AUTO"
        }
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = SurfaceDarkElevated,
          border = BorderStroke(1.dp, SurfaceBorder),
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { menuExpanded = true }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Language,
              contentDescription = null,
              tint = GreenMapy,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = langLabel,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = TextLight
            )
          }
        }

        DropdownMenu(
          expanded = menuExpanded,
          onDismissRequest = { menuExpanded = false },
          modifier = Modifier.background(SurfaceDarkElevated)
        ) {
          DropdownMenuItem(
            text = {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(stringResource(R.string.lang_system), color = TextLight, fontSize = 13.sp)
                if (currentLanguage == LocaleHelper.LANG_SYSTEM) {
                  Icon(Icons.Default.Check, contentDescription = null, tint = GreenMapy, modifier = Modifier.size(16.dp))
                }
              }
            },
            onClick = {
              menuExpanded = false
              onSelectLanguage(LocaleHelper.LANG_SYSTEM)
            }
          )
          DropdownMenuItem(
            text = {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(stringResource(R.string.lang_en), color = TextLight, fontSize = 13.sp)
                if (currentLanguage == LocaleHelper.LANG_EN) {
                  Icon(Icons.Default.Check, contentDescription = null, tint = GreenMapy, modifier = Modifier.size(16.dp))
                }
              }
            },
            onClick = {
              menuExpanded = false
              onSelectLanguage(LocaleHelper.LANG_EN)
            }
          )
          DropdownMenuItem(
            text = {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(stringResource(R.string.lang_hu), color = TextLight, fontSize = 13.sp)
                if (currentLanguage == LocaleHelper.LANG_HU) {
                  Icon(Icons.Default.Check, contentDescription = null, tint = GreenMapy, modifier = Modifier.size(16.dp))
                }
              }
            },
            onClick = {
              menuExpanded = false
              onSelectLanguage(LocaleHelper.LANG_HU)
            }
          )
        }
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = DarkBackground
    )
  )
}

@Composable
fun LiveHudCard(navState: NavState) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, SurfaceBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header row with Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Icon(
            imageVector = Icons.Default.Explore,
            contentDescription = null,
            tint = if (navState.isActive) GreenMapy else TextSubtle,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = stringResource(R.string.card_hud_title),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = TextLight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.width(8.dp))
        StatusBadge(isActive = navState.isActive)
      }

      Spacer(modifier = Modifier.height(14.dp))

      if (navState.isActive) {
        // Active Navigation HUD content
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = GreenMapy.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, GreenMapy.copy(alpha = 0.35f)),
            modifier = Modifier.size(56.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                imageVector = getManeuverIcon(navState.maneuver),
                contentDescription = null,
                tint = GreenMapy,
                modifier = Modifier.size(32.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = navState.distanceText.ifEmpty { "${navState.distanceMeters} m" },
              fontSize = 30.sp,
              fontWeight = FontWeight.ExtraBold,
              color = TextLight,
              maxLines = 1
            )
            Text(
              text = navState.directionText.ifEmpty { navState.maneuver.name },
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold,
              color = AccentYellow,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        if (navState.streetName.isNotEmpty()) {
          Spacer(modifier = Modifier.height(12.dp))
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = SurfaceDarkElevated,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = stringResource(R.string.hud_next_street, navState.streetName),
                fontSize = 13.sp,
                color = TextLight,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }

        if (navState.roundaboutExit > 0) {
          Spacer(modifier = Modifier.height(6.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = AccentYellow.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, AccentYellow.copy(alpha = 0.3f))
          ) {
            Text(
              text = stringResource(R.string.hud_roundabout_exit, navState.roundaboutExit),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = AccentYellow,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }

        if (navState.eta.isNotEmpty()) {
          Spacer(modifier = Modifier.height(10.dp))
          HorizontalDivider(color = SurfaceBorder)
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = stringResource(R.string.hud_eta_prefix, navState.eta, navState.remainingDistance),
            fontSize = 12.sp,
            color = TextMuted
          )
        }
      } else {
        // Idle Standby state
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = SurfaceDarkElevated,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = CircleShape,
              color = SurfaceBorder,
              modifier = Modifier.size(38.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.NearMe,
                  contentDescription = null,
                  tint = TextSubtle,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = stringResource(R.string.hud_standby_title),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextLight
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = stringResource(R.string.hud_idle_desc),
                fontSize = 12.sp,
                color = TextMuted,
                lineHeight = 16.sp
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun StatusBadge(isActive: Boolean) {
  val badgeBg = if (isActive) GreenMapy.copy(alpha = 0.15f) else SurfaceDarkElevated
  val badgeBorder = if (isActive) GreenMapy.copy(alpha = 0.4f) else SurfaceBorder
  val dotColor = if (isActive) GreenMapy else TextSubtle
  val textColor = if (isActive) GreenMapy else TextMuted
  val label = if (isActive) stringResource(R.string.badge_active) else stringResource(R.string.badge_inactive)

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = badgeBg,
    border = BorderStroke(1.dp, badgeBorder)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(CircleShape)
          .background(dotColor)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = label,
        color = textColor,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        softWrap = false
      )
    }
  }
}

@Composable
fun SystemBridgeCard(
  isPermissionGranted: Boolean,
  isForegroundRunning: Boolean,
  isBatteryOptimizationIgnored: Boolean,
  lastRequestTime: Long,
  onOpenPermissionSettings: () -> Unit,
  onRequestIgnoreBatteryOptimizations: () -> Unit,
  onOpenAppDetailsSettings: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, SurfaceBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = stringResource(R.string.card_system_bridge_title),
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = TextLight
      )

      Spacer(modifier = Modifier.height(12.dp))

      // 1. Notification Access Row
      BridgeStatusRow(
        icon = Icons.Default.Notifications,
        title = stringResource(R.string.status_notification_access),
        isOk = isPermissionGranted,
        statusText = if (isPermissionGranted) stringResource(R.string.status_granted) else stringResource(R.string.status_missing),
        actionLabel = if (!isPermissionGranted) stringResource(R.string.action_fix) else null,
        onAction = onOpenPermissionSettings
      )

      HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = SurfaceBorder.copy(alpha = 0.6f))

      // 2. Background Link Service Row
      BridgeStatusRow(
        icon = Icons.Default.Sync,
        title = stringResource(R.string.status_bg_service),
        isOk = isForegroundRunning,
        statusText = if (isForegroundRunning) stringResource(R.string.status_ready) else stringResource(R.string.fgs_status_starting),
        actionLabel = null,
        onAction = null
      )

      HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = SurfaceBorder.copy(alpha = 0.6f))

      // 3. Battery Protection Row
      BridgeStatusRow(
        icon = Icons.Default.BatterySaver,
        title = stringResource(R.string.status_battery_saver),
        isOk = isBatteryOptimizationIgnored,
        statusText = if (isBatteryOptimizationIgnored) stringResource(R.string.status_unrestricted) else stringResource(R.string.status_restricted),
        actionLabel = if (!isBatteryOptimizationIgnored) stringResource(R.string.action_fix) else null,
        onAction = onRequestIgnoreBatteryOptimizations
      )

      HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = SurfaceBorder.copy(alpha = 0.6f))

      // 4. Watch Endpoint & Query
      val lastReqText = if (lastRequestTime > 0) {
        val df = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        df.format(Date(lastRequestTime))
      } else {
        stringResource(R.string.status_waiting)
      }

      BridgeStatusRow(
        icon = Icons.Default.Sensors,
        title = stringResource(R.string.status_server_endpoint),
        isOk = lastRequestTime > 0,
        statusText = "127.0.0.1:8088 • $lastReqText",
        actionLabel = null,
        onAction = null
      )

      Spacer(modifier = Modifier.height(12.dp))

      // HyperOS Settings Quick Link Button
      OutlinedButton(
        onClick = onOpenAppDetailsSettings,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, SurfaceBorder)
      ) {
        Icon(
          imageVector = Icons.Default.Settings,
          contentDescription = null,
          tint = GreenMapy,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = stringResource(R.string.btn_xiaomi_settings),
          color = TextLight,
          fontSize = 12.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

@Composable
fun BridgeStatusRow(
  icon: ImageVector,
  title: String,
  isOk: Boolean,
  statusText: String,
  actionLabel: String? = null,
  onAction: (() -> Unit)? = null
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f)
    ) {
      Surface(
        shape = CircleShape,
        color = if (isOk) GreenMapy.copy(alpha = 0.15f) else AccentYellow.copy(alpha = 0.15f),
        modifier = Modifier.size(32.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = if (isOk) Icons.Default.Check else icon,
            contentDescription = null,
            tint = if (isOk) GreenMapy else AccentYellow,
            modifier = Modifier.size(16.dp)
          )
        }
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = title,
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium,
          color = TextLight,
          maxLines = 1
        )
        Text(
          text = statusText,
          fontSize = 11.sp,
          color = if (isOk) GreenMapy else AccentYellow,
          maxLines = 1
        )
      }
    }

    if (actionLabel != null && onAction != null) {
      Spacer(modifier = Modifier.width(8.dp))
      FilledTonalButton(
        onClick = onAction,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
          containerColor = AccentYellow.copy(alpha = 0.2f),
          contentColor = AccentYellow
        ),
        modifier = Modifier.height(30.dp)
      ) {
        Text(text = actionLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
fun TestSimulatorCard() {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, SurfaceBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = stringResource(R.string.card_simulator_title),
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = TextLight
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = stringResource(R.string.card_simulator_desc),
        fontSize = 12.sp,
        color = TextMuted
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Balanced 2-Column Grid
      // Row 1
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        SimButton(
          title = stringResource(R.string.test_straight),
          icon = Icons.Default.ArrowUpward,
          modifier = Modifier.weight(1f)
        ) {
          sendSim(ManeuverType.STRAIGHT, "Tovább egyenesen", "1,2 km", 1200, "M1 Autópálya")
        }
        SimButton(
          title = stringResource(R.string.test_right),
          icon = Icons.AutoMirrored.Filled.ArrowForward,
          modifier = Modifier.weight(1f)
        ) {
          sendSim(ManeuverType.TURN_RIGHT, "Forduljon jobbra", "150 m", 150, "Kossuth Lajos utca")
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Row 2
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        SimButton(
          title = stringResource(R.string.test_left),
          icon = Icons.AutoMirrored.Filled.ArrowBack,
          modifier = Modifier.weight(1f)
        ) {
          sendSim(ManeuverType.TURN_LEFT, "Forduljon balra", "80 m", 80, "Petőfi Sándor utca")
        }
        SimButton(
          title = stringResource(R.string.test_slight_right),
          icon = Icons.AutoMirrored.Filled.ArrowForward,
          modifier = Modifier.weight(1f)
        ) {
          sendSim(ManeuverType.SLIGHT_RIGHT, "Tartson enyhén jobbra", "300 m", 300, "Andrássy út")
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Row 3
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        SimButton(
          title = stringResource(R.string.test_slight_left),
          icon = Icons.AutoMirrored.Filled.ArrowBack,
          modifier = Modifier.weight(1f)
        ) {
          sendSim(ManeuverType.SLIGHT_LEFT, "Tartson enyhén balra", "250 m", 250, "Váci út")
        }
        SimButton(
          title = stringResource(R.string.test_sharp_right),
          icon = Icons.AutoMirrored.Filled.ArrowForward,
          modifier = Modifier.weight(1f)
        ) {
          sendSim(ManeuverType.SHARP_RIGHT, "Forduljon élesen jobbra", "50 m", 50, "Szent Gellért tér")
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Row 4
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        SimButton(
          title = stringResource(R.string.test_sharp_left),
          icon = Icons.AutoMirrored.Filled.ArrowBack,
          modifier = Modifier.weight(1f)
        ) {
          sendSim(ManeuverType.SHARP_LEFT, "Forduljon élesen balra", "60 m", 60, "Hegyalja út")
        }
        SimButton(
          title = stringResource(R.string.test_uturn),
          icon = Icons.Default.Refresh,
          modifier = Modifier.weight(1f)
        ) {
          sendSim(ManeuverType.U_TURN, "Forduljon vissza", "100 m", 100, "Hungária körút")
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Row 5
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        SimButton(
          title = stringResource(R.string.test_roundabout),
          icon = Icons.Default.Refresh,
          modifier = Modifier.weight(1f)
        ) {
          sendSim(ManeuverType.ROUNDABOUT, "Körforgalom 2. kijárat", "200 m", 200, "Bécsi út", roundaboutExit = 2)
        }
        SimButton(
          title = stringResource(R.string.test_destination),
          icon = Icons.Default.Flag,
          accentColor = AccentYellow,
          modifier = Modifier.weight(1f)
        ) {
          sendSim(ManeuverType.DESTINATION_REACHED, "Megérkezett a célhoz", "0 m", 0, "Úti cél elérve")
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Row 6
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        SimButton(
          title = stringResource(R.string.test_unknown),
          icon = Icons.Default.NearMe,
          modifier = Modifier.weight(1f)
        ) {
          sendSim(ManeuverType.UNKNOWN, "Kövesse az útvonalat", "500 m", 500, "Gyalogút")
        }
        SimButton(
          title = stringResource(R.string.test_stop),
          icon = Icons.Default.Stop,
          accentColor = AccentRed,
          modifier = Modifier.weight(1f)
        ) {
          MapyNotificationListenerService.updateSimulatedState(NavState.IDLE)
        }
      }
    }
  }
}

@Composable
fun SimButton(
  title: String,
  icon: ImageVector,
  modifier: Modifier = Modifier,
  accentColor: Color? = null,
  onClick: () -> Unit
) {
  val borderStroke = if (accentColor != null) {
    BorderStroke(1.dp, accentColor.copy(alpha = 0.5f))
  } else {
    BorderStroke(1.dp, SurfaceBorder)
  }
  val contentColor = accentColor ?: TextLight

  Surface(
    shape = RoundedCornerShape(10.dp),
    color = SurfaceDarkElevated,
    border = borderStroke,
    modifier = modifier
      .height(42.dp)
      .clip(RoundedCornerShape(10.dp))
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = contentColor,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = title,
        color = contentColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

private fun sendSim(
  maneuver: ManeuverType,
  direction: String,
  distText: String,
  distMeters: Int,
  street: String,
  roundaboutExit: Int = 0
) {
  MapyNotificationListenerService.updateSimulatedState(
    NavState(
      isActive = true,
      maneuver = maneuver,
      directionText = direction,
      distanceText = distText,
      distanceMeters = distMeters,
      streetName = street,
      roundaboutExit = roundaboutExit,
      eta = "14:45",
      remainingDistance = "3,5 km"
    )
  )
}

private fun getManeuverIcon(maneuver: ManeuverType): ImageVector {
  return when (maneuver) {
    ManeuverType.STRAIGHT -> Icons.Default.ArrowUpward
    ManeuverType.TURN_RIGHT, ManeuverType.SLIGHT_RIGHT, ManeuverType.SHARP_RIGHT -> Icons.AutoMirrored.Filled.ArrowForward
    ManeuverType.TURN_LEFT, ManeuverType.SLIGHT_LEFT, ManeuverType.SHARP_LEFT -> Icons.AutoMirrored.Filled.ArrowBack
    ManeuverType.U_TURN -> Icons.Default.Refresh
    ManeuverType.ROUNDABOUT -> Icons.Default.Sync
    ManeuverType.DESTINATION_REACHED -> Icons.Default.Flag
    ManeuverType.UNKNOWN -> Icons.Default.NearMe
  }
}
