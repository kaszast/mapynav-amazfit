package hu.maci.mapynav.model

data class NavState(
  val isActive: Boolean = false,
  val maneuver: ManeuverType = ManeuverType.UNKNOWN,
  val directionText: String = "",
  val distanceText: String = "",
  val distanceMeters: Int = 0,
  val streetName: String = "",
  val eta: String = "",
  val remainingDistance: String = "",
  val remainingTime: String = "",
  val roundaboutExit: Int = 0,
  val timestamp: Long = System.currentTimeMillis()
) {
  fun toJson(): String {
    val escapedDir = escapeJson(directionText)
    val escapedDist = escapeJson(distanceText)
    val escapedStreet = escapeJson(streetName)
    val escapedEta = escapeJson(eta)
    val escapedRemDist = escapeJson(remainingDistance)
    val escapedRemTime = escapeJson(remainingTime)

    return """
      {
        "active": $isActive,
        "action": "${maneuver.name}",
        "iconKey": "${maneuver.iconKey}",
        "directionText": "$escapedDir",
        "distance": "$escapedDist",
        "distanceMeters": $distanceMeters,
        "street": "$escapedStreet",
        "eta": "$escapedEta",
        "remainingDistance": "$escapedRemDist",
        "remainingTime": "$escapedRemTime",
        "roundaboutExit": $roundaboutExit,
        "vibePattern": ${maneuver.defaultVibePattern},
        "timestamp": $timestamp
      }
    """.trimIndent()
  }

  private fun escapeJson(value: String): String {
    return value
      .replace("\\", "\\\\")
      .replace("\"", "\\\"")
      .replace("\b", "\\b")
      .replace("\n", "\\n")
      .replace("\r", "\\r")
      .replace("\t", "\\t")
  }

  companion object {
    val IDLE = NavState(isActive = false)
  }
}
