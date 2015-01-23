/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164;

import static team164.core.Channels.*;
import static team164.util.Algorithms.*;

import team164.core.AbstractRobot;
import team164.core.Controller;
import team164.util.MapLocationSet;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

import java.util.Random;

/**
 * A Miner robot.
 *
 * <p>A Miner attempts to maximize mining efficiency by staying on a square with
 * more than 10 ore, since mining rate is capped at 2.5. If the current map
 * location contains less than 10 ore, it moves to an adjacent location with
 * more ore. If no such location exists, it continues to mine at its current
 * location.
 *
 * <p>If a map location and its surroundings contains no ore, the Miner will
 * move in a random direction, never visiting a map location that it previously
 * visited in the past 5 turns.
 *
 * <p>Finally, a Miner prioritizes its own life over all other functions. If an
 * enemy is within sensor radius, the Miner flees by moving in the opposite
 * direction.
 *
 * @author Holman Gao
 * @author Yang Yang
 */
public final class Miner extends AbstractRobot {

  private final Random rnd = new Random();

  /**
   * A set of map locations that are within attack range of enemy towers. The
   * Miner is forbidden from mining in these locations.
   */
  private final MapLocationSet dangerousLocations = new MapLocationSet();

  /**
   * A cache of map locations visited with no ore. A Miner will not revisit
   * these locations on its random walk.
   */
  private final MapLocationSet noOreLocations = new MapLocationSet();

  private MapLocation myLoc;
  private MapLocation[] adjacentSquares;

  /**
   * Constructs a {@code Miner} robot.
   *
   * <p>The Miner's RNG is seeded with the robot's ID.
   *
   * @param c controller
   */
  public Miner(Controller c) {
    super(c);
    rnd.setSeed(controller.getID());

    // Add all map locations within enemy tower attack range into a set of
    // dangerous locations.
    MapLocation[] enemyTowers = controller.getEnemyTowerLocations();
    for (int i = enemyTowers.length; --i >= 0;) {
      MapLocation[] locs = MapLocation.getAllMapLocationsWithinRadiusSq(
          enemyTowers[i], RobotType.TOWER.attackRadiusSquared);
      for (int j = locs.length; --j >= 0;) {
        dangerousLocations.add(locs[j]);
      }
    }
  }

  @Override protected void runHelper() {
    if (Clock.getRoundNum() % RobotType.MINER.buildTurns == 0) {
      int numMiners = controller.readBroadcast(NUM_MINERS);
      controller.broadcast(NUM_MINERS, numMiners + 1);
    }
    if (controller.isCoreReady()) {
      myLoc = getLocation();
      adjacentSquares = MapLocation.getAllMapLocationsWithinRadiusSq(myLoc, 2);
      RobotInfo[] enemies = controller.getNearbyRobots(
          RobotType.MINER.sensorRadiusSquared, controller.getOpponentTeam());
      if (enemies.length > 0) {
        flee(enemies);
      } else {
        double ore = controller.senseOre(myLoc);
        if (ore >= 10) {
          controller.mine();
        } else {
          move();
        }
      }
    }
  }

  /**
   * Moves the robot in the direction opposite of the enemy forces.
   *
   * <p>The direction chosen is the direction of minimal potential gradient,
   * where enemies are positive charges.
   *
   * @param enemies enemy robots
   */
  private void flee(RobotInfo[] enemies) {
    MapLocation[] locs = adjacentSquares;
    MapLocation target = null;
    double maxPotential = Double.NEGATIVE_INFINITY;
    for (int i = locs.length; --i >= 0;) {
      if (controller.canMove(myLoc.directionTo(locs[i]))
          && !dangerousLocations.contains(locs[i])) {
        double potential = 0;
        for (int j = enemies.length; --j >= 0;) {
          int d = locs[i].distanceSquaredTo(enemies[j].location);
          potential -= 1.0 / d;
        }
        if (potential > maxPotential) {
          maxPotential = potential;
          target = locs[i];
        }
      }
    }
    controller.move(myLoc.directionTo(target));
  }

  /**
   * Moves in the direction of increasing ore. If no adjaacent square contains
   * more ore and the current map location has ore, mine. Otherwise, move in a
   * random direction.
   *
   * <p>All moves will be outside of enemy tower attack radius.
   */
  private void move() {
    MapLocation[] locs = adjacentSquares;
    MapLocation target = null;
    double maxOre = controller.senseOre(myLoc);
    for (int i = locs.length; --i >= 0;) {
      if (!controller.isLocationOccupied(locs[i])
          && !dangerousLocations.contains(locs[i])) {
        double ore = controller.senseOre(locs[i]);
        if (ore > maxOre) {
          maxOre = ore;
          target = locs[i];
        }
      }
    }

    if (target != null) {
      controller.move(myLoc.directionTo(target));
    } else if (maxOre > 0) {
      controller.mine();
    } else {
      noOreLocations.add(myLoc);
      Direction[] dirs = Direction.values();
      boolean loop = true;
      int failures = 0;
      while (loop) {
        if (failures > 10) {
          // Probably stuck. Clear cache.
          noOreLocations.clear();
        }
        int i = rnd.nextInt(8);
        MapLocation loc = myLoc.add(dirs[i]);
        if (controller.canMove(dirs[i])
            && !noOreLocations.contains(loc)
            && !dangerousLocations.contains(loc)) {
          controller.move(dirs[i]);
          loop = false;
        } else {
          ++failures;
        }
      }
    }
  }
}
