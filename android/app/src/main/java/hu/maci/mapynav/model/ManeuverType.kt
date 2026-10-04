package hu.maci.mapynav.model

enum class ManeuverType(val iconKey: String, val defaultVibePattern: Int) {
  STRAIGHT("straight", 1),
  TURN_LEFT("turn_left", 2),
  TURN_RIGHT("turn_right", 3),
  SLIGHT_LEFT("slight_left", 4),
  SLIGHT_RIGHT("slight_right", 5),
  SHARP_LEFT("sharp_left", 6),
  SHARP_RIGHT("sharp_right", 7),
  U_TURN("u_turn", 8),
  ROUNDABOUT("roundabout", 9),
  DESTINATION_REACHED("destination", 10),
  UNKNOWN("unknown", 0);

  companion object {
    fun fromString(value: String): ManeuverType {
      return entries.firstOrNull { it.name.equals(value, ignoreCase = true) || it.iconKey.equals(value, ignoreCase = true) }
        ?: UNKNOWN
    }
  }
}
