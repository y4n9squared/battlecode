/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.AbstractRobot;
import zasshu.core.Controller;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public final class Soldier extends Unit {

  public Soldier(Controller c) {
    super(c);
  }

  @Override protected void runHelper() {
    if (getInfluence(controller.getLocation()) > 0) {
      attack();
    } else {
      retreat();
    }
  }

  @Override protected double getPotential(MapLocation loc) {
    double positive = 0;
    double negative = 0;

    RobotInfo[] units = controller.getNearbyRobots();
    for (int i = units.length; --i >= 0;) {
      double force = computeForce(loc, units[i]);
      if (force > 0) {
        positive = Math.max(positive, force);
      } else {
        negative += force;
      }
    }
    return positive + negative;
  }

  private double computeForce(MapLocation loc, RobotInfo unit) {
    double potential = 0;
    int d = loc.distanceSquaredTo(unit.location);
    if (unit.team == controller.getOpponentTeam()) {
      switch (unit.type) {
        case BEAVER:
        case SOLDIER:
        case BASHER:
          return computePositiveForce(d);
        case TOWER:
          return 10 * computePositiveForce(d);
        default:
          return 0;
      }
    }
    return computeNegativeForce(d);
  }

  private double computePositiveForce(int d) {
    // TODO use attackRadiusSquared
    return 10.0 / (Math.abs(d - 4.0) + 1);
  }

  private double computeNegativeForce(int d) {
    return -5.0 / d;
  }

  private void attack() {
    boolean attacked = false;
    if (controller.isWeaponReady()) {
      attacked = attackLowest();
    }

    if (!attacked && controller.isCoreReady()) {
      MapLocation myLoc = controller.getLocation();
      Direction[] dirs = Direction.values();
      for (int i = 8; --i >= 0;) {
        if (!controller.canMove(dirs[i])) {
          dirs[i] = Direction.NONE;
        }
      }

      gameState.updateVision(
          controller.nearbyAttackableEnemies(), new MapLocation[0]);

      Direction dir = getNextStep(controller.getLocation(), dirs);
      controller.move(dir);
    }
  }

  /* TODO */
  private void retreat() {
  }
}
