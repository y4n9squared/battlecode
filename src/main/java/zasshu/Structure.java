/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.AbstractRobot;
import zasshu.core.Controller;

import battlecode.common.Direction;
import battlecode.common.RobotType;

public final class Structure extends AbstractRobot {

  private RobotType typeToBuild;

  public Structure(Controller controller, RobotType type) {
    super(controller);
    this.typeToBuild = type;
  }

  @Override protected void runHelper() {
    if (controller.isCoreReady()) {
      Direction dir = controller.enemyDirection();
      controller.spawn(dir, typeToBuild);
    }
  }
}
