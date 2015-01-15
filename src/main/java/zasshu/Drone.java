/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.AbstractRobot;
import zasshu.core.Controller;

import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

/**
 * AI for the Drone robot type.
 *
 * @author Yang Yang
 */
public final class Drone extends AbstractRobot {

  private static final RobotType ROBOT_TYPE = RobotType.DRONE;

  /**
   * This is the order in which the drone attacks enemys.
   */
  private enum AttackPriority {
    MISSILE, TOWER, HQ, LAUNCHER, COMMANDER, TANK, DRONE, BASHER, SOLDIER,
    MINER, BEAVER, COMPUTER, AEROSPACELAB, BARRACKS, HELIPAD, MINERFACTORY,
    SUPPLYDEPOT, TANKFACTORY, TECHNOLOGYINSTITUTE, TRAININGFIELD,
    HANDWASHSTATION;
  }

  /**
   * Constructs a {@code Drone} object.
   *
   * @param c controller
   */
  public Drone(Controller c) {
    super(c);
  }

  @Override protected void runHelper() {
    if (controller.isWeaponReady()) {
      RobotInfo[] enemies = controller.getNearbyRobots(
          ROBOT_TYPE.attackRadiusSquared, controller.getOpponentTeam());

      RobotInfo target = null;
      int maxPriority = AttackPriority.COMPUTER.ordinal();

      for (int i = enemies.length; --i >= 0;) {
        // Calling Enum.valueOf is potentially dangerous here - if the enemy
        // RobotType.toString conversion does not match an AttackPriority
        // enum, the method will throw IllegalArgumentException.
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
  }
}
