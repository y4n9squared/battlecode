/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import static org.junit.Assert.*;

import battlecode.common.MapLocation;
import battlecode.common.TerrainTile;

import org.junit.*;

/**
 * Unit tests for {@link TerrainMap}.
 *
 * @author Yang Yang
 */
public class TerrainMapTest {

  private final int size = 100;
  private TerrainTile[][] tiles;

  @Before public void setUp() {
    tiles = new TerrainTile[size][size];
    tiles[0][0] = TerrainTile.VOID;
  }

  @Test public void testIsLocationBlocked() {
    TerrainMap map = new TerrainMap(tiles);
    assertTrue(map.isLocationBlocked(0, 0));
    assertTrue(map.isLocationBlocked(new MapLocation(0, 0)));
    assertFalse(map.isLocationBlocked(1, 0));
    assertTrue(map.isLocationBlocked(-1, -1));
    assertTrue(map.isLocationBlocked(100, 0));
  }

  @Test public void testOutOfBounds() {
    TerrainMap map = new TerrainMap(tiles);
    assertTrue(map.isOutOfBounds(new MapLocation(-1, 0)));
    assertTrue(map.isOutOfBounds(new MapLocation(0, size)));
    assertFalse(map.isOutOfBounds(new MapLocation(0, 0)));
    assertFalse(map.isOutOfBounds(new MapLocation(1, 1)));
  }

  @Test public void testIsObstructed() {
    TerrainMap map = new TerrainMap(tiles);
    assertTrue(map.isObstructed(0, 0));
    assertTrue(map.isObstructed(new MapLocation(0, 0)));
    assertFalse(map.isObstructed(1, 0));
  }

  @Test public void testDimensions() {
    TerrainMap map = new TerrainMap(tiles);
    assertEquals(100, map.getWidth());
    assertEquals(100, map.getHeight());
  }
}
