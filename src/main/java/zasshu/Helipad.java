/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.AbstractRobot;
import zasshu.core.Controller;

import battlecode.common.GameConstants;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

/**
 * A Helipad structure for spawning drones and launchers.
 *
 * @author Yang Yang
 */
public final class Helipad extends AbstractRobot {

  public Helipad(Controller controller) {
    super(controller);
  }

  @Override protected void runHelper() {
    RobotInfo[] robots = controller.getNearbyRobots(
        GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,
        controller.getTeam());
    for (int i = robots.length; --i >= 0;) {
      int supplyUpkeep = robots[i].type.supplyUpkeep;
      if (robots[i].supplyLevel < 5 * supplyUpkeep) {
        controller.transferSupplies(50 * supplyUpkeep, robots[i]);
      }
    }
  }
}
