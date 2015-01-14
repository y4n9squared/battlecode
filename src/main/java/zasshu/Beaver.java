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

public final class Beaver extends AbstractRobot {


  /**
   * Whether or not this robot is currently traveling to its build location.
   */
  private boolean isMovingToBuild = false;

  /**
   * If {@code isMovingToBuild} is {@code true}, this specifies the location of
   * the structure to be built.
   */
  private MapLocation destination;

  /**
   * If {@code isMovingToBuild} is {@code true}, this specifies the type of
   * structure to build once the robot reaches its destination.
   */
  private RobotType buildType = null;

  /**
   * A list of previous map locations that this robot moved to.
   */
  private MapLocation[] pastLocations = new MapLocation[10000];
  private int head = 0;

  /**
   * Constructs a {@code Beaver} object.
   *
   * @param c controller
   */
  public Beaver(Controller c) {
    super(c);
  }

  @Override protected void runHelper() {
    MapLocation myLoc = getLocation();
    if (pastLocations[head] != myLoc) {
      pastLocations[head++] = myLoc;
    }
    if (controller.isCoreReady()) {
      // TODO: If enemies are close, flee from danger.
      if (!isMovingToBuild) {
        // Figure out what to build next.
        if (!doesRobotExist(RobotType.MINERFACTORY)) {
          isMovingToBuild = true;
          buildType = RobotType.MINERFACTORY;
          destination = getConstructionLocation();
        } else if (!doesRobotExist(RobotType.BARRACKS)) {
          isMovingToBuild = true;
          buildType = RobotType.BARRACKS;
          destination = getConstructionLocation();
        } else if (!doesRobotExist(RobotType.HELIPAD)) {
          isMovingToBuild = true;
          buildType = RobotType.HELIPAD;
          destination = getConstructionLocation();
        }
      }

      if (isMovingToBuild) {
        if (myLoc.distanceSquaredTo(destination) <= 2) {
          isMovingToBuild = false;
          controller.build(myLoc.directionTo(destination), buildType);
        } else {
          controller.move(computeGradient());
        }
      }
    }
  }

  private Direction computeGradient() {
    MapLocation[] locs = MapLocation.getAllMapLocationsWithinRadiusSq(
        getLocation(), 2);
    MapLocation myLoc = getLocation();
    double maxPotential = Double.NEGATIVE_INFINITY;
    int choice = -1;
    for (int i = locs.length; --i >= 0;) {
      if (controller.canMove(myLoc.directionTo(locs[i]))) {
        double potential = getPotential(locs[i]);
        if (potential > maxPotential) {
          maxPotential = potential;
          choice = i;
        }
      }
    }
    return getLocation().directionTo(locs[choice]);
  }

  private double getPotential(MapLocation loc) {
    int distDest = loc.distanceSquaredTo(destination);
    double potential = 10.0 / Math.abs(distDest + 1);
    for (int i = head; --i >= Math.max(0, head - 2);) {
      if (loc.distanceSquaredTo(pastLocations[i]) <= 3) {
        potential -= 0.001;
      }
    }
    return potential;
  }

  private MapLocation getConstructionLocation() {
    MapLocation hqLocation = controller.getHQLocation();
    MapLocation[] locs = MapLocation.getAllMapLocationsWithinRadiusSq(
        hqLocation, 4);
    for (int i = locs.length; --i >= 0;) {
      if (!controller.isLocationOccupied(locs[i])
          && isLocationSafeToBuild(locs[i])) {
        return locs[i];
      }
    }
    // TODO: If this happens, there are no safe locations to build anything. We
    // will have to move the robot.
    return null;
  }

  private boolean isLocationSafeToBuild(MapLocation loc) {
    // TODO: Implement
    return true;
  }

  private boolean doesRobotExist(RobotType type) {
    DependencyProgress progress = controller.getDependencyProgress(type);
    return progress != DependencyProgress.NONE;
  }
}
