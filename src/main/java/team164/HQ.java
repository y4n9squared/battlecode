/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164;

import static battlecode.common.RobotType.*;
import static team164.core.Channels.*;
import static team164.util.Algorithms.*;

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
   * The distance at which to be from target when we are retreating.
   */
  private static final int RETREAT_RADIUS_SQUARED =
      RobotType.HQ.sensorRadiusSquared / 2;

  /**
   * The rounds it takes to spawn an army.
   */
  private static final int ROUNDS_UNTIL_ATTACK = 100;

  private int attackDistance = SWARM_RADIUS_SQUARED;
  private MapLocation currentTarget = null;
  private int numTowers = -1;
  private boolean attacking = false;
  private MapLocation[] myTowers;
  private MapLocation[] enemyTowers;

  /**
   * We start this so we will start attacking on some arbitrary turn.
   */
  private int attackRoundCounter = ROUNDS_UNTIL_ATTACK - 500;

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
      if (Clock.getRoundNum() % BEAVER.buildTurns == 2) {
        int numBeavers = controller.readBroadcast(getCountChannel(BEAVER));
        if (numBeavers < NUM_BEAVER_TARGET) {
          controller.spawn(getEnemyHQDirection(), BEAVER);
        }
      }
    }
    resetRobotCount();

    myTowers = controller.getTowerLocations();
    enemyTowers = controller.getEnemyTowerLocations();

    if (myTowers.length > enemyTowers.length
        || ++attackRoundCounter < ROUNDS_UNTIL_ATTACK) {
      if (attacking) {
        attacking = false;
        numTowers = -1;
      }

      computeDefenseTarget();
      attackDistance = RETREAT_RADIUS_SQUARED;
    } else {
      if (!attacking) {
        attacking = true;
        numTowers = -1;
      }

      computeAttackTarget();

      RobotInfo[] teammatesAroundTarget = controller.getNearbyRobots(
          currentTarget,
          SWARM_RADIUS_SQUARED + 20,
          controller.getTeam());

      if (teammatesAroundTarget.length >= 12) {
        attackDistance = 0;
      } else if (teammatesAroundTarget.length < 5) {
        attackDistance = SWARM_RADIUS_SQUARED;
      }
    }

    int existingAttackDistance =
        controller.readBroadcast(Channels.ATTACK_DISTANCE);
    if (existingAttackDistance != attackDistance) {
      controller.broadcast(Channels.ATTACK_DISTANCE, attackDistance);
    }
  }

  private void computeDefenseTarget() {
    /*
    if (numTowers == myTowers.length) {
      return;
    }

    MapLocation locToSearchAround;
    locToSearchAround = controller.getEnemyHQLocation();
    numTowers = myTowers.length;

    int targetIndex = 0;
    if (numTowers > 0) {
      double closestDistance = Double.POSITIVE_INFINITY;
      for (int i = numTowers; --i >= 0;) {
        double distance = locToSearchAround.distanceSquaredTo(myTowers[i]);
        if (distance < closestDistance) {
          closestDistance = distance;
          targetIndex = i + 1;
        }
      }
      currentTarget = myTowers[targetIndex - 1];
    } else {
      currentTarget = controller.getHQLocation();
    }
    */
    MapLocation[] myTowers = controller.getTowerLocations();

    if (numTowers == myTowers.length) {
      return;
    }
    currentTarget = controller.getHQLocation();
    controller.broadcast(Channels.TARGET_LOCATION,
        locationToInt(currentTarget, controller.getLocation()));
  }

  private void computeAttackTarget() {
    if (numTowers == enemyTowers.length) {
      return;
    }

    MapLocation locToSearchAround;
    if (numTowers != -1) {
      // We just killed an enemy, do a dance!

      // If we want to continue attacking, search around currentTarget
      // locToSearchAround = currentTarget;

      // If we want to retreat, go back to defenseTarget
      attackRoundCounter = 0;
      return;
    }

    if (currentTarget == null) {
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
    controller.broadcast(Channels.TARGET_LOCATION,
        locationToInt(currentTarget, controller.getLocation()));
  }

  private void resetRobotCount() {
    int roundNum = Clock.getRoundNum();
    RobotType[] types = RobotType.values();
    for (int i = types.length; --i >= 0;) {
      if (types[i].buildTurns > 0 && roundNum % types[i].buildTurns == 0) {
        controller.broadcast(getCountChannel(types[i]), 0);
      }
    }
  }
}
