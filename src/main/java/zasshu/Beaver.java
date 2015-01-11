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

public final class Beaver extends AbstractRobot {

  /**
   * Constructs a {@code Beaver} object.
   *
   * @param c controller
   */
  public Beaver(Controller c) {
    super(c);
  }

  @Override protected void runHelper() {
    if (controller.isCoreReady()) {
      // TODO: Build if tech tree is not satisfied.

      if (!doesRobotExist(RobotType.MINERFACTORY)) {
        controller.build(getEnemyHQDirection(), RobotType.MINERFACTORY);
      } else if (!doesRobotExist(RobotType.BARRACKS)) {
        controller.build(getEnemyHQDirection(), RobotType.BARRACKS);
      }

      // If enemies are close, flee from danger. Otherwise, mine.
    }
  }

  private boolean doesRobotExist(RobotType type) {
    DependencyProgress progress = controller.getDependencyProgress(type);
    return progress != DependencyProgress.NONE;
  }
}
