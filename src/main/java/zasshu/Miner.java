/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import static zasshu.util.Algorithms.*;

import zasshu.core.AbstractRobot;
import zasshu.core.Controller;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

import java.util.Random;

/**
 * A Miner robot.
 *
 * @author Holman Gao
 * @author Yang Yang
 */
public final class Miner extends AbstractRobot {

  private final Random rnd = new Random();

  /**
   * Constructs a {@code Miner} object.
   *
   * @param c controller
   */
  public Miner(Controller c) {
    super(c);
    rnd.setSeed(controller.getID());
  }

  @Override protected void runHelper() {
    if (controller.isCoreReady()) {
      MapLocation myLoc = getLocation();
      RobotInfo[] enemies = controller.getNearbyRobots(
          RobotType.MINER.sensorRadiusSquared, controller.getOpponentTeam());
      MapLocation[] locs = MapLocation.getAllMapLocationsWithinRadiusSq(
          myLoc, 2);
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

  private void flee(RobotInfo[] enemies) {
    MapLocation myLoc = getLocation();
    MapLocation[] locs = MapLocation.getAllMapLocationsWithinRadiusSq(
        myLoc, 2);
    MapLocation target = null;
    double maxPotential = Double.NEGATIVE_INFINITY;
    for (int i = locs.length; --i >= 0;) {
      if (controller.canMove(myLoc.directionTo(locs[i]))) {
        double potential = 0;
        for (int j = enemies.length; --j >= 0;) {
          int d = locs[i].distanceSquaredTo(enemies[j].location);
          potential -= 5.0 / d;
        }
        if (potential > maxPotential) {
          maxPotential = potential;
          target = locs[i];
        }
      }
    }
    controller.move(myLoc.directionTo(target));
  }

  private void move() {
    MapLocation myLoc = getLocation();
    MapLocation[] locs = MapLocation.getAllMapLocationsWithinRadiusSq(
        myLoc, 2);
    MapLocation[] enemyTowers = controller.getEnemyTowerLocations();

    MapLocation target = null;
    double maxOre = controller.senseOre(myLoc);
    for (int i = locs.length; --i >= 0;) {
      if (!controller.isLocationOccupied(locs[i])) {
        boolean inTowerRange = false;
        for (int j = enemyTowers.length; --j >= 0;) {
          if (locs[i].distanceSquaredTo(enemyTowers[j])
              <= RobotType.TOWER.attackRadiusSquared) {
            inTowerRange = true;
            break;
          }
        }
        if (inTowerRange) {
          continue;
        }
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
      Direction[] dirs = Direction.values();
      boolean loop = true;
      while (loop) {
        int i = rnd.nextInt(8);
        if (controller.canMove(dirs[i])) {
          boolean inTowerRange = false;
          for (int j = enemyTowers.length; --j >= 0;) {
            if (locs[i].distanceSquaredTo(enemyTowers[j])
                <= RobotType.TOWER.attackRadiusSquared) {
              inTowerRange = true;
              break;
            }
          }
          if (inTowerRange) {
            continue;
          }
          controller.move(dirs[i]);
          loop = false;
        }
      }
    }
  }
}
