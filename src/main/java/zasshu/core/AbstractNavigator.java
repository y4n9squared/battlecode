/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import battlecode.common.Direction;
import battlecode.common.MapLocation;

/**
 * Abstract skeletal implementation of a {@link Navigator}.
 *
 * @author Yang Yang
 */
public abstract class AbstractNavigator implements Navigator {

  protected final Map map;

  private MapLocation destination;

  protected AbstractNavigator(Map m) {
    map = m;
  }

  /**
   * {@inheritDoc}
   */
  @Override public void setDestination(MapLocation dest) {
    destination = dest;
  }

  /**
   * Returns the destination map location.
   *
   * @return destination map location
   */
  protected MapLocation getDestination() {
    return destination;
  }
}
