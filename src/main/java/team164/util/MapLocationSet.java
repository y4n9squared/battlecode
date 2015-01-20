/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164.util;

import battlecode.common.MapLocation;

/**
 * A bytecode-efficient implementation of a set which takes advantage of the low
 * bytecode costs of {@link java.lang.StringBuilder}.
 *
 * @author Yang Yang
 */
public final class MapLocationSet {

  private int size = 0;
  private StringBuilder keys = new StringBuilder();

  private static String locToStr(MapLocation loc) {
    return "^" + (char) loc.x + (char) loc.y;
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
   * Adds the specified element to this set if it is not already present.
   *
   * @param e element to be added to this set
   * @return {@code true} if this set did not already contain the specified
   *         location
   */
  public boolean add(MapLocation e) {
    String key = locToStr(e);
    if (keys.indexOf(key) == -1) {
      keys.append(key);
      ++size;
      return true;
    }
    return false;
  }

  /**
   * Removes the specified element from this set if it is present.
   *
   * @param e element to be removed from this set
   * @return {@code true} if this set contained the specified element
   */
  public boolean remove(MapLocation e) {
    String key = locToStr(e);
    int index = keys.indexOf(key);
    if (index != -1) {
      keys.delete(index, index + 3);
      --size;
      return true;
    }
    return false;
  }

  /**
   * Returns {@code true} if this set contains the specified element.
   *
   * @param loc element whose presence in this set is to be tested
   * @return {@code true} if this set contains the specified element
   */
  public boolean contains(MapLocation e) {
    return keys.indexOf(locToStr(e)) != -1;
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
   * Removes all of the elements from this set. The set will be empty after
   * this call returns.
   */
  public void clear() {
    keys = new StringBuilder();
    size = 0;
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
    for (int i = size; --i >= 0;) {
      elems[i] = new MapLocation(keys.charAt(i * 3 + 1),
          keys.charAt(i * 3 + 2));
    }
    return elems;
  }
}
