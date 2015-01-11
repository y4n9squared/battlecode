/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.AbstractRobot;
import zasshu.core.Controller;

import battlecode.common.DependencyProgress;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;

public final class Miner extends AbstractRobot {

  /**
   * Constructs a {@code Miner} object.
   *
   * @param c controller
   */
  public Miner(Controller c) {
    super(c);
  }

  @Override protected void runHelper() {
    if (controller.isCoreReady()) {
      // mine and move
    }
  }
}
