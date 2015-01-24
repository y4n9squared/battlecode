/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164;

import team164.core.AbstractRobot;
import team164.core.Channels;
import team164.core.Controller;

import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

/**
 * A Missile robot. Explodes when within range of any enemy robot.
 *
 * @author Shariq Hashme
 */

public final class Missile extends AbstractRobot {

  /**
   * Constructs a {@code Missile} robot.
   *
   * @param c controller
   */
  public Missile(Controller c) {
    super(c);
  }

  @Override public void run() {
    while (true) {
      // no use destroying without enemies close by
      RobotInfo[] enemies = controller.getNearbyRobots(
          RobotType.MISSILE.attackRadiusSquared, controller.getOpponentTeam());
      if (enemies.length > 0) {
        controller.explode();
      }
      controller.yield();
    }
  }

}
