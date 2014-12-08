/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import battlecode.common.*;

/**
 * A {@code Controller} provides a facade for the Battlecode {@code
 * RobotController} class, whose interface and implementation may change from
 * year to year.
 *
 * @author Holman Gao
 * @author Yang Yang
 */
public final class Controller {
  private final RobotController rc;
  private Team myTeam;
  private Team opponentTeam;
  private RobotType myType;
  private MapLocation enemySpawn;

  /**
   * Constructs a {@code Controller}.
   *
   * @param rc {@code RobotController} instance
   */
  public Controller(RobotController rc) {
    this.rc = rc;
    //myTeam = rc.getTeam();
    //opponentTeam = myTeam.opponent();
    //myType = rc.getType();
  }

  /**
   * Attacks the map location occupied by {@code target} if in range.
   *
   * @param target object to attack
   * @return {@code true} if attack was successful
   */
  public boolean attack(GameObject target) {
    try {
      MapLocation targetLoc = rc.senseLocationOf(target);
      return attack(targetLoc);
    } catch (GameActionException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Attacks the specified map location.
   *
   * @param targetLoc location to attack
   * @return {@code true} if attack was successful
   */
  public boolean attack(MapLocation loc) {
    try {
      rc.attackSquare(loc);
      return true;
    } catch (GameActionException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Computes the terrain of the map.
   *
   * @return 2-dimensional array of TerrainTile objects
   */
  public TerrainTile[][] computeTerrain() {
    int width = rc.getMapWidth();
    int height = rc.getMapHeight();
    TerrainTile[][] terrain = new TerrainTile[width][height];
    for (int x = width; --x >= 0;) {
      for (int y = height; --y >= 0;) {
        // TODO (Yang): Maps are 180-degree rotationally invariant. Can reduce
        // bytecode cost of this method by half.
        terrain[x][y] = rc.senseTerrainTile(new MapLocation(x, y));
      }
    }
    return terrain;
  }

  /**
   * Computes the nearest map location in the specified direction.
   *
   * @return nearest map location in the specified direction
   */
  public MapLocation getLocationInDirection(Direction dir) {
    return getLocation().add(dir);
  }

  /**
   * Returns the current map location of the robot.
   *
   * @return current map location
   */
  public MapLocation getLocation() {
    return rc.getLocation();
  }

  /**
   * Returns the direction to the enemy spawn location.
   *
   * @return direction to enemy spawn location
   */
  public Direction enemyDirection() {
    return getLocation().directionTo(enemySpawn());
  }

  /**
   * Returns the map location of the enemy spawn.
   *
   * @return map location of enemy spawn
   */
  public MapLocation enemySpawn() {
    if (enemySpawn == null) {
      enemySpawn = rc.senseEnemyHQLocation();
    }
    return enemySpawn;
  }

  /**
   * Attempt to move in the specified direction.
   *
   * @return {@code true} if move was successful
   */
  public boolean move(Direction dir) {
    try {
      if (rc.senseObjectAtLocation(getLocationInDirection(dir)) == null) {
        rc.move(dir);
        return true;
      }
    } catch (GameActionException e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean spawn(Direction dir) {
    try {
      if (rc.senseObjectAtLocation(getLocationInDirection(dir)) == null) {
        rc.spawn(dir);
        return true;
      }
    } catch (GameActionException e) {
      e.printStackTrace();
    }
    return false;
  }

  public int senseRobotCount() {
    return rc.senseRobotCount();
  }

  /**
   * Yield execution of this robot.
   */
  public void yield() {
    rc.yield();
  }

  /**
   * Whether or not this robot is currently active.
   *
   * @return {@code true} if robot is active
   */
  public boolean isActive() {
    return rc.isActive();
  }

  /**
   * Returns the map.
   *
   * @return map
   */
  public Map getMap() {
    return new Map(computeTerrain());
  }

  public boolean broadcast(int channel, int val) {
    try {
      rc.broadcast(channel, val);
    } catch (GameActionException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public int readBroadcast(int channel) {
    try {
      return rc.readBroadcast(channel);
    } catch (GameActionException e) {
      e.printStackTrace();
      return 0;
    }
  }
}
