/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164;

import team164.core.AbstractRobot;
import team164.core.Channels;
import team164.core.Controller;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

/**
 * A Missile robot. Explodes when within range of any enemy robot.
 *
 * @author Shariq Hashme
 */

public final class Missile {

  private Controller controller;

  /**
   * Constructs a {@code Missile} robot.
   *
   * @param c controller
   */
  public Missile(Controller c) {
    controller = c;
  }

  public void run() {
    Team opponentTeam = controller.getOpponentTeam();
    // reduce bytecode usage
    while (true) {
      // no use destroying without enemies close by
      RobotInfo[] enemies = controller.getNearbyRobots(
            RobotType.MISSILE.attackRadiusSquared, opponentTeam);
      if (enemies.length != 0) {
        controller.explode();
        controller.yield();
      }
      MapLocation location = controller.getLocation();
      enemies = controller.getNearbyRobots(
          RobotType.MISSILE.sensorRadiusSquared, controller.getOpponentTeam());
      if (enemies.length > 2) {
// more than this runs into bytecode limit
// very close to bytecode limit: only 10 bytecodes left!
        int closestEnemy = 0;
        int closestDistance = 25;
        // sensorRadiusSquared is 24
        for (int i = 0; i < 3; i++) {
          int distanceTo = location.distanceSquaredTo(enemies[i].location);
          if (distanceTo < closestDistance) {
            closestDistance = distanceTo;
            closestEnemy = i;
          }
        }
        controller.move(location.directionTo(enemies[closestEnemy].location));
        controller.yield();
      } else if (enemies.length == 2) {
        int distance0 = location.distanceSquaredTo(enemies[0].location);
        int distance1 = location.distanceSquaredTo(enemies[1].location);
        if (distance0 < distance1) {
          controller.move(location.directionTo(enemies[0].location));
        } else {
          controller.move(location.directionTo(enemies[1].location));
        }
        controller.yield();
      } else if (enemies.length == 0) {
        //TODO : don't just move randomly?
        controller.move(Direction.values()[controller.getID() % 8]);
        controller.yield();
      } else {
        controller.move(location.directionTo(enemies[0].location));
        controller.yield();
      }
    }
  }

}
