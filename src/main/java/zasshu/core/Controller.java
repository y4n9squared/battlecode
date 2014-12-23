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

  // Lazily-evaluated constants
  private Team myTeam;
  private Team opponentTeam;
  private RobotType myType;
  private MapLocation enemySpawn;
  private TerrainMap terrainMap;

  private static int startByteCount;
  private static int startRound;

  /**
   * Constructs a {@code Controller}.
   *
   * @param rc {@code RobotController} instance
   */
  public Controller(RobotController rc) {
    this.rc = rc;
    terrainMap = new TerrainMap(computeTerrain());
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
   * @param loc location to attack
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
    MapLocation loc = getLocationInDirection(dir);
    try {
      if (!terrainMap.isLocationBlocked(loc)
          && rc.senseObjectAtLocation(loc) == null) {
        // TODO: Seeing some exceptions being thrown here despite checking
        // location prior to moving. If our robot runs out of bytecodes here, it
        // could cause an exception to be thrown. Checking and moving need to be
        // performed atomically.
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
   * Returns the terrain map.
   *
   * @return terrain map
   */
  public TerrainMap getTerrainMap() {
    return terrainMap;
  }

  /**
   * Broadcasts an object to the {@code RobotController}'s channels.
   *
   * <p>The object is serialzed into a byte stream and saved to channels in
   * 4-byte pieces. The total bytecode usage is proportional to the size of the
   * object, plus a constant serialization overhead.
   *
   * <p>If an error occurs, the return value will be -1.
   *
   * @param start start channel
   * @param obj object to broadcast
   * @return number of channels written
   */
  public int writeChannels(int start, Object obj) {
    byte[] arr = Serialization.toByteArray(obj);
    // TODO(Yang): Need to do some check here to determine if the array being
    // passed is too large (i.e. will overflow channel buffer).
    int numChannelsWritten = 0;
    for (int i = arr.length / 4; --i >= 0;) {
      byte[] tmp = new byte[4];
      System.arraycopy(arr, 4 * i, tmp, 0, 4);
      int data = pack(tmp);
      try {
        rc.broadcast(start + i, data);
        ++numChannelsWritten;
      } catch (GameActionException e) {
        e.printStackTrace();
        return -1;
      }
    }
    return numChannelsWritten;
  }

  /**
   * Reads an object from the {@code RobotController}'s channels.
   *
   * <p>The object is reconstructed from its byte stream via deserialization.
   * Bytecode usage is proportional to the size of the object, plus a constant
   * deserialization overhead.
   *
   * <p>If an error occurs, the return value will be {@code null}.
   *
   * @param start start channel
   * @param size number of channels to read
   */
  public Object readChannels(int start, int size) {
    byte[] arr = new byte[size * 4];
    for (int i = 0; i < size; ++i) {
      byte[] val;
      try {
        val = unpack(rc.readBroadcast(start + i));
      } catch (GameActionException e) {
        e.printStackTrace();
        return null;
      }
      arr[4 * i] = val[0];
      arr[4 * i + 1] = val[1];
      arr[4 * i + 2] = val[2];
      arr[4 * i + 3] = val[3];
    }
    return Serialization.fromByteArray(arr);
  }

  /**
   * Converts 4 bytes into an int.
   *
   * <p>Cost: 33 bytecodes
   *
   * @param arr byte array of length 4
   */
  static int pack(byte[] arr) {
    return (((0xFF & arr[0]) << 24) | ((0xFF & arr[1]) << 16)
        | ((0xFF & arr[2]) << 8) | (0xFF & arr[3]));
  }

  /**
   * Returns a byte array of length 4 representing the specified integer.
   *
   * @param val integer value to convert
   */
  static byte[] unpack(int val) {
    byte[] arr = new byte[4];
    arr[0] = (byte) (val >> 24);
    arr[1] = (byte) (val >> 16);
    arr[2] = (byte) (val >> 8);
    arr[3] = (byte) val;
    return arr;
  }

  /**
   * Starts the controller clock to begin measuring the number of bytecodes
   * executed.
   */
  public static void startBytecodeCounter() {
    startByteCount = Clock.getBytecodeNum();
    startRound = Clock.getRoundNum();
  }

  /**
   * Stops the controller clock and returns the number of bytecodes executed
   * since started.
   */
  public static int stopBytecodeCounter() {
    return (GameConstants.BYTECODE_LIMIT - startByteCount)
      + (Clock.getRoundNum() - startRound - 1) * GameConstants.BYTECODE_LIMIT
      + Clock.getBytecodeNum();
  }
}
