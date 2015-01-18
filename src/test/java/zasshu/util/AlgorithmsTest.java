/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.util;

import static org.junit.Assert.*;
import static zasshu.util.Algorithms.*;

import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

import org.junit.*;

/**
 * Unit tests for {@link Algorithms}.
 *
 * @author Yang Yang
 */
public final class AlgorithmsTest {

  @Test public void testGetRobotCount() {
    RobotInfo[] robots = new RobotInfo[] {
      new RobotInfo(1, Team.A, RobotType.HQ, new MapLocation(0, 0),
        0.0, 0.0, 100.0, 0.0, 0, 0, null, null),
      new RobotInfo(1, Team.A, RobotType.SOLDIER, new MapLocation(0, 1), 0.0,
          0.0, 100.0, 0.0, 0, 0, null, null),
      new RobotInfo(1, Team.A, RobotType.SOLDIER, new MapLocation(1, 0), 0.0,
          0.0, 100.0, 0.0, 0, 0, null, null)
    };
    int[] count = getRobotCount(robots, Team.A);
    assertEquals("Incorrect HQ count.", 1, count[RobotType.HQ.ordinal()]);
    assertEquals(
        "Incorrect SOLDIER count.", 2, count[RobotType.SOLDIER.ordinal()]);
    assertEquals(
        "Incorrect BEAVER count.", 0, count[RobotType.BEAVER.ordinal()]);
  }
}
