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

  /**
   * This is the order in which the soldier attacks enemys.
   */
  private enum AttackPriority {
    LAUNCHER, COMMANDER, TANK, DRONE, BASHER, SOLDIER, MINER, BEAVER, COMPUTER,
    MISSILE;
  }

  public Soldier(Controller c) {
    super(c);
  }

  @Override protected void runHelper() {
    if (getInfluence(controller.getLocation()) > 0) {
      if (controller.isWeaponReady()) {
        RobotInfo[] enemies = controller.getNearbyRobots(
            RobotType.SOLDIER.attackRadiusSquared,
            controller.getOpponentTeam());

        RobotInfo target = null;
        int maxPriority = AttackPriority.MISSILE.ordinal();

        for (int i = enemies.length; --i >= 0;) {
          // Calling Enum.valueOf is potentially dangerous here - if the enemy
          // RobotType.toString conversion does not match an AttackPriority
          // enum, the method will throw IllegalArgumentException.
          int p = AttackPriority.valueOf(enemies[i].type.toString()).ordinal();
          if (target == null || p < maxPriority
              || (p == maxPriority && enemies[i].health > target.health)) {
            target = enemies[i];
          }
        }
        if (target != null) {
          controller.attack(target);
        }
      }
      if (controller.isCoreReady()) {
        MapLocation myLoc = controller.getLocation();
        MapLocation[] locs =
            MapLocation.getAllMapLocationsWithinRadiusSq(myLoc, 2);
        double maxPotential = Double.NEGATIVE_INFINITY;
        MapLocation maxLoc = myLoc;

        for (int i = 8; --i >= 0;) {
          if (getInfluence(locs[i]) > 0) {
            double potential = getPotential(locs[i]);

            if (maxPotential < potential) {
              maxPotential = potential;
              maxLoc = locs[i];
            }
          }
        }

        Direction dir = myLoc.directionTo(maxLoc);
        controller.move(dir);
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
