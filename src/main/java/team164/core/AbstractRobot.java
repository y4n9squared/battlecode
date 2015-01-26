/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164.core;

import static battlecode.common.RobotType.*;
import static team164.core.Channels.*;
import static team164.util.Algorithms.*;

import team164.util.Timer;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

/**
 * Abstract skeletal implementation of the {@code Robot} interface.
 *
 * @author Holman Gao
 * @author Yang Yang
 */
public abstract class AbstractRobot implements Robot {

  /**
   * Ratio used for supply weighted average calculation.
   */
  private static final double UNIT_STRUCTURE_SUPPLY_RATIO = 5.0;

  protected final Controller controller;
  protected final Timer timer;
  protected final RobotType type;
  protected final int mySpawnRound;

  protected MapLocation target = null;
  protected int attackDistance;
  protected MapLocation myLoc;
  protected boolean useBugNavigator = false;
  protected Direction bugHeading;
  protected int bugInitialDistanceSquared;

  private MapLocation[] targets;

  protected AbstractRobot(Controller ctrl) {
    controller = ctrl;
    timer = new Timer(controller.getType());
    type = controller.getType();
    mySpawnRound = Clock.getRoundNum();
  }

  @Override public void run() {
    while (true) {
      try {
        if (type.buildTurns > 0 && Clock.getRoundNum() % type.buildTurns == 1) {
          broadcastAlive();
        }
        myLoc = controller.getLocation();
        targets = null;
        runHelper();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        controller.setIndicatorString(0, "USING BUG: " + useBugNavigator);
        if (bugHeading != null) {
          controller.setIndicatorString(1, "HEADING: " + bugHeading);
        }
        if (Clock.getBytecodesLeft() > 750) {
          propogateSupply();
        }
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
  protected MapLocation[] getTraversableAdjacentMapLocations(
      boolean avoidTargets) {

    MapLocation[] arr = new MapLocation[9];
    MapLocation[] locs = MapLocation.getAllMapLocationsWithinRadiusSq(
        getLocation(), 2);
    int count = 0;
    for (int i = locs.length; --i >= 0;) {
      MapLocation loc = locs[i];
      if (controller.canMove(myLoc.directionTo(loc)) || loc.equals(myLoc)) {
        if (avoidTargets && isWithinEnemyTowerRange(loc)) {
          continue;
        }

        arr[count++] = loc;
      }
    }
    MapLocation[] returnLocs = new MapLocation[count];
    System.arraycopy(arr, 0, returnLocs, 0, count);
    return returnLocs;
  }

  private boolean countsAsStructureForSupply(RobotType type) {
    return type.isBuilding || type == RobotType.BEAVER
        || type == RobotType.MINER;
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

    if (robots.length == 0) {
      return;
    }

    int unitCount = 0;
    int structureCount = 0;
    double totalUnitSupply = 0.0;
    double totalStructureSupply = 0.0;

    double mySupply = controller.getSupplyLevel();
    if (countsAsStructureForSupply(type)) {
      ++structureCount;
      totalStructureSupply += mySupply;
    } else {
      ++unitCount;
      totalUnitSupply += mySupply;
    }

    RobotInfo unitMin = null;
    RobotInfo structureMin = null;
    double unitSupplyMin = Double.POSITIVE_INFINITY;
    double structureSupplyMin = Double.POSITIVE_INFINITY;

    for (int i = robots.length; --i >= 0;) {
      double supply = robots[i].supplyLevel;
      if (countsAsStructureForSupply(robots[i].type)) {
        ++structureCount;
        totalStructureSupply += supply;

        if (structureSupplyMin > supply) {
          structureMin = robots[i];
          structureSupplyMin = supply;
        }
      } else {
        ++unitCount;
        totalUnitSupply += supply;

        if (unitSupplyMin > supply) {
          unitMin = robots[i];
          unitSupplyMin = supply;
        }
      }
    }

    double totalSupply = totalStructureSupply + totalUnitSupply;

    double optimalStructureSupply = totalSupply
        / (structureCount + unitCount * UNIT_STRUCTURE_SUPPLY_RATIO);
    double optimalUnitSupply = optimalStructureSupply
        * UNIT_STRUCTURE_SUPPLY_RATIO;

    double myOptimalSupply = countsAsStructureForSupply(type)
        ? optimalStructureSupply : optimalUnitSupply;

    if (mySupply > myOptimalSupply) {
      RobotInfo supplyTarget;
      if (optimalUnitSupply - unitSupplyMin
          > optimalStructureSupply - structureSupplyMin) {
        supplyTarget = unitMin;
      } else {
        supplyTarget = structureMin;
      }

      controller.transferSupplies((int) (mySupply - myOptimalSupply),
          supplyTarget);
    }
  }

  protected MapLocation[] getAttackTargets() {
    if (targets != null) {
      return targets;
    }

    MapLocation hq = controller.getEnemyHQLocation();
    MapLocation[] towers = controller.getEnemyTowerLocations();
    targets = new MapLocation[towers.length + 1];
    targets[0] = hq;
    System.arraycopy(towers, 0, targets, 1, towers.length);
    return targets;
  }

  protected void startBugNavigation() {
    useBugNavigator = true;
    bugInitialDistanceSquared = myLoc.distanceSquaredTo(target);
    Direction dir = myLoc.directionTo(target);
    bugHeading = getAvailableMoveDirection(dir);
    controller.move(dir);
  }

  protected boolean moveLikeABug() {
    Direction dir = bugHeading.opposite().rotateLeft();
    if (controller.canMove(dir) && !isWithinEnemyTowerRange(myLoc.add(dir))) {
      return false;
    }

    dir = getAvailableMoveDirection(dir);
    if (controller.move(dir)) {
      bugHeading = dir;
    }

    return true;
  }

  /**
   * Increments the attendance channel for this robot type by 1.
   */
  private void broadcastAlive() {
    int channel = getCountChannel(controller.getType());
    int count = controller.readBroadcast(channel);
    controller.broadcast(channel, count + 1);
  }

  /**
   * Returns {@code true} if the specified map location is within sensor range
   * of enemy towers and HQ.
   *
   * @param loc map location
   * @return {@code true} if {@code loc} is within enemy tower/HQ sensor range
   */
  private boolean isWithinEnemyTowerRange(MapLocation loc) {
    MapLocation[] targets = getAttackTargets();
    for (int j = targets.length; --j >= 0;) {
      if (loc.distanceSquaredTo(targets[j]) <= 24) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the first direction available to move or {@code Direction.NONE} if
   * none exists. Directions are considered in a counter-clockwise fashion.
   *
   * @param dir starting direction
   * @return direction to move
   */
  private Direction getAvailableMoveDirection(Direction dir) {
    for (int i = 8; --i >= 0;) {
      if (controller.canMove(dir) && !isWithinEnemyTowerRange(myLoc.add(dir))) {
        return dir;
      }
      dir = dir.rotateLeft();
    }
    return Direction.NONE;
  }
}
