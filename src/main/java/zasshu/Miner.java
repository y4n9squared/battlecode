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

  /**
   * A square is considered to have low ore if its ore is below this number.
   */
  private static double LOW_ORE = 1.0;

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
      boolean moved = false;
      MapLocation myLoc = controller.getLocation();

      // For now, let's have a mine:move ratio of 3:1
      if (mineCounter == 3 || controller.senseOre(myLoc) < LOW_ORE) {
        double maxOre = 0.0;
        MapLocation maxLoc = null;
        MapLocation[] locs =
            MapLocation.getAllMapLocationsWithinRadiusSq(myLoc, 2);

        for (int i = 8; --i >= 0;) {
          double ore = controller.senseOre(locs[i]);
          if (ore > maxOre) {
            maxOre = ore;
            maxLoc = locs[i];
          }
        }

        Direction dir = myLoc.directionTo(maxLoc);
        moved = controller.move(dir);
      }

      if (moved) {
        mineCounter = 0;
      } else {
        controller.mine();
        ++mineCounter;
      }
    }
  }
}
