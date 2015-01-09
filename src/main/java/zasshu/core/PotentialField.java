/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.Team;

/**
 * A vector field that represents the "potential" of each map location. Enemy
 * units emit attracting forces. Friendly units emit repelling forces for
 * obstacle avoidance. The potential at a map location is the sum of attractive
 * and repelling forces.
 *
 * @author Yang Yang
 */
public final class PotentialField {

  private final GameState state;
  private final FieldConfiguration config;

  /**
   * Constructs a {@code PotentialField} object with no sources.
   *
   * @param r radius of maximal charge of sources
   */
  public PotentialField(GameState gs, FieldConfiguration fc) {
    state = gs;
    config = fc;
  }

  /**
   * Computes the potential of the specified map location. Only the highest
   * potential produced by an enemy is added to the total field.
   *
   * @param loc map location where potential is to be calculated
   * @return potential value
   */
  public double potential(MapLocation loc) {
    double positive = 0;
    double negative = 0;
    RobotInfo[] units = state.nearbyUnits();
    for (int i = units.length; --i >= 0;) {
      double force = computeForce(loc, units[i]);
      if (force > 0) {
        positive = Math.max(positive, force);
      } else {
        negative += force;
      }
    }
    return positive + negative;
  }

  private double computeForce(MapLocation loc, RobotInfo unit) {
    double potential = 0;
    int d = loc.distanceSquaredTo(unit.location);
    if (unit.team == config.getTeam().opponent()) {
      switch (unit.type) {
        case BEAVER:
        case SOLDIER:
        case BASHER:
          return computePositiveForce(d);
        case TOWER:
          return 10 * computePositiveForce(d);
        default:
          return 0;
      }
    }
    return computeNegativeForce(d);
  }

  private double computePositiveForce(int d) {
    return 10.0 / (Math.abs(
          d - config.getAttackRadiusSquared()) + 1);
  }

  private double computeNegativeForce(int d) {
    return -5.0 / d;
  }
}
