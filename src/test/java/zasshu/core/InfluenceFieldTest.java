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

  @Test public void testTeammateInfluence() {
    InfluenceField field = new InfluenceField();
    field.addTeammate(new MapLocation(8, 10));
    assertTrue(field.influence(new MapLocation(8, 8)) > 0);
  }

  @Test public void testEnemyInfluence() {
    InfluenceField field = new InfluenceField();
    field.addEnemy(new MapLocation(8, 10));
    field.addEnemy(new MapLocation(10, 8));
    assertTrue(field.influence(new MapLocation(8, 8)) < 0);
  }

  @Test public void testCombinedInfluence() {
    InfluenceField field = new InfluenceField();
    field.addEnemy(new MapLocation(8, 10));
    field.addTeammate(new MapLocation(11, 8));
    assertTrue(field.influence(new MapLocation(8, 8)) > 0);

    field.addEnemy(new MapLocation(9, 9));
    field.addEnemy(new MapLocation(10, 8));
    assertTrue(field.influence(new MapLocation(8, 8)) < 0);
  }

}
