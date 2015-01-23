/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164;

import static team164.core.Channels.*;

import team164.core.AbstractRobot;
import team164.core.Controller;

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
      if (Clock.getRoundNum() % RobotType.MINER.buildTurns == 1) {
        int numMiners = controller.readBroadcast(NUM_MINERS);
        if (numMiners < NUM_MINER_TARGET) {
          controller.spawn(getEnemyHQDirection(), RobotType.MINER);
        }
      }
    }
  }
}
