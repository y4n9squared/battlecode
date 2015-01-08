/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import zasshu.util.MapLocationQueue;

import battlecode.common.Direction;
import battlecode.common.MapLocation;

/**
 * A navigator that directs the caller in the direction of maximal potential.
 *
 * @author Yang Yang
 */
public final class PotentialNavigator {

  private static final int MAX_TRAIL_LENGTH = 3;

  private final PotentialField field;

  /**
   * Tracks the past {@code MAX_TRAIL_LENGTH} locations, including the current
   * location.
   */
  private MapLocationQueue trail;

  /**
   * Constructs a {@code PotentialNavigator}.
   *
   * @param f potential field
   */
  public PotentialNavigator(PotentialField f) {
    field = f;
    trail = new MapLocationQueue();
  }

  /**
   * Returns the direction of the maximum potential gradient.
   *
   * @param loc current location
   */
  public Direction getNextStep(MapLocation loc,
      Direction[] possibleDirections) {

    updateTrail(loc);
    updateField();

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

  /**
   * Adds {loc} to {@code trail} if it is not equal to the previous location. If
   * the trail is larger than {@code MAX_TRAIL_LENGTH}, the oldest location is
   * removed.
   *
   * @param loc current location
   */
  private void updateTrail(MapLocation loc) {
    if (trail.isEmpty() || !trail.back().equals(loc)) {
      trail.add(loc);
      while (trail.size() > MAX_TRAIL_LENGTH) {
        trail.remove();
      }
    }
  }

  /**
   * Adds all locations in {@code trail} except for the last (current location)
   * as sinks in the potential field.
   */
  private void updateField() {
    MapLocation[] locs = trail.toArray();
    for (int i = locs.length - 1; --i >= 0;) {
      field.addSink(locs[i]);
    }
  }
}
