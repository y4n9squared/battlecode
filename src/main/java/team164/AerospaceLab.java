/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164;

import team164.core.AbstractRobot;
import team164.core.Controller;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.RobotType;

/**
 * An Aerospace Lab robot.
 *
 * @author Yang Yang
 */
public final class AerospaceLab extends AbstractRobot {

  private static final RobotType TYPE_TO_BUILD = RobotType.LAUNCHER;

  public AerospaceLab(Controller controller) {
    super(controller);
  }

  @Override protected void runHelper() {
    if (controller.isCoreReady()
        && Clock.getRoundNum() % TYPE_TO_BUILD.buildTurns > 1
        && shouldSpawnUnit(TYPE_TO_BUILD)) {
      Direction dir = getEnemyHQDirection();
      controller.spawn(dir, TYPE_TO_BUILD);
    }
  }
}
