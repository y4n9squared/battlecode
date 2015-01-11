/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.AbstractRobot;
import zasshu.core.Controller;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;

public final class Beaver extends Unit {

  private int buildCounter = 0;

  /**
   * Constructs a {@code Beaver} object.
   *
   * @param c controller
   */
  public Beaver(Controller c) {
    super(c);
  }

  @Override protected void runHelper() {
    if (controller.isCoreReady()) {
      boolean built = false;

      if (buildCounter > 3) {
        if (controller.canAffordToBuild(RobotType.BARRACKS)) {
          build(RobotType.BARRACKS);
          built = true;
        }

        buildCounter -= 2;
      }

      if (!built) {
        if (buildCounter % 2 == 0) {
          controller.mine();
        } else {
          move();
        }

        buildCounter++;
      }
    }
  }

  private void build(RobotType type) {
    MapLocation myLoc = controller.getLocation();
    Direction[] dirs = Direction.values();
    Direction bestDir = Direction.NONE;
    double maxInfluence = 0.0;
    for (int i = 8; --i >= 0;) {
      if (!controller.canBuild(dirs[i], type)) {
        continue;
      }

      double influence = getInfluence(myLoc.add(dirs[i]));

      if (influence > maxInfluence) {
        bestDir = dirs[i];
        maxInfluence = influence;
      }
    }

    controller.build(bestDir, type);
  }

  @Override protected double getPotential(MapLocation loc) {
    return 0;
  }

  private void move() {
    MapLocation myLoc = controller.getLocation();
    Direction[] dirs = Direction.values();
    for (int i = 8; --i >= 0;) {
      if (!controller.canMove(dirs[i])) {
        dirs[i] = Direction.NONE;
      }
    }
    Direction dir = getNextStep(controller.getLocation(), dirs);
    controller.move(dir);
  }
}
