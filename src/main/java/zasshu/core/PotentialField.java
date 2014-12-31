/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import zasshu.util.MapLocationSet;

import battlecode.common.MapLocation;

/**
 * A vector field that represents the "potential" of each map location. Enemy
 * units emit attracting forces. Friendly units emit repelling forces for
 * obstacle avoidance. The potential at a map location is the sum of attractive
 * and repelling forces.
 *
 * @author Yang Yang
 */
public final class PotentialField {

  private static final double SOLDIER_CHARGE = 10.0;

  /**
   * Maximum range of obstacle repelling force.
   */
  private static final int REPEL_RADIUS_SQUARED = 2;

  private final double[][] staticField;

  /**
   * Map locations that emit attracting forces.
   */
  private final MapLocationSet sources;

  /**
   * Map locations that emit repelling forces.
   */
  private final MapLocationSet sinks;

  /**
   * Enemies will have maximum potential at this radius squared value.
   */
  private final int radiusSquared;

  /**
   * Constructs a {@code PotentialField} object with no sources.
   *
   * @param r radius of maximal charge of sources
   */
  public PotentialField(TerrainMap map, int r) {
    sources = new MapLocationSet();
    sinks = new MapLocationSet();
    radiusSquared = r;
    staticField = new double[map.getWidth()][map.getHeight()];
    initStaticField(map);
  }

  /**
   * Adds a source at the specified map location.
   *
   * @param loc location of source
   */
  public void addSource(MapLocation loc) {
    sources.add(loc);
  }


  /**
   * Adds a sink at the specific map location.
   */
  public void addSink(MapLocation loc) {
    sinks.add(loc);
  }

  /**
   * Clears all sources and sinks.
   */
  public void clear() {
    sources.clear();
    sinks.clear();
  }

  /**
   * Computes the potential of the specified map location.
   *
   * @param loc map location
   */
  public double potential(MapLocation loc) {
    double maxPotential = Double.NEGATIVE_INFINITY;

    // Account for sources
    MapLocation[] elems = sources.toArray();
    for (int i = elems.length; --i >= 0;) {
      maxPotential = Math.max(maxPotential,
          computeEnemyForce(loc, elems[i]));
    }

    // Account for sinks
    elems = sinks.toArray();
    for (int i = elems.length; --i >= 0;) {
      maxPotential += computeObstacleForce(elems[i], loc);
    }

    // Account for static
    return maxPotential + staticField[loc.x][loc.y];
  }

  private double computeEnemyForce(MapLocation loc, MapLocation sourceLoc) {
    int d = sourceLoc.distanceSquaredTo(loc);
    if (d > 0 && d < radiusSquared - 2) {
      return SOLDIER_CHARGE / (d * (radiusSquared - 2));
    } else if (d >= radiusSquared - 2 && d <= radiusSquared) {
      return SOLDIER_CHARGE;
    } else {
      return SOLDIER_CHARGE - 0.24 * (d - radiusSquared);
    }
  }

  /**
   * Computes the force felt at {@code point} from a charge located at {@code
   * source}.
   *
   * <p>The location of the charge and point must be different.
   *
   * @param source location of charge
   * @param point location of observer
   * @return force felt by observer from charge
   */
  private double computeObstacleForce(MapLocation source, MapLocation point) {
    return -1 * 5.0 / source.distanceSquaredTo(point);
  }

  /**
   * Compute the static field contributions from map obstacles.
   */
  private void initStaticField(TerrainMap map) {
    double[][] localStaticField = staticField;
    MapLocation[] arr = map.getObstacles().toArray();
    for (int i = arr.length; --i >= 0;) {
      MapLocation[] area = MapLocation.getAllMapLocationsWithinRadiusSq(
          arr[i], REPEL_RADIUS_SQUARED);
      for (int j = area.length; --j >= 0;) {
        MapLocation loc = area[j];
        if (map.isLocationBlocked(loc)) {
          continue;
        }
        localStaticField[loc.x][loc.y] += computeObstacleForce(arr[i], loc);
      }
    }
  }
}
