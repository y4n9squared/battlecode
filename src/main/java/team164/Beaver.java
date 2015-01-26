/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164;

import static battlecode.common.DependencyProgress.*;
import static battlecode.common.RobotType.*;
import static team164.util.Algorithms.*;

import team164.core.AbstractRobot;
import team164.core.Channels;
import team164.core.Controller;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

import java.util.Random;

public final class Beaver extends AbstractRobot {

  private static final RobotType[] BUILD_ORDER = new RobotType[] {
    MINERFACTORY, BARRACKS, TANKFACTORY, HELIPAD, AEROSPACELAB, SUPPLYDEPOT
  };

  private final Random rnd = new Random();

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
    rnd.setSeed(controller.getID());
    MapLocation hqLocation = controller.getHQLocation();
    if ((hqLocation.x + hqLocation.y) % 2 == 0) {
      buildOnEven = true;
    } else {
      buildOnEven = false;
    }
  }

  @Override protected void runHelper() {
    if (controller.isCoreReady()) {
      // TODO: If enemies are close, flee from danger.
      if (!isMovingToBuild) {
        destination = getConstructionLocation();
        isMovingToBuild = true;
      }
      if (isMovingToBuild) {
        MapLocation myLoc = getLocation();
        if (pastLocations[head] != myLoc) {
          pastLocations[head++] = myLoc;
        }
        if (myLoc.distanceSquaredTo(destination) <= 2) {
          RobotType buildType = buildNextStructure();
          if (buildType != null) {
            boolean success = controller.build(
                myLoc.directionTo(destination), buildType);
            if (success) {
              isMovingToBuild = false;
            } else {
              if (controller.getRobotAtLocation(destination).type.isBuilding) {
                destination = getConstructionLocation();
              }
            }
          }
        } else {
          MapLocation[] locs = getTraversableAdjacentMapLocations(false);
          MapLocation[] pos = new MapLocation[] { destination };
          double[] posCharges = new double[] { 1.0 };
          MapLocation target = maxPotentialMapLocation(
              locs, pos, posCharges, null, null);
          if (target != null) {
            controller.move(myLoc.directionTo(target));
          }
        }
      }
    }
  }

  private MapLocation getConstructionLocation() {
    MapLocation[] locs = MapLocation.getAllMapLocationsWithinRadiusSq(
        controller.getLocation(), 16);
    int size = 0;
    MapLocation[] goodLocs = new MapLocation[locs.length];
    for (int i = locs.length; --i >= 0;) {
      MapLocation loc = locs[i];
      if (isLocationSafeToBuild(loc)) {
        boolean occupied = controller.isLocationOccupied(loc);
        if (!occupied || !controller.getRobotAtLocation(loc).type.isBuilding) {
          goodLocs[size++] = locs[i];
        }
      }
    }
    return goodLocs[rnd.nextInt(size)];
  }

  /**
   * Determine if a location is a safe place to build a structure.
   *
   * <p>A location is safe if there is no possibility of blocking units from
   * passing through. Since units can travel diagonally, building in a
   * checkerboard pattern will always guarentee passage so long as no tiles of
   * the opposite parity are VOID terrain.
   *
   * @param loc map location to build
   * @return {@code true} if the location is safe to build
   */
  private boolean isLocationSafeToBuild(MapLocation loc) {
    if (controller.getTerrain(loc) != TerrainTile.NORMAL) {
      return false;
    }
    if (buildOnEven && (loc.x + loc.y) % 2 == 1
        || !buildOnEven && (loc.x + loc.y) % 2 == 0) {
      return false;
    }
    MapLocation[] locs = MapLocation.getAllMapLocationsWithinRadiusSq(loc, 1);
    for (int i = locs.length; --i >= 0;) {
      if (controller.getTerrain(loc) == TerrainTile.VOID) {
        return false;
      }
    }
    return true;
  }

  /**
   * Determine which structure to build.
   *
   * <p>If a structure is selected for construction, the type is stored in
   * {@code buildType} and the location is set in {@code destination}. {@code
   * isMovingToBuild} will be set to {@code true}.
   */
  private RobotType buildNextStructure() {
    RobotType buildType = null;
    for (int i = 0; i < BUILD_ORDER.length; ++i) {
      RobotType type = BUILD_ORDER[i];
      if (controller.getDependencyProgress(type.dependency) == DONE
          && controller.getDependencyProgress(type) == NONE) {
        buildType = type;
        if (controller.getTeamOre() < buildType.oreCost) {
          return null;
        }
        break;
      }
    }
    return buildType;
  }
}
