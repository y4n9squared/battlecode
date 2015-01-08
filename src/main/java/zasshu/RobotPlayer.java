/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.Controller;
import zasshu.core.Robot;

import battlecode.common.RobotController;

/**
 * Main entry-point into player.
 *
 * @author Holman Gao
 * @author Yang Yang
 */
public class RobotPlayer {

  /**
   * Entry-point for Battlecode server.
   *
   * @param rc {@code RobotController} instance for this player
   */
  public static void run(RobotController rc) {
    Controller controller = new Controller(rc);
    Robot robot;
    switch (rc.getType()) {
      case HQ:
        robot = new HQ(controller);
        break;
      case TOWER:
        robot = new Tower(controller);
        break;
      case BEAVER:
        robot = new Beaver(controller);
        break;
      case SOLDIER:
        robot = new Soldier(controller);
        break;
      case BARRACKS:
        robot = new Barracks(controller);
        break;
      default:
        // Should never happen
        return;
    }
    robot.run();
  }
}
