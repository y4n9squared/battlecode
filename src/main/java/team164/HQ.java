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

import java.util.Random;

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
  private static final int MIN_ROUND_TO_ATTACK = 800;
  private static final int ROUNDS_UNTIL_DEFENSE_SWITCH = 100;
  private static final int ROUNDS_UNTIL_NEW_ATTACK = 300;

  private final Random rnd = new Random();

  private int attackDistance = SWARM_RADIUS_SQUARED;
  private MapLocation defenseTarget = null;
  private MapLocation attackTarget = null;
  private int numTowers = -1;
  private MapLocation[] myTowers;
  private MapLocation[] enemyTowers;
  private MapLocation myLoc;

  private int attackRoundCounter = -1;
  private int defenseRoundCounter = 0;

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
    rnd.setSeed(controller.getID());
    myLoc = controller.getLocation();
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

    computeDefenseTarget();

    // If we are losing or tied
    if (myTowers.length <= enemyTowers.length
        && Clock.getRoundNum() > MIN_ROUND_TO_ATTACK) {
      computeAttackTarget();

      RobotInfo[] teammatesAroundTarget = controller.getNearbyRobots(
          attackTarget,
          SWARM_RADIUS_SQUARED + 20,
          controller.getTeam());

      if (teammatesAroundTarget.length >= 12) {
        attackDistance = 0;
      } else if (teammatesAroundTarget.length < 5) {
        attackDistance = SWARM_RADIUS_SQUARED;
      }

      int existingAttackDistance =
          controller.readBroadcast(Channels.ATTACK_DISTANCE);
      if (existingAttackDistance != attackDistance) {
        controller.broadcast(Channels.ATTACK_DISTANCE, attackDistance);
      }
    } else {
      attackRoundCounter = -1;
      int existingAttackersRound = controller.readBroadcast(
          Channels.ATTACKERS_MAX_SPAWN_ROUND);

      if (existingAttackersRound != 0) {
        controller.broadcast(Channels.ATTACKERS_MAX_SPAWN_ROUND, 0);
      }
    }
  }

  private void computeDefenseTarget() {
    int helpTarget = controller.readBroadcast(Channels.TOWER_HELP);
    if (helpTarget != 0) {
      MapLocation newTarget = intToLocation(helpTarget, myLoc);
      if (newTarget.equals(defenseTarget)) {
        return;
      }

      defenseRoundCounter = 0;
      defenseTarget = newTarget;

    } else {
      if (defenseTarget != null
          && ++defenseRoundCounter < ROUNDS_UNTIL_DEFENSE_SWITCH) {
        return;
      }
      defenseRoundCounter = 0;

      if (myTowers.length == 0) {
        defenseTarget = controller.getHQLocation();
      } else {
        MapLocation newTarget;
        do {
          int index = rnd.nextInt(myTowers.length + 1);
          if (index == 0) {
            newTarget = controller.getHQLocation();
          } else {
            newTarget = myTowers[index - 1];
          }
        } while (newTarget.equals(defenseTarget));

        defenseTarget = newTarget;
      }
    }

    controller.broadcast(Channels.DEFENSE_TARGET,
        locationToInt(defenseTarget, controller.getLocation()));
  }

  private void computeAttackTarget() {
    if (attackRoundCounter == -1
        || ++attackRoundCounter > ROUNDS_UNTIL_NEW_ATTACK) {
      controller.broadcast(Channels.ATTACKERS_MAX_SPAWN_ROUND,
          Clock.getRoundNum());
      attackRoundCounter = 0;
    }

    if (numTowers == enemyTowers.length) {
      return;
    }

    MapLocation locToSearchAround;
    if (attackTarget == null) {
      locToSearchAround = controller.getLocation();
    } else {
      locToSearchAround = attackTarget;
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
      attackTarget = enemyTowers[targetIndex - 1];
    } else {
      attackTarget = controller.getEnemyHQLocation();
    }
    controller.broadcast(Channels.ATTACK_TARGET,
        locationToInt(attackTarget, controller.getLocation()));
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
