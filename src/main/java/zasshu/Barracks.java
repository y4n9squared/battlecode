/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.AbstractRobot;
import zasshu.core.Controller;

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
    RobotInfo[] robots = controller.getNearbyRobots(
        GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,
        controller.getTeam());
    for (int i = robots.length; --i >= 0;) {
      if (Clock.getBytecodesLeft() < 600) {
        break;
      }
      int supplyUpkeep = robots[i].type.supplyUpkeep;
      if (robots[i].supplyLevel < 5 * supplyUpkeep) {
        controller.transferSupplies(100 * supplyUpkeep, robots[i]);
      }
    }
  }
}
