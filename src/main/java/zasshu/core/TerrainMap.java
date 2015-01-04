/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import zasshu.util.MapLocationSet;

import battlecode.common.MapLocation;
import battlecode.common.TerrainTile;

/**
 * A representation of the map in graphical coordinates. When referencing
 * coordinates, (0,0) is the upper left corner and (1, 0) is to the right.
 *
 * @author Yang Yang
 */
public final class TerrainMap {

  private final MapLocationSet obstacles;
  private final int width;
  private final int height;

  /**
   * Constructs a {@code Map}.
   *
   * @param tiles terrain tiles
   */
  public TerrainMap(TerrainTile[][] tiles) {
    obstacles = new MapLocationSet();
    for (int i = tiles.length; --i >= 0;) {
      for (int j = tiles[0].length; --j >= 0;) {
        if (tiles[i][j] == TerrainTile.VOID
            || tiles[i][j] == TerrainTile.OFF_MAP) {
          obstacles.add(new MapLocation(i, j));
        }
      }
    }
    width = tiles.length;
    height = tiles[0].length;
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
    if (isOutOfBounds(loc) || obstacles.contains(loc)) {
      return true;
    }
    return false;
  }

  /**
   * Returns {@code true} if {@code loc} is a wall or off of the map and {@code
   * false} otherwise.
   *
   * @param x x-coordinate
   * @param y y-coordinate
   */
  public boolean isLocationBlocked(int x, int y) {
    return isLocationBlocked(new MapLocation(x, y));
  }

  private boolean isOutOfBounds(MapLocation loc) {
    if (loc.x < 0 || loc.y < 0 || loc.x >= width || loc.y >= height) {
      return true;
    }
    return false;
  }

  /**
   * Returns the set of map obstacles.
   *
   * <p>This method does not make a defensive copy in order to save bytecodes.
   * However, it is intended to be read-only.
   *
   * @return set of map obstacles
   */
  public MapLocationSet getObstacles() {
    return obstacles;
  }
}
