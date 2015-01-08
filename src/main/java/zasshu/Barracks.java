/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.AbstractRobot;
import zasshu.core.Controller;

import battlecode.common.Direction;
import battlecode.common.RobotType;

public final class Barracks extends AbstractRobot {

  public Barracks(Controller controller) {
    super(controller);
  }

  @Override protected void runHelper() {
    boolean attacked = false;

    if (controller.isWeaponReady()) {
      attacked = controller.attackLowest();
    }

    if (!attacked && controller.isCoreReady()) {
      Direction dir = controller.enemyDirection();
      controller.spawn(dir, RobotType.SOLDIER);
    }
  }
}
