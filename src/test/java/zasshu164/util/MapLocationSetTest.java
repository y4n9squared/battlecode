/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu164.util;

import static org.junit.Assert.*;

import battlecode.common.MapLocation;

import org.junit.*;

/**
 * Unit tests for {@link MapLocationSet}.
 *
 * @author Yang Yang
 */
public class MapLocationSetTest {

  private MapLocationSet set;

  @Before public void setUp() {
    set = new MapLocationSet();
  }

  @Test public void testSize() {
    assertEquals(0, set.size());
    set.add(new MapLocation(0, 0));
    assertEquals(1, set.size());
    set.add(new MapLocation(1, 1));
    assertEquals(2, set.size());
  }

  @Test public void testIsEmpty() {
    assertTrue(set.isEmpty());
    set.add(new MapLocation(0, 0));
    assertFalse(set.isEmpty());
  }

  @Test public void testAdd() {
    assertTrue(set.add(new MapLocation(1, 1)));
    assertFalse(set.add(new MapLocation(1, 1)));
    assertTrue(set.contains(new MapLocation(1, 1)));
  }

  @Test public void testRemove() {
    set.add(new MapLocation(1, 1));
    assertTrue(set.remove(new MapLocation(1, 1)));
    assertEquals(0, set.size());
    assertFalse(set.remove(new MapLocation(1, 1)));
  }

  @Test public void testContains() {
    set.add(new MapLocation(-1, 0));
    assertTrue(set.contains(new MapLocation(-1, 0)));
    assertFalse(set.contains(new MapLocation(0, -1)));
  }

  @Test public void testClear() {
    set.add(new MapLocation(-1, 0));
    set.add(new MapLocation(-1, 2));
    set.clear();
    assertEquals(0, set.size());
  }

  @Test public void testToArray() {
    set.add(new MapLocation(-1, 0));
    set.add(new MapLocation(-1, 2));
    set.add(new MapLocation(-1, 2));
    MapLocation[] elems = set.toArray();
    assertEquals(2, elems.length);
  }
}
