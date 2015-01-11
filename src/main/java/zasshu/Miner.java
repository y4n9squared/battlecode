/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.AbstractRobot;
import zasshu.core.Controller;

import battlecode.common.DependencyProgress;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;

public final class Miner extends AbstractRobot {

  private int mineCounter = 0;

  /**
   * Constructs a {@code Miner} object.
   *
   * @param c controller
   */
  public Miner(Controller c) {
    super(c);
  }

  @Override protected void runHelper() {
    if (controller.isCoreReady()) {
      // For now, let's have a mine:move ratio of 3:1
      if (mineCounter < 3) {
        controller.mine();
        mineCounter++;
      } else {
        double maxOre = 0.0;
        MapLocation maxLoc = null;
        MapLocation myLoc = controller.getLocation();
        MapLocation locs =
            MapLocation.getAllMapLocationsWithinRadiusSq(myLoc);

        for (int i = 8; --i >= 0;) {
          double ore = controller.senseOre(locs[i]);
          if (ore > maxOre) {
            maxOre = ore;
            maxLoc = locs[i];
          }
        }

        Direction dir = myloc.directionTo(maxLoc);
        if (controller.move(dir)) {
          mineCounter = 0;
        } else {
          controller.mine();
        }
      }
    }
  }
}
