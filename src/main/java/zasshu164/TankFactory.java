/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu164;

import zasshu164.core.AbstractRobot;
import zasshu164.core.Controller;

import battlecode.common.Direction;
import battlecode.common.RobotType;

/**
 * A Tank Factory robot.
 *
 * @author Yang Yang
 */
public final class TankFactory extends AbstractRobot {

  public TankFactory(Controller controller) {
    super(controller);
  }

  @Override protected void runHelper() {
    if (controller.isCoreReady()) {
      Direction dir = getEnemyHQDirection();
      controller.spawn(dir, RobotType.TANK);
    }
  }
}
