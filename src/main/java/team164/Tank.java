/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164;

import team164.core.AbstractRobot;
import team164.core.Channels;
import team164.core.Controller;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

/**
 * AI for the Tank robot type.
 *
 * @author Holman Gao
 * @author Yang Yang
 */
public final class Tank extends AbstractRobot {

  private static final RobotType ROBOT_TYPE = RobotType.SOLDIER;

  /**
   * This is the order in which the tank attacks enemys.
   */
  private enum AttackPriority {
    TOWER, HQ, MISSILE, LAUNCHER, COMMANDER, TANK, DRONE, BASHER, SOLDIER,
    MINER, BEAVER, COMPUTER, AEROSPACELAB, BARRACKS, HELIPAD, MINERFACTORY,
    SUPPLYDEPOT, TANKFACTORY, TECHNOLOGYINSTITUTE, TRAININGFIELD,
    HANDWASHSTATION;
  }

  /**
   * Constructs a {@code Tank} object.
   *
   * @param c controller
   */
  public Tank(Controller c) {
    super(c);
  }

  @Override protected void runHelper() {
    if (controller.isWeaponReady()) {
      RobotInfo[] enemies = controller.getNearbyRobots(
          ROBOT_TYPE.attackRadiusSquared,
          controller.getOpponentTeam());

      RobotInfo target = null;
      int maxPriority = AttackPriority.HANDWASHSTATION.ordinal();

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
      Direction maxDir = Direction.NONE;

      MapLocation[] targets = getAttackTargets();
      MapLocation target = readMapLocationBroadcast(Channels.TARGET_LOCATION);

      RobotInfo[] enemies = controller.getNearbyRobots(
          ROBOT_TYPE.sensorRadiusSquared,
          controller.getOpponentTeam());

      int attackDistance = controller.readBroadcast(Channels.ATTACK_DISTANCE);

      for (int i = 8; --i >= 0;) {
        Direction dir = myLoc.directionTo(locs[i]);
        if (controller.canMove(dir)) {
          double potential = 0.0;
          boolean badDir = false;
          MapLocation loc = locs[i];

          for (int j = targets.length; --j >= 0;) {
            MapLocation possibleTarget = targets[j];
            int distanceToTarget = loc.distanceSquaredTo(possibleTarget);

            if (targets[j].equals(target)) {
              potential +=
                50 * computePositiveForce(distanceToTarget, attackDistance);

              if (distanceToTarget <= attackDistance) {
                badDir = true;
                break;
              }
            } else if (distanceToTarget <= 24) {
              badDir = true;
              break;
            }
          }
          if (badDir) {
            continue;
          }

          for (int j = enemies.length; --j >= 0;) {
            double force = computeForce(loc, enemies[j]);
            potential = Math.max(potential, force);
          }

          if (maxPotential < potential) {
            maxPotential = potential;
            maxDir = dir;
          }
        }
      }

      controller.move(maxDir);
    }
    // TODO: Add a retreat strategy that moves according to influence gradient.
    // Gradient should be the direction of retreat.
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
        default:
          return 0;
      }
    }
    return computeNegativeForce(d);
  }

  private double computePositiveForce(int d) {
    return ROBOT_TYPE.attackRadiusSquared
        / (Math.abs(d - ROBOT_TYPE.attackRadiusSquared) + 1);
  }

  private double computePositiveForce(int d, int optimalDistance) {
    return ROBOT_TYPE.attackRadiusSquared
        / (Math.abs(d - optimalDistance) + 1.0);
  }

  private double computeNegativeForce(int d) {
    return -5.0 / d;
  }
}
