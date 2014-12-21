/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import battlecode.common.*;

/**
 * A simple bug navigator with a sensor radius of 1 unit.
 *
 * BugNavigator moves in the direction of its destination when its next
 * step is unobstructed. When it encounters a wall, it follows it until the
 * tangent bug termination condition is satisfied.
 *
 * @author Yang Yang
 */
public final class BugNavigator implements Navigator {

  private static enum State {
    MOTION_TO_GOAL,
    FOLLOW_BOUNDARY
  }

  private final TerrainMap map;
  private State state;
  private MapLocation wall;
  private int distReach;
  private int distFollowed;
  private MapLocation destination;

  public BugNavigator(TerrainMap m) {
    map = m;
    state = State.MOTION_TO_GOAL;
  }

 /**
  * {@inheritDoc}
  */
  @Override public Direction getNextStep(MapLocation loc) {
    Direction nextDir = Direction.NONE;
    if (destination == null || loc.equals(destination)) {
      return nextDir;
    }
    while (nextDir == Direction.NONE) {
      switch (state) {
        case MOTION_TO_GOAL:
          nextDir = motionToGoal(loc);
          break;
        case FOLLOW_BOUNDARY:
          nextDir = followBoundary(loc);
          break;
      }
    }
    return nextDir;
  }

  public void setDestination(MapLocation dest) {
    destination = dest;
  }

  private Direction motionToGoal(MapLocation loc) {
    Direction dir = loc.directionTo(destination);
    MapLocation nextLoc = loc.add(dir);
    if (map.isLocationBlocked(nextLoc)) {
      state = State.FOLLOW_BOUNDARY;
      wall = nextLoc;
      distReach = Integer.MAX_VALUE;
      return Direction.NONE;
    }
    return dir;
  }

  private Direction followBoundary(MapLocation loc) {
    distReach = Math.min(distReach, wall.distanceSquaredTo(destination));
    distFollowed = loc.distanceSquaredTo(destination);
    if (distFollowed < distReach) {
      state = State.MOTION_TO_GOAL;
      return Direction.NONE;
    }
    Direction dir = loc.directionTo(wall);
    MapLocation nextWall = wall;
    while (map.isLocationBlocked(nextWall)) {
      wall = nextWall;
      dir = dir.rotateRight();
      nextWall = loc.add(dir);
    }
    return dir;
  }
}
