/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import battlecode.common.Direction;
import battlecode.common.MapLocation;

/**
 * A simple bug navigator with a sensor radius of 1 unit.
 *
 * BugNavigator moves in the direction of its destination when its next
 * step is unobstructed. When it encounters a wall, it follows it until the
 * tangent bug termination condition is satisfied.
 *
 * @author Yang Yang
 */
public final class BugNavigator extends AbstractNavigator {

  private static enum State {
    MOTION_TO_GOAL,
    FOLLOW_BOUNDARY
  }

  private State state;
  private MapLocation wall;
  private int distReach;
  private int distFollowed;

  public BugNavigator(Map map) {
    super(map);
    state = State.MOTION_TO_GOAL;
  }

 /**
  * {@inheritDoc}
  */
  @Override public Direction getNextStep(MapLocation loc) {
    Direction nextDir = Direction.NONE;
    if (loc.equals(getDestination())) {
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

  private Direction motionToGoal(MapLocation loc) {
    Direction dir = loc.directionTo(getDestination());
    MapLocation nextLoc = loc.add(dir);
    if (map.isLocationBlocked(nextLoc)) {
      state = State.FOLLOW_BOUNDARY;
      wall = nextLoc;
      distReach = Integer.MAX_VALUE;
      System.out.println("Going to follow mode.");
      return Direction.NONE;
    }
    return dir;
  }

  private Direction followBoundary(MapLocation loc) {
    System.out.println("Current wall: " + wall);
    MapLocation dest = getDestination();
    distReach = Math.min(distReach, wall.distanceSquaredTo(dest));
    distFollowed = loc.distanceSquaredTo(dest);
    if (distFollowed < distReach) {
      System.out.println("Going to goal mode");
      state = State.MOTION_TO_GOAL;
      return Direction.NONE;
    }
    // TODO (Yang): Fix bug. Wall-following behavior is incorrect.
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
