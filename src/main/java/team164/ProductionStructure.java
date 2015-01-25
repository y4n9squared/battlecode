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
    1.0 / 4, 1.0 / 4, 1.0 / 4, 1.0 / 4
  };

  private final RobotType buildType;
  private int debt;

  public ProductionStructure(Controller controller, RobotType type) {
    super(controller);
    buildType = type;
  }

  @Override protected void runHelper() {
    if (shouldSpawnUnit()) {
      ++debt;
      if (controller.isCoreReady()) {
        if (Clock.getRoundNum() % buildType.buildTurns > 1) {
          Direction dir = getEnemyHQDirection();
          controller.spawn(dir, buildType);
          --debt;
        }
      }
    }
    if (debt > buildType.buildTurns * 0.75) {
      // TODO: Supply is not meeting demand. Broadcast for Beaver to build
      // another production building
      debt = 0;
    }
  }

  protected boolean shouldSpawnUnit() {
    // If we really sucked at ore management...
    if (controller.getTeamOre() > 2100) {
      return true;
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
