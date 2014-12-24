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
    InfluenceField field = new InfluenceField(25);
    field.addTeammate(new MapLocation(8, 10));
    assertTrue(field.influence(new MapLocation(8, 8)) > 0);
  }

  @Test public void testEnemyInfluence() {
    InfluenceField field = new InfluenceField(25);
    field.addEnemy(new MapLocation(8, 10));
    assertTrue(field.influence(new MapLocation(8, 10)) < 0);
  }

  @Test public void testCombinedInfluence() {
    InfluenceField field = new InfluenceField(25);
    field.addEnemy(new MapLocation(8, 10));
    field.addTeammate(new MapLocation(9, 10));
    assertTrue(field.influence(new MapLocation(8, 8)) < 0);

    field.addTeammate(new MapLocation(9, 9));
    assertTrue(field.influence(new MapLocation(8, 8)) > 0);
  }

}
