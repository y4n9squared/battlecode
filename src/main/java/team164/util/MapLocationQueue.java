/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164.util;

import battlecode.common.MapLocation;

/**
 * A bytecode-efficient implementation of a queue.  Shit goes crazy if you add
 * more than {@code MAX_SIZE} elements to the queue, don't try it!
 *
 * @author Holman Gao
 */
public final class MapLocationQueue {

  /**
   * Maximum size of the queue.
   */
  private static final int MAX_SIZE = 100000;

  private final MapLocation[] queue;
  private int headIndex = 0;
  private int size = 0;

  /**
   * Constructs a new {@code MapLocationQueue}.
   */
  public MapLocationQueue() {
    queue = new MapLocation[MAX_SIZE];
  }

  /**
   * Adds the specified element to the end of the queue.
   *
   * @param e element to be added to this set
   */
  public void add(MapLocation e) {
    queue[headIndex + size] = e;
    ++size;
  }

  /**
   * Removes the head of the queue and returns it.
   *
   * @return a MapLocation if one was popped
   */
  public MapLocation remove() {
    if (size == 0) {
      return null;
    } else {
      --size;
      return queue[headIndex++];
    }
  }

  /**
   * Returns the most recently added element, if exists, and {@code null}
   * otherwise.
   *
   * @return most recently added element
   */
  public MapLocation back() {
    if (size == 0) {
      return null;
    }
    return queue[headIndex + size - 1];
  }

  /**
   * Returns the number of elements in this set.
   *
   * @return the number of elements in this set
   */
  public int size() {
    return size;
  }

  /**
   * Removes all of the elements from this set. The set will be empty after
   * this call returns.
   */
  public void clear() {
    size = 0;
    headIndex = 0;
  }

  /**
   * Returns {@code true} if this set contains no elements.
   *
   * @return {@code true} if this set contains no elements
   */
  public boolean isEmpty() {
    return size() == 0;
  }

  /**
   * Returns an array containing all of the elements in this set. No references
   * to the returned array are maintained by this set. The caller is thus free
   * to modify the returned array.
   *
   * @return an array containing all elements in this set
   */
  public MapLocation[] toArray() {
    MapLocation[] elems = new MapLocation[size];
    System.arraycopy(queue, headIndex, elems, 0, size);
    return elems;
  }
}
