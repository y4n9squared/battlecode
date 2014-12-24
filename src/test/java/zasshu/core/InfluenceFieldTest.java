/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import static org.junit.Assert.*;

import battlecode.common.*;

import org.junit.*;

/**
 * Unit tests for {@link InfluenceField}.
 *
 * @author Holman Gao
 * @author Yang Yang
 */
public class InfluenceFieldTest {

  private TerrainMap map;
  TerrainTile[][] terrain;

  /**
   * Set up the TerrainMap.
   */
  @Before public void setUp() {
    terrain = new TerrainTile[20][20];
    for (int i = 0; ++i < 20;) {
      for (int j = 0; ++j < 20;) {
        terrain[i][j] = TerrainTile.NORMAL;
      }
    }
    map = new TerrainMap(terrain);
  }

  @Test public void testTeammateInfluence() {
    InfluenceField field = new InfluenceField(map, 25);
    field.addTeammate(new MapLocation(8, 10));
    assertTrue(field.influence(new MapLocation(8, 8)) > 0);
  }

  @Test public void testEnemyInfluence() {
    InfluenceField field = new InfluenceField(map, 25);
    field.addEnemy(new MapLocation(8, 10));
    assertTrue(field.influence(new MapLocation(8, 10)) < 0);
  }

  @Test public void testCombinedInfluence() {
    InfluenceField field = new InfluenceField(map, 25);
    field.addEnemy(new MapLocation(8, 10));
    field.addTeammate(new MapLocation(9, 10));
    assertTrue(field.influence(new MapLocation(8, 8)) < 0);

    field.addTeammate(new MapLocation(9, 9));
    assertTrue(field.influence(new MapLocation(8, 8)) > 0);
  }

  @Test public void testOutOfBoundsInfluence() {
    terrain[8][8] = TerrainTile.VOID;
    InfluenceField field = new InfluenceField(map, 25);
    assertEquals(
        field.influence(new MapLocation(8, 8)), Double.NEGATIVE_INFINITY, 0);
  }

}
