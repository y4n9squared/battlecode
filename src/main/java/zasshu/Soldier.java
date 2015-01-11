/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.AbstractRobot;
import zasshu.core.Controller;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public final class Soldier extends AbstractRobot {

  public Soldier(Controller c) {
    super(c);
  }

  @Override protected void runHelper() {
    if (getInfluence(controller.getLocation()) > 0) {
      if (controller.isWeaponReady()) {
        // TODO: Attack intelligently
      }
      if (controller.isCoreReady()) {
        // TODO: Move according to potential gradient. Gradient should be
        // direction of engagement.
        //
        // There's actually a troubling issue with this strategy. If potential
        // brings you into range of an enemy, influence could be negative next
        // round. It's possible the robot will just alternate between attacking
        // and retreating mindlessly until allies catch up.
      }
    } else {
      // TODO: Move according to influence gradient. Gradient should be the
      // direction of retreat.
    }
  }

  private double getInfluence(MapLocation loc) {
    // TODO: Implement
    return 0;
  }

  private double getPotential(MapLocation loc) {
    double positive = 0;
    double negative = 0;
    RobotInfo[] robots = controller.getNearbyRobots();
    for (int i = robots.length; --i >= 0;) {
      double force = computeForce(loc, robots[i]);
      if (force > 0) {
        positive = Math.max(positive, force);
      } else {
        negative += force;
      }
    }
    return positive + negative;
  }

  private double computeForce(MapLocation loc, RobotInfo robot) {
    double potential = 0;
    int d = loc.distanceSquaredTo(robot.location);
    if (robot.team == controller.getOpponentTeam()) {
      switch (robot.type) {
        case BEAVER:
        case SOLDIER:
        case BASHER:
          return computePositiveForce(d);
        case TOWER:
          return 10 * computePositiveForce(d);
        default:
          return 0;
      }
    }
    return computeNegativeForce(d);
  }

  private double computePositiveForce(int d) {
    // TODO use attackRadiusSquared
    return 10.0 / (Math.abs(d - 4.0) + 1);
  }

  private double computeNegativeForce(int d) {
    return -5.0 / d;
  }
}
