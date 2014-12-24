/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import zasshu.util.MapLocationSet;

import battlecode.common.*;

public final class InfluenceField {

  private static final double SOLDIER_CHARGE = 10.0;

  private final MapLocationSet teammates;
  private final MapLocationSet enemies;
  private final MapLocationSet blocked;

  private final int mapWidth;
  private final int mapHeight;

  /**
   * The attack radius of robot in distance squared. Enemies will have maximum
   * influence close to this value.
   */
  private final int attackRangeSquared;

  /**
   *  A vector field that represents the "influence" of each map location. Enemy
   *  units and map obstacles emit negative charges, while teammates emit
   *  positive charges. The influence of a map location is the sum of the
   *  charges from all sources.
   *
   *  @author Holman Gao
   */
  public InfluenceField(TerrainMap m, int range) {
    mapWidth = m.getWidth();
    mapHeight = m.getHeight();

    blocked = new MapLocationSet();
    for (int i = mapWidth; --i >= 0;) {
      for (int j = mapHeight; --j >= 0;) {
        if (m.isLocationBlocked(i, j)) {
          blocked.add(new MapLocation(i, j));
        }
      }
    }
    teammates = new MapLocationSet();
    enemies = new MapLocationSet();
    attackRangeSquared = range;
  }

  /**
   * Adds a teammate at the specified map location.
   *
   * @param loc location of teammate
   */
  public void addTeammate(MapLocation loc) {
    teammates.add(loc);
  }

  /**
   * Adds an enemy at the specified map location.
   *
   * @param loc location of enemy
   */
  public void addEnemy(MapLocation loc) {
    enemies.add(loc);
  }

  /*
   * Clears all sources.
   */
  public void clear() {
    teammates.clear();
    enemies.clear();
  }

  /**
   * Computes the influence of the specified map location.
   *
   * @param loc map location
   */
  public double influence(MapLocation loc) {
    if (isLocationOutOfBounds(loc) || blocked.contains(loc)) {
      return Double.NEGATIVE_INFINITY;
    }
    double myInfluence = 0;
    for (MapLocation s : teammates.toArray()) {
      myInfluence += influenceHelper(loc, s);
    }
    for (MapLocation s : enemies.toArray()) {
      myInfluence -= influenceHelper(loc, s);
    }
    return myInfluence;
  }

  private double influenceHelper(MapLocation loc, MapLocation sourceLoc) {
    int d = sourceLoc.distanceSquaredTo(loc);
    if (d > 0 && d < attackRangeSquared - 2) {
      return SOLDIER_CHARGE / (d * (attackRangeSquared - 2));
    } else if (d >= attackRangeSquared - 2 && d <= attackRangeSquared) {
      return SOLDIER_CHARGE;
    } else {
      return SOLDIER_CHARGE - 0.24 * (d - attackRangeSquared);
    }
  }

  private boolean isLocationOutOfBounds(MapLocation loc) {
    if (loc.x < 0 || loc.y < 0 || loc.x >= mapWidth || loc.y >= mapHeight) {
      return true;
    }
    return false;
  }
}

