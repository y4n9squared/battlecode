/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.util;

import static org.junit.Assert.*;

import battlecode.common.MapLocation;

import org.junit.*;

/**
 * Unit tests for {@link MapLocationSet}.
 *
 * @author Yang Yang
 */
public class MapLocationSetTest {

  private MapLocationSet s;

  @Before public void setUp() {
    s = new MapLocationSet();
  }

  @Test public void testSize() {
    assertEquals(0, s.size());
    s.add(new MapLocation(0, 0));
    assertEquals(1, s.size());
    s.add(new MapLocation(1, 1));
    assertEquals(2, s.size());
  }

  @Test public void testIsEmpty() {
    assertTrue(s.isEmpty());
    s.add(new MapLocation(0, 0));
    assertFalse(s.isEmpty());
  }

  @Test public void testAdd() {
    assertTrue(s.add(new MapLocation(1, 1)));
    assertFalse(s.add(new MapLocation(1, 1)));
    assertTrue(s.contains(new MapLocation(1, 1)));
  }

  @Test public void testRemove() {
    s.add(new MapLocation(1, 1));
    assertTrue(s.remove(new MapLocation(1, 1)));
    assertEquals(0, s.size());
    assertFalse(s.remove(new MapLocation(1, 1)));
  }

  @Test public void testContains() {
    s.add(new MapLocation(-1, 0));
    assertTrue(s.contains(new MapLocation(-1, 0)));
    assertFalse(s.contains(new MapLocation(0, -1)));
  }

  @Test public void testClear() {
    s.add(new MapLocation(-1, 0));
    s.add(new MapLocation(-1, 2));
    s.clear();
    assertEquals(0, s.size());
  }

  @Test public void testToArray() {
    s.add(new MapLocation(-1, 0));
    s.add(new MapLocation(-1, 2));
    s.add(new MapLocation(-1, 2));
    MapLocation[] elems = s.toArray();
    assertEquals(2, elems.length);
  }
}
