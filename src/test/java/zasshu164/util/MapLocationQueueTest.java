/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu164.util;

import static org.junit.Assert.*;

import battlecode.common.MapLocation;

import org.junit.*;

/**
 * Unit tests for {@link MapLocationQueue}.
 *
 * @author Holman Gao
 */
public class MapLocationQueueTest {

  private MapLocationQueue queue;

  @Before public void setUp() {
    queue = new MapLocationQueue();
  }

  @Test public void testSize() {
    assertEquals(0, queue.size());
    queue.add(new MapLocation(0, 0));
    assertEquals(1, queue.size());
    queue.add(new MapLocation(1, 1));
    assertEquals(2, queue.size());
  }

  @Test public void testIsEmpty() {
    assertTrue(queue.isEmpty());
    queue.add(new MapLocation(0, 0));
    assertFalse(queue.isEmpty());
  }

  @Test public void testAdd() {
    queue.add(new MapLocation(0, 0));
    queue.add(new MapLocation(1, 1));
    assertEquals(2, queue.size());
  }

  @Test public void testRemove() {
    queue.add(new MapLocation(1, 1));
    assertEquals(new MapLocation(1, 1), queue.remove());
    assertNull(queue.remove());
    assertEquals(0, queue.size());
  }

  @Test public void testClear() {
    queue.add(new MapLocation(-1, 0));
    queue.add(new MapLocation(-1, 2));
    queue.clear();
    assertEquals(0, queue.size());
  }

  @Test public void testBack() {
    assertNull(queue.back());
    queue.add(new MapLocation(-1, 0));
    assertEquals(new MapLocation(-1, 0), queue.back());
    queue.add(new MapLocation(1, 1));
    assertEquals(new MapLocation(1, 1), queue.back());
  }

  @Test public void testToArray() {
    queue.add(new MapLocation(-1, 2));
    queue.add(new MapLocation(-1, 3));

    MapLocation[] expectedAfterAdds = new MapLocation[2];
    expectedAfterAdds[0] = new MapLocation(-1, 2);
    expectedAfterAdds[1] = new MapLocation(-1, 3);
    assertArrayEquals(expectedAfterAdds, queue.toArray());

    queue.remove();
    MapLocation[] expectedAfterRemove = new MapLocation[1];
    expectedAfterRemove[0] = new MapLocation(-1, 3);
    assertArrayEquals(expectedAfterRemove, queue.toArray());

  }
}
