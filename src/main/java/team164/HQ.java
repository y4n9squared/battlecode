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

  private static final RobotType[] ATTACKING_ROBOT_TYPES = new RobotType[] {
    TANK, SOLDIER
  };

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

  /**
   * The distance at which to be from target when we are retreating.
   */
  private static final int RETREAT_RADIUS_SQUARED =
      RobotType.HQ.sensorRadiusSquared / 2;

  /**
   * The rounds it takes to spawn an army.
   */
  private static final int MIN_ROUND_TO_ATTACK = 500;
  private static final int ROUNDS_UNTIL_DEFENSE_SWITCH = 100;
  private static final int ROUNDS_UNTIL_NEW_ATTACK = 100;

  private final Random rnd = new Random();

  private int attackDistance = SWARM_RADIUS_SQUARED;
  private MapLocation defenseTarget = null;
  private MapLocation attackTarget = null;
  private MapLocation needHelp = null;
  private int numEnemyTowers = -1;
  private int numMyTowers = -1;
  private MapLocation[] myTowers;
  private MapLocation[] enemyTowers;
  private MapLocation myLoc;

  private int attackRoundCounter = -1;
  private int defenseRoundCounter = 0;
  boolean shouldAttack = false;

  /**
   * This is the order in which the HQ attacks enemys.
   */
  private enum AttackPriority {
    TOWER, MISSILE, HQ, LAUNCHER, COMMANDER, TANK, DRONE, BASHER, SOLDIER,
    MINER, BEAVER, COMPUTER, AEROSPACELAB, BARRACKS, HELIPAD, MINERFACTORY,
    SUPPLYDEPOT, TANKFACTORY, TECHNOLOGYINSTITUTE, TRAININGFIELD,
    HANDWASHSTATION;
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
      if (enemies.length > 0) {
        RobotInfo target = getCriticalTarget(enemies);
        if (target == null) {
          target = getPriorityTarget(enemies);
        }
        controller.attack(target);
        needHelp = target.location;
      }
    }

    RobotInfo[] sensedEnemies = controller.getNearbyRobots(
        RobotType.HQ.sensorRadiusSquared + 10,
        controller.getOpponentTeam());
    if (needHelp == null && sensedEnemies.length > 0) {
      needHelp = sensedEnemies[0].location;
    } else if (sensedEnemies.length == 0) {
      needHelp = null;
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

    // If we are tied or losing
    if (myTowers.length <= enemyTowers.length
        && Clock.getRoundNum() > MIN_ROUND_TO_ATTACK) {

      boolean shouldTryNewAttack = false;
      if (attackRoundCounter == -1
          || ++attackRoundCounter > ROUNDS_UNTIL_NEW_ATTACK) {
        shouldTryNewAttack = true;
      }

      if (shouldTryNewAttack) {
        double powerCount = 0;
        for (int i = ATTACKING_ROBOT_TYPES.length; --i >= 0;) {
          int channel = getCountChannel(ATTACKING_ROBOT_TYPES[i]);
          powerCount = controller.readBroadcast(channel)
              * ATTACKING_ROBOT_TYPES[i].attackPower;
        }
        if (powerCount > 8 * TANK.attackPower) {
          // if we have 8 tanks or 40 soldiers
          shouldAttack = true;

          // Keep some robots home to defend
          controller.broadcast(Channels.ATTACKERS_MAX_SPAWN_ROUND,
              Clock.getRoundNum() - 50);
          attackRoundCounter = 0;
        }
      }
    } else {
      shouldAttack = false;
    }

    if (shouldAttack) {
      computeAttackTarget();

      RobotInfo[] teammatesAroundTarget = controller.getNearbyRobots(
          attackTarget,
          SWARM_RADIUS_SQUARED + 20,
          controller.getTeam());

      if (teammatesAroundTarget.length >= 10) {
        attackDistance = 0;
      } else if (teammatesAroundTarget.length < 3) {
        attackDistance = SWARM_RADIUS_SQUARED;
      }

      int existingAttackDistance =
          controller.readBroadcast(Channels.ATTACK_DISTANCE);
      if (existingAttackDistance != attackDistance) {
        controller.broadcast(Channels.ATTACK_DISTANCE, attackDistance);
      }
    } else {
      shouldAttack = false;
      attackRoundCounter = -1;
      int existingAttackersRound = controller.readBroadcast(
          Channels.ATTACKERS_MAX_SPAWN_ROUND);

      if (existingAttackersRound != 0) {
        controller.broadcast(Channels.ATTACKERS_MAX_SPAWN_ROUND, 0);
      }
    }
  }

  private void computeDefenseTarget() {
    if (needHelp != null) {
      if (needHelp.equals(defenseTarget)) {
        return;
      }
      defenseTarget = needHelp;
      controller.broadcast(Channels.DEFENSE_TARGET,
          locationToInt(needHelp, controller.getLocation()));
      return;
    }

    int helpTarget = 0;
    if (numMyTowers != myTowers.length) {
      defenseTarget = null;
      controller.broadcast(Channels.TOWER_HELP, 0);
      numMyTowers = myTowers.length;
    } else {
      helpTarget = controller.readBroadcast(Channels.TOWER_HELP);
    }

    if (helpTarget != 0) {
      // TODO: Should we check if this target is still valid?  aka if the tower
      // died.

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
    if (numEnemyTowers == enemyTowers.length) {
      return;
    }

    MapLocation locToSearchAround;
    if (attackTarget == null) {
      locToSearchAround = controller.getLocation();
    } else {
      locToSearchAround = attackTarget;
    }

    numEnemyTowers = enemyTowers.length;

    int targetIndex = 0;
    if (numEnemyTowers > 0) {
      double closestDistance = Double.POSITIVE_INFINITY;
      for (int i = numEnemyTowers; --i >= 0;) {
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

  /**
   * Returns the enemy with the maximum HP that can be killed by this HQ in
   * one hit.
   *
   * @param enemies enemies in attack range
   * @return enemy with largest HP that can be killed
   */
  private RobotInfo getCriticalTarget(RobotInfo[] enemies) {
    RobotInfo target = null;
    double maxHealth = 0;
    for (int i = enemies.length; --i >= 0;) {
      if (enemies[i].health > maxHealth
          && enemies[i].health <= RobotType.HQ.attackPower) {
        maxHealth = enemies[i].health;
        target = enemies[i];
      }
    }
    return target;
  }

  /**
   * Returns the highest priority target with the highest HP.
   *
   * @param enemies enemies in attack range
   * @return highest priority enemy with the highest HP
   */
  private RobotInfo getPriorityTarget(RobotInfo[] enemies) {
    RobotInfo target = null;
    int maxPriority = AttackPriority.HANDWASHSTATION.ordinal();
    for (int i = enemies.length; --i >= 0;) {
      int p = AttackPriority.valueOf(enemies[i].type.toString()).ordinal();
      if (target == null || p < maxPriority
          || (p == maxPriority && enemies[i].health > target.health)) {
        target = enemies[i];
      }
    }
    return target;
  }
}
