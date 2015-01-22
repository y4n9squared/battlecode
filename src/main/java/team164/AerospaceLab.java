/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164;

import team164.core.AbstractRobot;
import team164.core.Controller;

import battlecode.common.Direction;
import battlecode.common.RobotType;

/**
 * An Aerospace Lab robot.
 *
 * @author Yang Yang
 */
public final class AerospaceLab extends AbstractRobot {

  public AerospaceLab(Controller controller) {
    super(controller);
  }

  @Override protected void runHelper() {
    if (controller.isCoreReady()) {
      if (shouldSpawn()) {
        Direction dir = getEnemyHQDirection();
        controller.spawn(dir, RobotType.LAUNCHER);
      }
    }
  }

  private boolean shouldSpawn() {
    // TODO: Spawn intelligently
    return true;
  }
}
