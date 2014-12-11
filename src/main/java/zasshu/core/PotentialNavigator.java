/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import battlecode.common.Direction;
import battlecode.common.MapLocation;

/**
 * A navigator that directs the caller in the direction of maximal potential.
 *
 * @author Yang Yang
 */
public final class PotentialNavigator implements Navigator {

  private final PotentialField field;
  private final TerrainMap map;

  /**
   * Constructs a {@code PotentialNavigator}.
   *
   * @param f potential field
   * @param m terrain map
   */
  public PotentialNavigator(PotentialField f, TerrainMap m) {
    field = f;
    map = m;
  }

  /**
   * Returns the direction of the maximum potential gradient.
   *
   * @param loc current location
   */
  @Override public Direction getNextStep(MapLocation loc) {
    double maxPotential = Double.NEGATIVE_INFINITY;
    int idx = -1;
    MapLocation[] locs = MapLocation.getAllMapLocationsWithinRadiusSq(loc, 2);
    for (int i = 0; i < locs.length; ++i) {
      if (map.isLocationBlocked(locs[i])) {
        continue;
      }
      double potential = field.potential(locs[i]);
      if (potential > maxPotential) {
        maxPotential = potential;
        idx = i;
      }
    }
    return loc.directionTo(locs[idx]);
  }
}
