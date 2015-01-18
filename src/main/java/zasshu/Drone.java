/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.AbstractRobot;
import zasshu.core.Channels;
import zasshu.core.Controller;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

/**
 * AI for the Drone robot type.
 *
 * @author Holman Gao
 * @author Yang Yang
 */
public final class Drone extends AbstractRobot {

  private static final RobotType ROBOT_TYPE = RobotType.DRONE;

  /**
   * This is the order in which the drone attacks enemys.
   */
  private enum AttackPriority {
    MISSILE, TOWER, HQ, LAUNCHER, COMMANDER, TANK, DRONE, BASHER, SOLDIER,
    MINER, BEAVER, COMPUTER, AEROSPACELAB, BARRACKS, HELIPAD, MINERFACTORY,
    SUPPLYDEPOT, TANKFACTORY, TECHNOLOGYINSTITUTE, TRAININGFIELD,
    HANDWASHSTATION;
  }

  /**
   * Constructs a {@code Drone} object.
   *
   * @param c controller
   */
  public Drone(Controller c) {
    super(c);
  }

  @Override protected void runHelper() {
    if (controller.isWeaponReady()) {
      RobotInfo[] enemies = controller.getNearbyRobots(
          ROBOT_TYPE.attackRadiusSquared,
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
      Direction maxDir = Direction.NONE;

      MapLocation[] towers = controller.getEnemyTowerLocations();
      MapLocation target = new MapLocation(
          controller.readBroadcast(Channels.ATTACK_TARGET_X),
          controller.readBroadcast(Channels.ATTACK_TARGET_Y));

      RobotInfo[] enemies = controller.getNearbyRobots(
          ROBOT_TYPE.sensorRadiusSquared,
          controller.getOpponentTeam());

      int attackDistance = controller.readBroadcast(Channels.ATTACK_DISTANCE);

      for (int i = 8; --i >= 0;) {
        Direction dir = myLoc.directionTo(locs[i]);
        if (controller.canMove(dir)) {
          int distanceToTarget = locs[i].distanceSquaredTo(target);
          double potential = 10 * computePositiveForce(distanceToTarget);

          if (distanceToTarget <= attackDistance) {
            continue;
          }

          for (int j = enemies.length; --j >= 0;) {
            double force = computeForce(locs[i], enemies[j]);
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
    propogateSupply();
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
    return ROBOT_TYPE.attackRadiusSquared / (Math.abs(d - 4.0) + 1);
  }

  private double computeNegativeForce(int d) {
    return -5.0 / d;
  }
}
