/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164;

import static team164.util.Algorithms.*;

import team164.core.AbstractRobot;
import team164.core.Channels;
import team164.core.Controller;

import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

/**
 * A Tower robot.
 *
 * @author Holman Gao
 * @author Yang Yang
 */
public final class Tower extends AbstractRobot {

  /**
   * This is the order in which the tower attacks enemys.
   */
  private enum AttackPriority {
    TOWER, MISSILE, HQ, LAUNCHER, COMMANDER, TANK, DRONE, BASHER, SOLDIER,
    MINER, BEAVER, COMPUTER, AEROSPACELAB, BARRACKS, HELIPAD, MINERFACTORY,
    SUPPLYDEPOT, TANKFACTORY, TECHNOLOGYINSTITUTE, TRAININGFIELD,
    HANDWASHSTATION;
  }

  private static final int ATTACK_RADIUS = RobotType.TOWER.attackRadiusSquared;

  private final MapLocation myLoc;

  /**
   * Constructs a tower robot.
   *
   * @param controller controller
   */
  public Tower(Controller controller) {
    super(controller);
    myLoc = controller.getLocation();
  }

  @Override protected void runHelper() {
    MapLocation locToBroadcast = null;
    if (controller.isWeaponReady()) {
      RobotInfo[] enemies = controller.getNearbyRobots(
          ATTACK_RADIUS, controller.getOpponentTeam());
      if (enemies.length > 0) {
        RobotInfo target = getCriticalTarget(enemies);
        if (target == null) {
          target = getPriorityTarget(enemies);
        }
        controller.attack(target);
        locToBroadcast = target.location;
      }
    }

    if (locToBroadcast == null) {
      RobotInfo[] sensedEnemies = controller.getNearbyRobots(
          RobotType.TOWER.sensorRadiusSquared + 10,
          controller.getOpponentTeam());
      if (sensedEnemies.length > 0) {
        locToBroadcast = sensedEnemies[0].location;
      }
    }

    int helpTargetAsInt = controller.readBroadcast(Channels.TOWER_HELP);
    MapLocation helpTarget = helpTargetAsInt == 0 ? null
        : intToLocation(helpTargetAsInt, controller.getHQLocation());

    if (locToBroadcast == null) {
      // If we were the one to broadcast the help, clear it
      if (helpTarget != null
          && myLoc.distanceSquaredTo(helpTarget) <= ATTACK_RADIUS) {
        controller.broadcast(Channels.TOWER_HELP, 0);
      }
    } else if (helpTarget == null || !locToBroadcast.equals(helpTarget)) {
      // If we need help and the target is not the same
      controller.broadcast(Channels.TOWER_HELP,
          locationToInt(locToBroadcast, controller.getHQLocation()));
    }
  }

  /**
   * Returns the enemy with the maximum HP that can be killed by this tower in
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
          && enemies[i].health <= RobotType.TOWER.attackPower) {
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
