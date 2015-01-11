/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.AbstractRobot;
import zasshu.core.Controller;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public final class HQ extends AbstractRobot {

  public HQ(Controller controller) {
    super(controller);
  }

  @Override protected void runHelper() {
    boolean attacked = false;

    if (controller.isWeaponReady()) {
      attacked = attackLowest();
    }

    if (!attacked && controller.isCoreReady()) {
      Direction dir = enemyDirection();

      if (teammatesOfType(RobotType.BEAVER) < 4) {
        controller.spawn(dir, RobotType.BEAVER);
      }
    }

    RobotInfo[] robotsToSupply = nearbyRobotsToSupply();
    for (int i = robotsToSupply.length; --i >= 0;) {
      int upkeep = robotsToSupply[i].type.supplyUpkeep;

      if (upkeep > 0 && robotsToSupply[i].supplyLevel < upkeep * 2) {
        controller.transferSupplies(upkeep * 10, robotsToSupply[i].location);
      }
    }
  }

  private RobotInfo[] nearbyRobotsToSupply() {
    // TODO: Filter by (getLocation(),
    // GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, myTeam);
    return controller.getNearbyRobots();
  }
}
