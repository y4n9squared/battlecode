/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.AbstractRobot;
import zasshu.core.Controller;

import battlecode.common.Direction;
import battlecode.common.RobotType;

public final class Tower extends AbstractRobot {

  public Tower(Controller controller) {
    super(controller);
  }

  @Override protected void runHelper() {
    if (controller.isWeaponReady()) {
      controller.attackLowest();
    }
  }
}
