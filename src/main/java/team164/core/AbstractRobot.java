/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164.core;

import static team164.util.Algorithms.*;

import team164.util.Timer;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.TerrainTile;

/**
 * Abstract skeletal implementation of the {@code Robot} interface.
 *
 * @author Holman Gao
 * @author Yang Yang
 */
public abstract class AbstractRobot implements Robot {

  protected final Controller controller;
  protected final Timer timer;

  protected MapLocation target = null;
  protected int attackDistance;
  protected MapLocation myLoc;
  protected boolean useBugNavigator = false;
  protected Direction bugHeading;
  protected int bugInitialDistanceSquared;

  protected AbstractRobot(Controller ctrl) {
    controller = ctrl;
    timer = new Timer(controller.getType());
  }

  @Override public void run() {
    while (true) {
      try {
        myLoc = controller.getLocation();
        runHelper();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        propogateSupply();
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
   * Returns a list of map locations to which the robot is available to move,
   * including the robot's current location.
   *
   * @return list of available locations for robot to move
   */
  protected MapLocation[] getTraversableAdjacentMapLocations() {
    MapLocation[] arr = new MapLocation[9];
    MapLocation[] locs = MapLocation.getAllMapLocationsWithinRadiusSq(
        getLocation(), 2);
    int count = 0;
    for (int i = locs.length; --i >= 0;) {
      MapLocation loc = locs[i];
      if (controller.canMove(myLoc.directionTo(loc))
          || loc.equals(myLoc)) {
        arr[count++] = loc;
      }
    }
    MapLocation[] returnLocs = new MapLocation[count];
    System.arraycopy(arr, 0, returnLocs, 0, count);
    return returnLocs;
  }

  /**
   * Transfers supply to the robot with lowest supply, if our supply level is
   * above the average supply level.
   *
   * @param robots list of robots to consider supplying
   */
  private void propogateSupply() {
    RobotInfo[] robots = controller.getNearbyRobots(
        GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, controller.getTeam());

    double mySupply = controller.getSupplyLevel();
    RobotInfo target = null;
    double maxDifference = 0;
    double totalSupply = mySupply;

    for (int i = robots.length; --i >= 0;) {
      totalSupply += robots[i].supplyLevel;
      double supplyDifference = mySupply - robots[i].supplyLevel;
      if (supplyDifference > maxDifference) {
        maxDifference = supplyDifference;
        target = robots[i];
      }
    }
    double avgSupply = totalSupply / (robots.length + 1);
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

  protected void startBugNavigation() {
    bugInitialDistanceSquared = myLoc.distanceSquaredTo(target);
    useBugNavigator = true;

    Direction dir = myLoc.directionTo(target);
    for (int i = 8; --i >= 0;) {
      //if (controller.getTerrain(myLoc.add(dir)) != TerrainTile.VOID) {
      if (controller.canMove(dir)) {
        break;
      }
      dir = dir.rotateLeft();
    }
    bugHeading = dir;
    controller.move(dir);
  }

  protected boolean moveLikeABug() {
    Direction dir = bugHeading.opposite().rotateLeft();
    if (controller.canMove(dir)) {
      return false;
    }

    for (int i = 8; --i >= 0;) {
      //if (controller.getTerrain(myLoc.add(dir)) != TerrainTile.VOID) {
      if (controller.canMove(dir)) {
        break;
      }
      dir = dir.rotateLeft();
    }

    if (controller.move(dir)) {
      bugHeading = dir;
    }
    return true;
  }
}
