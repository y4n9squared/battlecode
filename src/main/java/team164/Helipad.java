/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164;

import team164.core.AbstractRobot;
import team164.core.Controller;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

/**
 * A Helipad structure for spawning drones and launchers.
 *
 * @author Yang Yang
 */
public final class Helipad extends AbstractRobot {

  private static final RobotType TYPE_TO_BUILD = RobotType.DRONE;

  public Helipad(Controller controller) {
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
