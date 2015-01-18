/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.AbstractRobot;
import zasshu.core.Channels;
import zasshu.core.Controller;

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
  private static final int NUM_BEAVER_TARGET = 3;

  /**
   * The distance at which to swarm the attack target.
   */
  private static final int SWARM_RADIUS_SQUARED =
      RobotType.HQ.sensorRadiusSquared;

  private int attackDistance = SWARM_RADIUS_SQUARED;

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

    MapLocation[] enemyTowers = controller.getEnemyTowerLocations();
    MapLocation target;
    if (enemyTowers.length > 0) {
      MapLocation myLoc = controller.getLocation();
      double closestDistance = Double.POSITIVE_INFINITY;
      int closestIndex = 0;
      for (int i = enemyTowers.length; --i >= 0;) {
        double distance = myLoc.distanceSquaredTo(enemyTowers[i]);
        if (distance < closestDistance) {
          closestDistance = distance;
          closestIndex = i;
        }
      }

      target = enemyTowers[closestIndex];
    } else {
      target = controller.getEnemyHQLocation();
    }

    int existingTargetX = controller.readBroadcast(Channels.ATTACK_TARGET_X);
    if (target.x != existingTargetX) {
      controller.broadcast(Channels.ATTACK_TARGET_X, target.x);
    }
    int existingTargetY = controller.readBroadcast(Channels.ATTACK_TARGET_Y);
    if (target.y != existingTargetY) {
      controller.broadcast(Channels.ATTACK_TARGET_Y, target.y);
    }

    RobotInfo[] teammatesAroundTarget = controller.getNearbyRobots(
        target,
        SWARM_RADIUS_SQUARED + 5,
        controller.getTeam());

    if (teammatesAroundTarget.length >= 10) {
      attackDistance = 0;
    } else if (teammatesAroundTarget.length < 5) {
      attackDistance = SWARM_RADIUS_SQUARED;
    }
    int existingAttackDistance =
        controller.readBroadcast(Channels.ATTACK_DISTANCE);
    if (existingAttackDistance != attackDistance) {
      controller.broadcast(Channels.ATTACK_DISTANCE, attackDistance);
    }

    propogateSupply();
  }
}
