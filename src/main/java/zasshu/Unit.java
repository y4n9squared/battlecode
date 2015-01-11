/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.AbstractRobot;
import zasshu.core.Controller;

import battlecode.common.Direction;
import battlecode.common.MapLocation;

public abstract class Unit extends AbstractRobot {

  public Unit(Controller c) {
    super(c);
  }

  /**
   * Returns the direction of the maximum potential gradient.
   *
   * @param loc current location
   */
  protected Direction getNextStep(
      MapLocation loc, Direction[] possibleDirections) {

    double maxPotential = Double.NEGATIVE_INFINITY;
    int idx = 0;
    for (int i = 8; --i >= 0;) {
      Direction dir = possibleDirections[i];

      if (dir == Direction.NONE) {
        continue;
      }

      double potential = getPotential(loc.add(dir));
      if (potential > maxPotential) {
        maxPotential = potential;
        idx = i;
      }
    }
    return possibleDirections[idx];
  }

  protected double getPotential(MapLocation loc) {
    return 0.0;
  }
}
