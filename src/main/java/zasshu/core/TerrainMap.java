/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import battlecode.common.MapLocation;
import battlecode.common.TerrainTile;

/**
 * A representation of the map in graphical coordinates. When referencing
 * coordinates, (0,0) is the upper left corner and (1, 0) is to the right.
 *
 * @author Yang Yang
 */
public final class TerrainMap {

  private final TerrainTile[][] terrain;
  private final int width;
  private final int height;

  /**
   * Constructs a {@code Map}.
   *
   * @param tiles terrain tiles
   */
  public TerrainMap(TerrainTile[][] tiles) {
    terrain = tiles;
    width = terrain.length;
    height = terrain[0].length;
  }

  /**
   * Returns the width of the map.
   */
  public int getWidth() {
    return width;
  }

  /**
   * Returns the height of the map.
   */
  public int getHeight() {
    return height;
  }

  /**
   * Returns {@code true} if {@code loc} is a wall or off of the map and {@code
   * false} otherwise.
   *
   * @param loc map location
   */
  public boolean isLocationBlocked(MapLocation loc) {
    return isLocationBlocked(loc.x, loc.y);
  }

  /**
   * Returns {@code true} if {@code loc} is a wall or off of the map and {@code
   * false} otherwise.
   *
   * @param x x-coordinate
   * @param y y-coordinate
   */
  public boolean isLocationBlocked(int x, int y) {
    if (x < 0 || y < 0 || x >= width || y >= height) {
      return true;
    }
    if (terrain[x][y] == TerrainTile.VOID
        || terrain[x][y] == TerrainTile.OFF_MAP) {
      return true;
    }
    return false;
  }
}
