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
        Direction maxDir = null;
        MapLocation myLoc = controller.getLocation();
        Direction[] dirs = Direction.values();

        for (int i = 8; --i >= 0;) {
          double ore = controller.senseOre(myLoc.add(dirs[i]));
          if (ore > maxOre) {
            maxOre = ore;
            maxDir = dirs[i];
          }
        }

        if (controller.move(maxDir)) {
          mineCounter = 0;
        } else {
          controller.mine();
        }
      }
    }
  }
}
