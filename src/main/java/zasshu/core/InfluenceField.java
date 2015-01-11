/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import zasshu.util.MapLocationSet;

import battlecode.common.*;

/**
 *  A vector field that represents the "influence" of each map location. Enemy
 *  units and map obstacles emit negative charges, while teammates emit
 *  positive charges. The influence of a map location is the sum of the
 *  charges from all sources.
 *
 *  @author Holman Gao
 *  @author Yang Yang
 */
public final class InfluenceField {


  private final MapLocationSet teammates;
  private final MapLocationSet enemies;

  /**
   * Initializes instance variables and sets the range.
   */
  public InfluenceField() {
    teammates = new MapLocationSet();
    enemies = new MapLocationSet();
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

}

