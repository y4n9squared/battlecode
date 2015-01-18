/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import static zasshu.util.Algorithms.*;

import zasshu.core.AbstractRobot;
import zasshu.core.Channels;
import zasshu.core.Controller;

import battlecode.common.Clock;
import battlecode.common.DependencyProgress;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

public final class Beaver extends AbstractRobot {

  private static final RobotType[] BUILD_ORDER = new RobotType[] {
    RobotType.MINERFACTORY, RobotType.BARRACKS, RobotType.TANKFACTORY,
    RobotType.HELIPAD, RobotType.AEROSPACELAB, RobotType.SUPPLYDEPOT
  };

  private static final int[] BUILD_COUNT = new int[] {
    1, 1, 2, 1, 1, 5
  };

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
   * If {@code buildOnEven} is {@code true}, Beavers can only build at map
   * locations where x + y is even.
   */
  private boolean buildOnEven;

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
    MapLocation hqLocation = controller.getHQLocation();
    if ((hqLocation.x + hqLocation.y) % 2 == 0) {
      buildOnEven = true;
    } else {
      buildOnEven = false;
    }
  }

  @Override protected void runHelper() {
    if (Clock.getRoundNum() % RobotType.BEAVER.buildTurns == 0) {
      broadcastAlive();
    }
    if (controller.isCoreReady()) {
      // TODO: If enemies are close, flee from danger.
      if (!isMovingToBuild) {
        buildNextStructure();
      }

      if (isMovingToBuild) {
        MapLocation myLoc = getLocation();
        if (pastLocations[head] != myLoc) {
          pastLocations[head++] = myLoc;
        }
        if (myLoc.distanceSquaredTo(destination) <= 2) {
          isMovingToBuild = false;
          controller.build(myLoc.directionTo(destination), buildType);
        } else {
          controller.move(computeGradient());
        }
      }
    }
    propogateSupply();
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
    MapLocation[] locs = MapLocation.getAllMapLocationsWithinRadiusSq(
        controller.getLocation(), 16);
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
    if (controller.getTerrain(loc) != TerrainTile.NORMAL) {
      return false;
    }
    if (buildOnEven && (loc.x + loc.y) % 2 == 1
        || !buildOnEven && (loc.x + loc.y) % 2 == 0) {
      return false;
    }
    return true;
  }

  /**
   * Broadcast that we are allive by incrementing the beaver channel.
   */
  private void broadcastAlive() {
    int numBeavers = controller.readBroadcast(Channels.NUM_BEAVERS);
    controller.broadcast(Channels.NUM_BEAVERS, numBeavers + 1);
  }

  /**
   * Determine which structure to build.
   *
   * <p>If a structure is selected for construction, the type is stored in
   * {@code buildType} and the location is set in {@code destination}. {@code
   * isMovingToBuild} will be set to {@code true}.
   */
  private void buildNextStructure() {
    // Figure out what to build next.

    RobotInfo[] robots = controller.getNearbyRobots();
    int[] count = getRobotCount(robots, controller.getTeam());

    buildType = null;
    for (int i = 0; i < BUILD_ORDER.length; ++i) {
      RobotType type = BUILD_ORDER[i];
      if (count[type.ordinal()] < BUILD_COUNT[i]) {
        buildType = type;
        break;
      }
    }
    if (buildType != null) {
      isMovingToBuild = true;
      destination = getConstructionLocation();
    }
  }
}
