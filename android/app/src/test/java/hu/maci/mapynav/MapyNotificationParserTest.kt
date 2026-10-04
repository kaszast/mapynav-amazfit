package hu.maci.mapynav

import hu.maci.mapynav.model.ManeuverType
import hu.maci.mapynav.parser.MapyNotificationParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MapyNotificationParserTest {

  @Test
  fun testHungarianTurnRight() {
    val state = MapyNotificationParser.parse(
      title = "150 m múlva forduljon jobbra",
      text = "Kossuth Lajos utca",
      subText = "14:35 • 3,2 km"
    )

    assertTrue(state.isActive)
    assertEquals(ManeuverType.TURN_RIGHT, state.maneuver)
    assertEquals(150, state.distanceMeters)
    assertEquals("Kossuth Lajos utca", state.streetName)
    assertEquals("14:35", state.eta)
  }

  @Test
  fun testHungarianSlightLeftWithKilometers() {
    val state = MapyNotificationParser.parse(
      title = "1.5 km múlva enyhén balra",
      text = "M1 Autópálya",
      subText = "15:10 • 42 km"
    )

    assertTrue(state.isActive)
    assertEquals(ManeuverType.SLIGHT_LEFT, state.maneuver)
    assertEquals(1500, state.distanceMeters)
    assertEquals("M1 Autópálya", state.streetName)
  }

  @Test
  fun testHungarianRoundaboutExit() {
    val state = MapyNotificationParser.parse(
      title = "200 m múlva a körforgalomból 2. kijárat",
      text = "Bécsi út",
      subText = "ETA 12:40"
    )

    assertTrue(state.isActive)
    assertEquals(ManeuverType.ROUNDABOUT, state.maneuver)
    assertEquals(2, state.roundaboutExit)
    assertEquals(200, state.distanceMeters)
  }

  @Test
  fun testCzechNavigationFormat() {
    val state = MapyNotificationParser.parse(
      title = "Za 300 m odbočte vlevo",
      text = "Národní třída",
      subText = "11:20"
    )

    assertTrue(state.isActive)
    assertEquals(ManeuverType.TURN_LEFT, state.maneuver)
    assertEquals(300, state.distanceMeters)
    assertEquals("Národní třída", state.streetName)
  }

  @Test
  fun testDestinationReached() {
    val state = MapyNotificationParser.parse(
      title = "Megérkezett a célhoz",
      text = "Végállomás",
      subText = ""
    )

    assertTrue(state.isActive)
    assertEquals(ManeuverType.DESTINATION_REACHED, state.maneuver)
  }

  @Test
  fun testIdleStateWhenEmpty() {
    val state = MapyNotificationParser.parse(null, null, null)
    assertTrue(!state.isActive)
  }
}
