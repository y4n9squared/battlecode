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

  private MapLocation myLoc;
  private int myLocAsInt;

  public Tower(Controller controller) {
    super(controller);
    myLoc = controller.getLocation();
    myLocAsInt = locationToInt(myLoc, controller.getHQLocation());
  }

  @Override protected void runHelper() {
    if (controller.isWeaponReady()) {
      RobotInfo[] enemies = controller.getNearbyRobots(
          RobotType.TOWER.attackRadiusSquared, controller.getOpponentTeam());

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
    } else {
      RobotInfo[] sensedEnemies = controller.getNearbyRobots(
          RobotType.TOWER.sensorRadiusSquared + 5,
          controller.getOpponentTeam());

      if (sensedEnemies.length > 0) {
        int currentTower = controller.readBroadcast(Channels.TOWER_HELP);

        if (currentTower != myLocAsInt) {
          controller.broadcast(Channels.TOWER_HELP, myLocAsInt);
        }
      }
    }
  }
}
