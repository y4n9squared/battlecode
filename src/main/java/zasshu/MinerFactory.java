/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.AbstractRobot;
import zasshu.core.Controller;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public final class MinerFactory extends AbstractRobot {

  /**
   * The number of miners to maintain on the map.
   */
  private static int NUM_MINER_TARGET = 8;

  public MinerFactory(Controller controller) {
    super(controller);
  }

  @Override protected void runHelper() {
    if (controller.isCoreReady()) {
      RobotInfo[] robots = controller.getNearbyRobots();

      // Check for the number of miners on the map that we own.
      int numMiners = 0;
      for (int i = robots.length; --i >= 0;) {
        if (robots[i].type == RobotType.MINER
            && robots[i].team == controller.getTeam()) {
          ++numMiners;
        }
      }

      if (numMiners < NUM_MINER_TARGET) {
        controller.spawn(getEnemyHQDirection(), RobotType.MINER);
      }
    }
  }
}
