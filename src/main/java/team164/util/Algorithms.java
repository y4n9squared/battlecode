/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164.util;

import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

/**
 * A static library of useful algorithms.
 *
 * @author Yang Yang
 */
public final class Algorithms {

  private Algorithms() {
  }

  /**
   * Returns the number of robots of the specified team by type. The number of
   * robots of type {@code t} is {@code count[t.ordinal()]}.
   *
   * @param robots robots to count
   * @param team team to filter by
   * @return number of robots by type
   */
  public static int[] getRobotCount(RobotInfo[] robots, Team team) {
    int[] count = new int[RobotType.values().length];
    for (int i = robots.length; --i >= 0;) {
      if (robots[i].team == team) {
        ++count[robots[i].type.ordinal()];
      }
    }
    return count;
  }

  /**
   * Converts a map location to a unique 32-bit integer.
   *
   * @param loc map location to convert
   * @param hq map location of HQ
   * @see #intToLocation(int, MapLocation)
   */
  public static int locationToInt(MapLocation loc, MapLocation hq) {
    return ((loc.x - hq.x) << 16) | ((loc.y - hq.y) & 0xFFFF);
  }

  /**
   * Converts an integer into a map location.
   *
   * @param int integer to convert
   * @param hq map location of HQ
   * @see #locationToInt(MapLocation, MapLocation)
   */
  public static MapLocation intToLocation(int x, MapLocation hq) {
    return new MapLocation((x >> 16) + hq.x, (short) (x & 0xFFFF) + hq.y);
  }

  /**
   * Returns an array of map locations corresponding to the locations of the
   * specified robots.
   *
   * @param robots array of robots
   * @return robot locations
   */
  public static MapLocation[] getRobotLocations(RobotInfo[] robots) {
    MapLocation[] locs = new MapLocation[robots.length];
    for (int i = robots.length; --i >= 0;) {
      locs[i] = robots[i].location;
    }
    return locs;
  }

  /**
   * Computes the map location with the highest potential.
   *
   * @param locs map locations to consider
   * @param pos map locations with positive charges
   * @param posCharges positive charge values
   * @param neg map locations with negative charges
   * @param negCharges negative charge values
   * @return map location with highest potential
   */
  public static MapLocation maxPotentialMapLocation(
      MapLocation[] locs, MapLocation[] pos, double[] posCharges,
      MapLocation[] neg, double[] negCharges) {
    MapLocation target = null;
    double maxPotential = Double.NEGATIVE_INFINITY;
    for (int i = locs.length; --i >= 0;) {
      double potential = 0;
      for (int j = pos.length; --j >= 0;) {
        int d = locs[i].distanceSquaredTo(pos[j]);
        potential = Math.max(potential, posCharges[j] / d);
      }
      for (int j = neg.length; --j >= 0;) {
        int d = locs[i].distanceSquaredTo(neg[j]);
        potential -= negCharges[j] / d;
      }
      if (potential > maxPotential) {
        maxPotential = potential;
        target = locs[i];
      }
    }
    return target;
  }
}
