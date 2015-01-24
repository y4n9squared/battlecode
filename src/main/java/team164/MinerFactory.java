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

/**
 * A MinerFactory robot.
 *
 * <p>The number of miners produced is based on a logistic function where the
 * variable is the distance between HQs.
 */
public final class MinerFactory extends AbstractRobot {

  private static final int MAX_MINERS = 30;
  private static final double GROWTH_RATE = 0.01;

  /**
   * The number of miners to maintain on the map.
   */
  private final int numTargetMiners;

  /**
   * Constructs a Miner robot.
   *
   * @param controller controller
   */
  public MinerFactory(Controller controller) {
    super(controller);
    int d = controller.getHQLocation().distanceSquaredTo(
        controller.getEnemyHQLocation());
    numTargetMiners = (int) (MAX_MINERS / (1 + Math.pow(
            Math.E, -1 * GROWTH_RATE * Math.sqrt(d))));
  }

  @Override protected void runHelper() {
    if (controller.isCoreReady()) {
      if (Clock.getRoundNum() % RobotType.MINER.buildTurns == 1) {
        int numMiners = controller.readBroadcast(NUM_MINERS);
        if (numMiners < numTargetMiners) {
          controller.spawn(getEnemyHQDirection(), RobotType.MINER);
        }
        controller.broadcast(NUM_MINERS, 0);
      }
    }
  }
}
