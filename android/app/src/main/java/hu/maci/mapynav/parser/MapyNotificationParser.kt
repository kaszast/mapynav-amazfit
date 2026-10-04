package hu.maci.mapynav.parser

import hu.maci.mapynav.model.ManeuverType
import hu.maci.mapynav.model.NavState
import java.util.Locale
import java.util.regex.Pattern

object MapyNotificationParser {

  private val DISTANCE_PATTERN = Pattern.compile("(\\d+(?:[,.]\\d+)?)\\s*(m|km|méter|kilométer)", Pattern.CASE_INSENSITIVE)
  private val ROUNDABOUT_PATTERN = Pattern.compile("(?:körforgalom.*|kruhový objezd.*|roundabout.*)?([1-8])(?:[.\\s]+(?:kijárat|výjezd|exit)|\\.\\s*kijárat)", Pattern.CASE_INSENSITIVE)
  private val ETA_PATTERN = Pattern.compile("(\\d{1,2}:\\d{2})")

  fun parse(
    title: String?,
    text: String?,
    subText: String?
  ): NavState {
    val fullTitle = title?.trim() ?: ""
    val fullText = text?.trim() ?: ""
    val fullSub = subText?.trim() ?: ""

    if (fullTitle.isEmpty() && fullText.isEmpty()) {
      return NavState.IDLE
    }

    val combined = "$fullTitle $fullText".lowercase(Locale.ROOT)

    val maneuver = detectManeuver(combined)
    val roundaboutExit = detectRoundaboutExit(combined)
    val distanceInfo = extractDistance(fullTitle, fullText)
    val eta = extractEta(fullSub, fullText)

    // Street is typically in fullText if fullTitle contained distance/maneuver, or vice versa
    val street = extractStreet(fullTitle, fullText)

    return NavState(
      isActive = true,
      maneuver = maneuver,
      directionText = fullTitle.ifEmpty { maneuver.name },
      distanceText = distanceInfo.first,
      distanceMeters = distanceInfo.second,
      streetName = street,
      eta = eta,
      remainingDistance = fullSub,
      roundaboutExit = roundaboutExit,
      timestamp = System.currentTimeMillis()
    )
  }

  internal fun detectManeuver(text: String): ManeuverType {
    // Roundabout
    if (text.contains("körforgalom") || text.contains("kruhový objezd") || text.contains("roundabout")) {
      return ManeuverType.ROUNDABOUT
    }

    // Destination / Arrival
    if (text.contains("megérkez") || text.contains("célhoz") || text.contains("cíl") || text.contains("destination") || text.contains("arrived")) {
      return ManeuverType.DESTINATION_REACHED
    }

    // U-turn
    if (text.contains("vissza") || text.contains("otočte se") || text.contains("u-turn")) {
      return ManeuverType.U_TURN
    }

    // Sharp turns
    if (text.contains("élesen balra") || text.contains("ostře vlevo") || text.contains("sharp left")) {
      return ManeuverType.SHARP_LEFT
    }
    if (text.contains("élesen jobbra") || text.contains("ostře vpravo") || text.contains("sharp right")) {
      return ManeuverType.SHARP_RIGHT
    }

    // Slight turns / Keep lane
    if (text.contains("enyhén balra") || text.contains("tarts balra") || text.contains("mírně vlevo") || text.contains("držte se vlevo") || text.contains("slight left") || text.contains("keep left")) {
      return ManeuverType.SLIGHT_LEFT
    }
    if (text.contains("enyhén jobbra") || text.contains("tarts jobbra") || text.contains("mírně vpravo") || text.contains("držte se vpravo") || text.contains("slight right") || text.contains("keep right")) {
      return ManeuverType.SLIGHT_RIGHT
    }

    // Standard turns
    if (text.contains("balra") || text.contains("vlevo") || text.contains("left")) {
      return ManeuverType.TURN_LEFT
    }
    if (text.contains("jobbra") || text.contains("vpravo") || text.contains("right")) {
      return ManeuverType.TURN_RIGHT
    }

    // Straight
    if (text.contains("egyenesen") || text.contains("rovně") || text.contains("straight") || text.contains("tovább")) {
      return ManeuverType.STRAIGHT
    }

    return ManeuverType.UNKNOWN
  }

  private fun detectRoundaboutExit(text: String): Int {
    val matcher = ROUNDABOUT_PATTERN.matcher(text)
    if (matcher.find()) {
      return matcher.group(1)?.toIntOrNull() ?: 0
    }
    return 0
  }

  private fun extractDistance(title: String, text: String): Pair<String, Int> {
    var matcher = DISTANCE_PATTERN.matcher(title)
    if (matcher.find()) {
      val valueStr = matcher.group(1)?.replace(",", ".") ?: "0"
      val unit = matcher.group(2)?.lowercase(Locale.ROOT) ?: "m"
      val value = valueStr.toDoubleOrNull() ?: 0.0
      val meters = if (unit.startsWith("k")) (value * 1000).toInt() else value.toInt()
      return Pair("${matcher.group(1)} $unit", meters)
    }

    matcher = DISTANCE_PATTERN.matcher(text)
    if (matcher.find()) {
      val valueStr = matcher.group(1)?.replace(",", ".") ?: "0"
      val unit = matcher.group(2)?.lowercase(Locale.ROOT) ?: "m"
      val value = valueStr.toDoubleOrNull() ?: 0.0
      val meters = if (unit.startsWith("k")) (value * 1000).toInt() else value.toInt()
      return Pair("${matcher.group(1)} $unit", meters)
    }

    return Pair("", 0)
  }

  private fun extractEta(subText: String, text: String): String {
    val m1 = ETA_PATTERN.matcher(subText)
    if (m1.find()) return m1.group(1) ?: ""

    val m2 = ETA_PATTERN.matcher(text)
    if (m2.find()) return m2.group(1) ?: ""

    return ""
  }

  private fun extractStreet(title: String, text: String): String {
    // If title has the distance or turn, text is usually the street
    if (DISTANCE_PATTERN.matcher(title).find()) {
      return text
    }
    if (DISTANCE_PATTERN.matcher(text).find()) {
      return title
    }
    return text.ifEmpty { title }
  }
}
