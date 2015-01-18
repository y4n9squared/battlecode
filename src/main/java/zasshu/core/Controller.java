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

  private MapLocation enemyHQLocation;
  private RobotInfo[] nearbyRobots;

  /**
   * Constructs a {@code Controller}.
   *
   * @param rc {@code RobotController} instance
   */
  public Controller(RobotController rc) {
    this.rc = rc;
  }

  /**
   * Returns the team of this robot.
   *
   * @return team of this robot
   */
  public Team getTeam() {
    return rc.getTeam();
  }

  /**
   * Returns the opponent team.
   *
   * @return opponent team
   */
  public Team getOpponentTeam() {
    return rc.getTeam().opponent();
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
   * Returns the location of our HQ.
   *
   * @return location of our HQ
   */
  public MapLocation getHQLocation() {
    return rc.senseHQLocation();
  }

  /**
   * Returns the location of the enemy HQ.
   *
   * @return location of enemy HQ
   */
  public MapLocation getEnemyHQLocation() {
    if (enemyHQLocation == null) {
      enemyHQLocation = rc.senseEnemyHQLocation();
    }
    return enemyHQLocation;
  }

  /**
   * Returns the locations of our towers.
   *
   * @return locations of our towers
   */
  public MapLocation[] getTowerLocations() {
    return rc.senseTowerLocations();
  }

  /**
   * Returns and caches the location of enemy towers.
   *
   * @return locations of enemy towers
   */
  public MapLocation[] getEnemyTowerLocations() {
    return rc.senseEnemyTowerLocations();
  }

  /**
   * Returns and caches information about all robots in vision.
   *
   * @return information about all robots in vision
   */
  public RobotInfo[] getNearbyRobots() {
    if (nearbyRobots == null) {
      nearbyRobots = rc.senseNearbyRobots();
    }
    return nearbyRobots;
  }

  /**
   * Returns robots within a radius squared of robot.
   *
   * @return robots within a radius squared of robot
   */
  public RobotInfo[] getNearbyRobots(int radiusSquared) {
    return rc.senseNearbyRobots(radiusSquared);
  }

  /**
   * Returns robots within a radius squared of robot and of a given team.
   *
   * @return robots within a radius squared of robot and of a given team
   */
  public RobotInfo[] getNearbyRobots(int radiusSquared, Team team) {
    return rc.senseNearbyRobots(radiusSquared, team);
  }

  /**
   * Returns robots within a radius squared of a location and of a given team.
   *
   * @return robots within a radius squared of a location and of a given team
   */
  public RobotInfo[] getNearbyRobots(MapLocation loc, int radiusSquared,
      Team team) {
    return rc.senseNearbyRobots(loc, radiusSquared, team);
  }

  /**
   * Returns the amount of ore at the specified map location.
   *
   * @param loc map location
   * @return amount of ore
   */
  public double senseOre(MapLocation loc) {
    return rc.senseOre(loc);
  }

  /**
   * Returns the amount of our our team currently owns.
   *
   * @return amount of ore we own
   */
  public double getTeamOre() {
    return rc.getTeamOre();
  }

  /**
   * Returns the amount of supply this robot has.
   *
   * @return amount of supply this robot has
   */
  public double getSupplyLevel() {
    return rc.getSupplyLevel();
  }

  /**
   * Returns {@code true} if the specified location is occupied by another
   * robot.
   *
   * @param loc map location
   * @return {@code true} if location is occupied
   */
  public boolean isLocationOccupied(MapLocation loc) {
    try {
      return rc.isLocationOccupied(loc);
    } catch (GameActionException e) {
      e.printStackTrace();
    }
    return true;
  }

  /**
   * Returns the terrain type at the specified location.
   *
   * @param loc map location
   * @return terrain type
   */
  public TerrainTile getTerrain(MapLocation loc) {
    return rc.senseTerrainTile(loc);
  }

  /**
   * Returns the value stored on the specified channel.
   *
   * @param channel channel number
   * @return value on channel
   */
  public int readBroadcast(int channel) {
    try {
      return rc.readBroadcast(channel);
    } catch (GameActionException e) {
      e.printStackTrace();
    }
    return 0;
  }

  /**
   * Whether or not this robot can perform a core action.
   *
   * @return {@code true} if robot can perform a core action
   */
  public boolean isCoreReady() {
    return rc.isCoreReady();
  }

  /**
   * Whether or not this robot can perform an attack.
   *
   * @return {@code true} if robot can attack
   */
  public boolean isWeaponReady() {
    return rc.isWeaponReady();
  }

  public boolean canMove(Direction dir) {
    return rc.canMove(dir);
  }

  /**
   * Attacks the map location occupied by {@code target}.
   *
   * @param target object to attack
   * @return {@code true} if attack was successful
   */
  public boolean attack(RobotInfo target) {
    return attack(target.location);
  }

  /**
   * Attacks the specified map location.
   *
   * @param loc location to attack
   * @return {@code true} if attack was successful
   */
  public boolean attack(MapLocation loc) {
    if (rc.isWeaponReady() && rc.canAttackLocation(loc)) {
      try {
        rc.attackLocation(loc);
        return true;
      } catch (GameActionException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  /**
   * Attempt to move in the specified direction.
   *
   * @return {@code true} if move was successful
   */
  public boolean move(Direction dir) {
    if (rc.isCoreReady() && canMove(dir)) {
      try {
        rc.move(dir);
        return true;
      } catch (GameActionException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  /**
   * Try to spawn a robot in a given direction.  It will continue by rotating
   * the spawn direction until it can spawn a robot.
   *
   * @return {@code true} if robot was spawned
   */
  public boolean spawn(Direction dir, RobotType type) {
    try {
      for (int i = 8; --i >= 0;) {
        if (rc.isCoreReady() && rc.canSpawn(dir, type)) {
          rc.spawn(dir, type);
          return true;
        }
        dir = dir.rotateRight();
      }
    } catch (GameActionException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Try to mine at the current location.
   *
   * @return {@code true} if mining was successful
   */
  public boolean mine() {
    if (rc.isCoreReady() && rc.canMine()) {
      try {
        rc.mine();
        return true;
      } catch (GameActionException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  /**
   * Try to build a robot in the specified direction.
   *
   * @param dir direction to build
   * @param type type of robot to build
   * @return {@code true} if build is started
   */
  public boolean build(Direction dir, RobotType type) {
    if (rc.isCoreReady() && rc.canBuild(dir, type)) {
      try {
        rc.build(dir, type);
        return true;
      } catch (GameActionException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  /**
   * Transfer supplies to a robot.
   *
   * @param supply amount of supply to transfer
   * @param robot robot to recieve supplies
   * @return {@code true} if a transfer was made
   *
   */
  public boolean transferSupplies(int supply, RobotInfo robot) {
    if (getLocation().distanceSquaredTo(robot.location)
        <= GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED) {
      try {
        if (rc.getSupplyLevel() >= supply) {
          rc.transferSupplies(supply, robot.location);
          return true;
        }
      } catch (GameActionException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  /**
   * Returns the dependency progress of a robot type.
   *
   * @return dependency progress
   */
  public DependencyProgress getDependencyProgress(RobotType type) {
    return rc.checkDependencyProgress(type);
  }

  /**
   * Returns this robot's type.
   *
   * @return type
   */
  public RobotType getType() {
    return rc.getType();
  }

  /**
   * Broadcasts the value {@code data} to the specific channel.
   *
   * @param channel channel number
   * @param data value to broadcast
   * @return {@code true} if broadcast was successful
   */
  public boolean broadcast(int channel, int data) {
    try {
      rc.broadcast(channel, data);
      return true;
    } catch (GameActionException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * Yield execution of this robot and flush cache.
   */
  public void yield() {
    rc.yield();
    nearbyRobots = null;
  }
}
