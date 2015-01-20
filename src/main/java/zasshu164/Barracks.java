/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu164;

import zasshu164.core.AbstractRobot;
import zasshu164.core.Controller;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public final class Barracks extends AbstractRobot {

  public Barracks(Controller controller) {
    super(controller);
  }

  @Override protected void runHelper() {
    if (controller.isCoreReady()) {
      Direction dir = getEnemyHQDirection();
      controller.spawn(dir, RobotType.SOLDIER);
    }
  }
}
