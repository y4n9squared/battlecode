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
public final class PotentialNavigator {

  private final PotentialField field;

  /**
   * Constructs a {@code PotentialNavigator}.
   *
   * @param f potential field
   */
  public PotentialNavigator(PotentialField f) {
    field = f;
  }

  /**
   * Returns the direction of the maximum potential gradient.
   *
   * @param loc current location
   */
  public Direction getNextStep(MapLocation loc,
      Direction[] possibleDirections) {

    double maxPotential = Double.NEGATIVE_INFINITY;
    int idx = 0;
    for (int i = 8; --i >= 0;) {
      Direction dir = possibleDirections[i];

      if (dir == Direction.NONE) {
        continue;
      }

      double potential = field.potential(loc.add(dir));
      if (potential > maxPotential) {
        maxPotential = potential;
        idx = i;
      }
    }
    return possibleDirections[idx];
  }
}
