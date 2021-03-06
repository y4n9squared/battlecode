/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164;

import static team164.util.Algorithms.*;

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

  private static final RobotType ROBOT_TYPE = RobotType.TANK;

  private RobotInfo[] enemies;

  /**
   * This is the order in which the tank attacks enemys.
   */
  private enum AttackPriority {
    MISSILE, TOWER, HQ, LAUNCHER, COMMANDER, TANK, DRONE, BASHER, SOLDIER,
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
    RobotInfo[] attackable = controller.getNearbyRobots(
        ROBOT_TYPE.attackRadiusSquared,
        controller.getOpponentTeam());

    if (attackable.length > 0) {
      if (controller.isWeaponReady()) {
        RobotInfo target = null;
        int maxPriority = AttackPriority.HANDWASHSTATION.ordinal();

        for (int i = attackable.length; --i >= 0;) {
          // Calling Enum.valueOf is potentially dangerous here - if the enemy
          // RobotType.toString conversion does not match an AttackPriority
          // enum, the method will throw IllegalArgumentException.
          int p =
              AttackPriority.valueOf(attackable[i].type.toString()).ordinal();
          if (target == null || p < maxPriority
              || (p == maxPriority && attackable[i].health > target.health)) {
            target = attackable[i];
          }
        }
        if (target != null) {
          controller.attack(target);
        }
      }
    } else if (controller.isCoreReady()) {
      enemies = controller.getNearbyRobots(
        ROBOT_TYPE.sensorRadiusSquared,
        controller.getOpponentTeam());

      if (enemies.length > 0) {
        enemies = controller.getNearbyRobots(
          ROBOT_TYPE.sensorRadiusSquared + 10,
          controller.getOpponentTeam());
      }

      if (enemies.length > 0) {
        useBugNavigator = false;
      }

      boolean attacking = mySpawnRound
          < controller.readBroadcast(Channels.ATTACKERS_MAX_SPAWN_ROUND);

      int channel = attacking ? Channels.ATTACK_TARGET
          : Channels.DEFENSE_TARGET;

      MapLocation newTarget = intToLocation(
          controller.readBroadcast(channel),
          controller.getHQLocation());
      int newAttackDistance = attacking
          ? controller.readBroadcast(Channels.ATTACK_DISTANCE) : 1;

      if (!newTarget.equals(target) || newAttackDistance != attackDistance) {
        useBugNavigator = false;
        target = newTarget;
        attackDistance = newAttackDistance;
      } else if (myLoc.distanceSquaredTo(target) < bugInitialDistanceSquared) {
        useBugNavigator = false;
      }

      if (useBugNavigator) {
        if (!moveLikeABug()) {
          useBugNavigator = false;
        }
      }

      if (!useBugNavigator) {
        moveWithPotential();
      }
    }
    // TODO: Add a retreat strategy that moves according to influence gradient.
    // Gradient should be the direction of retreat.
  }

  private void moveWithPotential() {
    MapLocation[] locs = getTraversableAdjacentMapLocations(false);
    double maxPotential = Double.NEGATIVE_INFINITY;
    Direction maxDir = Direction.NONE;

    MapLocation[] targets = getAttackTargets();

    for (int i = locs.length; --i >= 0;) {
      MapLocation loc = locs[i];
      Direction dir = myLoc.directionTo(loc);
      double potential = 0.0;
      boolean badDir = false;

      // Add computations for target
      potential +=
        computeTargetForce(loc.distanceSquaredTo(target), attackDistance);

      // Add computations for targets
      for (int j = targets.length; --j >= 0;) {
        MapLocation possibleTarget = targets[j];
        int distanceToTarget = loc.distanceSquaredTo(possibleTarget);

        if (targets[j].equals(target)) {
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

    if (!controller.move(maxDir)
        && enemies.length == 0
        && myLoc.distanceSquaredTo(target) >= attackDistance + 2) {
      startBugNavigation();
    }
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

  private double computeTargetForce(int d, int optimalDistance) {
    return 5 * ROBOT_TYPE.attackRadiusSquared
        / (Math.abs(d - optimalDistance) + 1.0);
  }

  private double computeNegativeForce(int d) {
    return -5.0 / d;
  }
}
