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
  private static int NUM_BEAVER_TARGET = 3;

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
        // Calling Enum.valueOf is potentially dangerous here - if the enemy
        // RobotType.toString conversion does not match an AttackPriority enum,
        // the method will throw IllegalArgumentException.
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
      int numBeavers = controller.readBroadcast(Channels.NUM_BEAVERS);
      if (numBeavers < NUM_BEAVER_TARGET) {
        controller.spawn(getEnemyHQDirection(), RobotType.BEAVER);
      }
      controller.broadcast(Channels.NUM_BEAVERS, 0);
    }

    if (Clock.getRoundNum() % 50 == 0) {
      RobotInfo[] robots = controller.getNearbyRobots(
          GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,
          controller.getTeam());
      for (int i = robots.length; --i >= 0;) {
        if (robots[i].type.canSpawn()) {
          controller.transferSupplies(2000, robots[i]);
        } else if (robots[i].type == RobotType.BEAVER) {
          controller.transferSupplies(
              50 * robots[i].type.supplyUpkeep, robots[i]);
        }
      }
    }

    MapLocation[] enemyTowers = controller.getEnemyTowerLocations();
    if (enemyTowers.length > 0) {
      // TODO choose the closest tower
      controller.broadcast(Channels.ATTACK_TARGET_X, enemyTowers[0].x);
      controller.broadcast(Channels.ATTACK_TARGET_Y, enemyTowers[0].y);
    } else {
      MapLocation enemyHQ = controller.getEnemyHQLocation();
      controller.broadcast(Channels.ATTACK_TARGET_X, enemyHQ.x);
      controller.broadcast(Channels.ATTACK_TARGET_Y, enemyHQ.y);
    }
  }
}
