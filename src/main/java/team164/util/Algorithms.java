/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164.util;

import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

/**
 * A static library of useful algorithms.
 *
 * @author Yang Yang
 */
public final class Algorithms {

  private Algorithms() {
  }

  /**
   * Returns the number of robots of the specified team by type. The number of
   * robots of type {@code t} is {@code count[t.ordinal()]}.
   *
   * @param robots robots to count
   * @param team team to filter by
   * @return number of robots by type
   */
  public static int[] getRobotCount(RobotInfo[] robots, Team team) {
    int[] count = new int[RobotType.values().length];
    for (int i = robots.length; --i >= 0;) {
      if (robots[i].team == team) {
        ++count[robots[i].type.ordinal()];
      }
    }
    return count;
  }
}
