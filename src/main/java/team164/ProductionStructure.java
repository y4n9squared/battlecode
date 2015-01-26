/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164;

import static battlecode.common.RobotType.*;
import static team164.core.Channels.*;

import team164.core.AbstractRobot;
import team164.core.Controller;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.RobotType;

public class ProductionStructure extends AbstractRobot {

  /**
   * Following are used in calculating optimal army ratios.
   */
  private static final RobotType[] UNIT_TYPES = new RobotType[] {
    SOLDIER, TANK, DRONE, LAUNCHER
  };

  // MAKE SURE THIS ADDS UP TO ONE BEFORE EDITING!
  private static final double[] UNIT_RATIOS = new double[] {
    1.0 / 2, 1.0 / 4, 1.0 / 4, 0.0 / 4
  };

  private final RobotType myType;
  private final RobotType buildType;

  /**
   * Construct a robot that produces units.
   *
   * @param controller controller
   * @param type robot type to produce
   */
  public ProductionStructure(Controller controller, RobotType type) {
    super(controller);
    buildType = type;
    myType = controller.getType();
  }

  @Override protected void runHelper() {
    if (Clock.getRoundNum() % buildType.buildTurns > 1) {
      if (shouldSpawnUnit()) {
        if (controller.isCoreReady()) {
          Direction dir = getEnemyHQDirection();
          controller.spawn(dir, buildType);
        } else {
          if (Clock.getRoundNum() % buildType.buildTurns == 2) {
            int channel = getDebtChannel(myType);
            controller.broadcast(channel, 1);
          }
        }
      }
    }
  }

  protected boolean shouldSpawnUnit() {
    // If we really sucked at ore management...
    if (controller.getTeamOre() > 2100) {
      return true;
    }

    if (controller.getTeamOre() < buildType.oreCost) {
      return false;
    }

    int[] counts = new int[UNIT_TYPES.length];
    double currentRatio = 0.0;
    int currentRatioSum = 0;
    int typeIndex = -1;

    for (int i = UNIT_TYPES.length; --i >= 0;) {
      RobotType myType = UNIT_TYPES[i];

      // For computing current ratio
      int count = controller.readBroadcast(getCountChannel(myType));
      currentRatioSum += count;

      if (myType == buildType) {
        currentRatio = (double) count;
        typeIndex = i;
      }
    }
    if (currentRatioSum == 0) {
      return true;
    }
    currentRatio /= currentRatioSum;
    return (currentRatio <= UNIT_RATIOS[typeIndex]);
  }
}
