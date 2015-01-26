/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164;

import static battlecode.common.RobotType.*;
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
public final class MinerFactory extends ProductionStructure {

  private static final int MAX_MINERS = 30;
  private static final double GROWTH_RATE = 0.01;
  private static final double MINER_RATIO = 4.0;

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
    super(controller, MINER);
    int d = controller.getHQLocation().distanceSquaredTo(
        controller.getEnemyHQLocation());
    numTargetMiners = (int) (MAX_MINERS / (1 + Math.pow(
            Math.E, -1 * GROWTH_RATE * Math.sqrt(d))));
  }

  @Override protected boolean shouldSpawnUnit() {
    if (controller.isCoreReady()) {
      if (Clock.getRoundNum() % MINER.buildTurns == 2) {
        int numMiners = controller.readBroadcast(getCountChannel(MINER));

        if (numMiners < numTargetMiners) {
          int otherRobots = controller.readBroadcast(getCountChannel(SOLDIER))
              + controller.readBroadcast(getCountChannel(TANK))
              + controller.readBroadcast(getCountChannel(DRONE))
              + controller.readBroadcast(getCountChannel(LAUNCHER));

          if (numMiners < MINER_RATIO * otherRobots) {
            return true;
          }
        }
        controller.broadcast(getCountChannel(MINER), 0);
      }
    }
    return false;
  }
}
