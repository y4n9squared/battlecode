/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import battlecode.common.Direction;
import battlecode.common.MapLocation;

/**
 * Navigation interface.
 *
 * @author Yang Yang
 */
public interface Navigator {

  /**
   * Returns the direction to move in order to reach the destination.
   *
   * @param location the current MapLocation of the robot
   * @param heading the current Direction the robot is facing
   * @return the Direction in which the robot should move
   * @see #setDestination(MapLocation)
   */
  Direction getNextStep(MapLocation location);
}
