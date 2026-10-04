package hu.maci.mapynav.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val GreenMapy = Color(0xFF22C55E)
val GreenMapyDark = Color(0xFF16A34A)
val DarkBackground = Color(0xFF0A0F1D)
val SurfaceDark = Color(0xFF131A2B)
val SurfaceDarkElevated = Color(0xFF1C253B)
val SurfaceBorder = Color(0xFF23304B)
val TextLight = Color(0xFFF8FAFC)
val TextMuted = Color(0xFF94A3B8)
val TextSubtle = Color(0xFF64748B)
val AccentYellow = Color(0xFFF59E0B)
val AccentRed = Color(0xFFEF4444)
val AccentBlue = Color(0xFF38BDF8)

private val DarkColorScheme = darkColorScheme(
  primary = GreenMapy,
  secondary = AccentYellow,
  tertiary = AccentBlue,
  background = DarkBackground,
  surface = SurfaceDark,
  surfaceVariant = SurfaceDarkElevated,
  outline = SurfaceBorder,
  onPrimary = Color.Black,
  onSecondary = Color.Black,
  onBackground = TextLight,
  onSurface = TextLight,
  onSurfaceVariant = TextMuted
)

@Composable
fun MapyNavTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    content = content
  )
}

