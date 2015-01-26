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

public final class Tower extends AbstractRobot {

  /**
   * This is the order in which the tower attacks enemys.
   */
  private enum AttackPriority {
    MISSILE, LAUNCHER, COMMANDER, TANK, DRONE, BASHER, SOLDIER, MINER, BEAVER,
    COMPUTER;
  }

  private static final int ATTACK_RADIUS = RobotType.TOWER.attackRadiusSquared;

  private MapLocation myLoc;

  public Tower(Controller controller) {
    super(controller);
    myLoc = controller.getLocation();
  }

  @Override protected void runHelper() {
    MapLocation locToBroadcast = null;
    if (controller.isWeaponReady()) {
      RobotInfo[] enemies = controller.getNearbyRobots(
          ATTACK_RADIUS, controller.getOpponentTeam());

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
        locToBroadcast = target.location;
      }
    }

    if (locToBroadcast == null) {
      RobotInfo[] sensedEnemies = controller.getNearbyRobots(
          RobotType.TOWER.sensorRadiusSquared + 5,
          controller.getOpponentTeam());

      if (sensedEnemies.length > 0) {
        locToBroadcast = myLoc;
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
}
