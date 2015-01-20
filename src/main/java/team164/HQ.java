/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164;

import team164.core.AbstractRobot;
import team164.core.Channels;
import team164.core.Controller;

import battlecode.common.Clock;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public final class HQ extends AbstractRobot {

  /**
   * The number of beavers the HQ will try to maintain on the map. This should
   * be set at minimum to 1, so that we can build structures, but not too large
   * as to waste ore.
   */
  private static final int NUM_BEAVER_TARGET = 2;

  /**
   * The distance at which to swarm the attack target.
   */
  private static final int SWARM_RADIUS_SQUARED =
      RobotType.HQ.sensorRadiusSquared;

  /**
   * The round on which to start attacking.
   */
  private static final int MIN_ATTACK_ROUND = 500;

  private int attackDistance = SWARM_RADIUS_SQUARED;
  private MapLocation currentTarget = null;
  private int numTowers = -1;

  /**
   * This is the order in which the HQ attacks enemys.
   */
  private enum AttackPriority {
    MISSILE, LAUNCHER, COMMANDER, TANK, DRONE, BASHER, SOLDIER, MINER, BEAVER,
    COMPUTER;
  }

  /**
   * Constructs the HQ.
   *
   * @param controller controller object
   */
  public HQ(Controller controller) {
    super(controller);
  }

  @Override protected void runHelper() {
    if (controller.isWeaponReady()) {
      RobotInfo[] enemies = controller.getNearbyRobots(
          RobotType.HQ.attackRadiusSquared, controller.getOpponentTeam());

      RobotInfo target = null;
      int maxPriority = AttackPriority.COMPUTER.ordinal();

      for (int i = enemies.length; --i >= 0;) {
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
      // Check for the number of beavers on the map that we own.
      if (Clock.getRoundNum() % RobotType.BEAVER.buildTurns == 1) {
        int numBeavers = controller.readBroadcast(Channels.NUM_BEAVERS);
        if (numBeavers < NUM_BEAVER_TARGET) {
          controller.spawn(getEnemyHQDirection(), RobotType.BEAVER);
        }
        controller.broadcast(Channels.NUM_BEAVERS, 0);
      }
    }

    computeAttackTarget();

    RobotInfo[] teammatesAroundTarget = controller.getNearbyRobots(
        currentTarget,
        SWARM_RADIUS_SQUARED + 20,
        controller.getTeam());

    if (Clock.getRoundNum() < MIN_ATTACK_ROUND) {
      attackDistance =
          controller.getLocation().distanceSquaredTo(currentTarget) - 24;
    } else if (teammatesAroundTarget.length >= 12) {
      attackDistance = 0;
    } else if (teammatesAroundTarget.length < 5) {
      attackDistance = SWARM_RADIUS_SQUARED;
    }
    int existingAttackDistance =
        controller.readBroadcast(Channels.ATTACK_DISTANCE);
    if (existingAttackDistance != attackDistance) {
      controller.broadcast(Channels.ATTACK_DISTANCE, attackDistance);
    }
  }

  private void computeAttackTarget() {
    MapLocation[] enemyTowers = controller.getEnemyTowerLocations();

    if (numTowers == enemyTowers.length) {
      return;
    }

    MapLocation locToSearchAround;
    if (numTowers == -1) {
      locToSearchAround = controller.getLocation();
    } else {
      locToSearchAround = currentTarget;
    }
    numTowers = enemyTowers.length;

    int targetIndex = 0;
    if (numTowers > 0) {
      double closestDistance = Double.POSITIVE_INFINITY;
      for (int i = numTowers; --i >= 0;) {
        double distance = locToSearchAround.distanceSquaredTo(enemyTowers[i]);
        if (distance < closestDistance) {
          closestDistance = distance;
          targetIndex = i + 1;
        }
      }
      currentTarget = enemyTowers[targetIndex - 1];
    } else {
      currentTarget = controller.getEnemyHQLocation();
    }
    controller.broadcast(Channels.ATTACK_TARGET_INDEX, targetIndex);
  }
}
