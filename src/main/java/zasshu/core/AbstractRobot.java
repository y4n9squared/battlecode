/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import zasshu.util.Timer;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;

/**
 * Abstract skeletal implementation of the {@code Robot} interface.
 *
 * @author Holman Gao
 * @author Yang Yang
 */
public abstract class AbstractRobot implements Robot {

  protected final Controller controller;
  protected final Timer timer;

  protected AbstractRobot(Controller ctrl) {
    controller = ctrl;
    timer = new Timer(controller.getType());
  }

  @Override public void run() {
    while (true) {
      try {
        runHelper();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        controller.yield();
      }
    }
  }

  protected void runHelper() {
  }

  /**
   * Returns the direction to the enemy HQ.
   *
   * @return direction to enemy HQ
   */
  protected Direction getEnemyHQDirection() {
    return getLocation().directionTo(controller.getEnemyHQLocation());
  }

  /**
   * Returns the current location of this robot.
   *
   * @return current location
   */
  protected MapLocation getLocation() {
    return controller.getLocation();
  }

  /**
   * Transfers supply to the robot with lowest supply, if our supply level is
   * above the average supply level.
   *
   * @param robots list of robots to consider supplying
   */
  protected void propogateSupply() {
    RobotInfo[] robots = controller.getNearbyRobots(
        GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, controller.getTeam());

    double mySupply = controller.getSupplyLevel();
    RobotInfo target = null;
    double maxDifference = 0;
    double totalSupply = 0;

    for (int i = robots.length; --i >= 0;) {
      totalSupply += robots[i].supplyLevel;
      double supplyDifference = mySupply - robots[i].supplyLevel;
      if (supplyDifference > maxDifference) {
        maxDifference = supplyDifference;
        target = robots[i];
      }
    }
    double avgSupply = totalSupply / robots.length;
    if (mySupply > avgSupply) {
      controller.transferSupplies((int) (mySupply - avgSupply), target);
    }
  }

  protected MapLocation[] getAttackTargets() {
    MapLocation hq = controller.getEnemyHQLocation();
    MapLocation[] towers = controller.getEnemyTowerLocations();

    MapLocation[] targets = new MapLocation[towers.length + 1];
    targets[0] = hq;
    System.arraycopy(towers, 0, targets, 1, towers.length);

    return targets;
  }
}
