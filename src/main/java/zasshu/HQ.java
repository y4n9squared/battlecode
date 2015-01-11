/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.AbstractRobot;
import zasshu.core.Controller;

import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public final class HQ extends AbstractRobot {

  /**
   * The number of beavers the HQ will try to maintain on the map. This should
   * be set at minimum to 1, so that we can build structures, but not too large
   * as to waste ore.
   */
  private static int NUM_BEAVER_TARGET = 1;

  public HQ(Controller controller) {
    super(controller);
  }

  @Override protected void runHelper() {
    if (controller.isWeaponReady()) {
      // TODO: Attack intelligently
    }

    if (controller.isCoreReady()) {
      RobotInfo[] robots = controller.getNearbyRobots();

      // Check for the number of beavers on the map that we own.
      int numBeavers = 0;
      for (int i = robots.length; --i >= 0;) {
        if (robots[i].type == RobotType.BEAVER
            && robots[i].team == controller.getTeam()) {
          ++numBeavers;
        }
      }

      if (numBeavers < NUM_BEAVER_TARGET) {
        controller.spawn(getEnemyHQDirection(), RobotType.BEAVER);
      }
    }

    // TODO: Transfer supply
  }
}
