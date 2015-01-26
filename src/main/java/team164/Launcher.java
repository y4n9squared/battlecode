/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164;

import static team164.util.Algorithms.*;

import team164.core.AbstractRobot;
import team164.core.Channels;
import team164.core.Controller;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

import java.util.Arrays;

/**
 * A Launcher robot.
 *
 * <p>A Launcher uses potential navigation. The goal location is set as a
 * positive charge and all enemies within sensor range are negative charges.
 * Locations within attack range of enemy towers are forbidden.
 *
 * <p>A launcher will fire a missile whenever it an enemy is within sensor
 * radius, or if the launcher is withiin seige range of a tower.
 *
 * @author Yang Yang
 */
public final class Launcher extends AbstractRobot {

  private RobotInfo[] enemies;

  /**
   * Constructs a {@code Launcher} robot.
   *
   * @param c controller
   */
  public Launcher(Controller c) {
    super(c);
  }

  @Override protected void runHelper() {
    MapLocation myLoc = getLocation();
    enemies = controller.getNearbyRobots(
        RobotType.LAUNCHER.sensorRadiusSquared, controller.getOpponentTeam());

    if (controller.getMissileCount() > 0 && enemies.length > 0) {
      // TODO: Also launch missile if we are within seige distance of tower
      controller.launchMissile(getBestLaunchDirection(enemies));
    }
    if (controller.isCoreReady()) {
      boolean attacking = mySpawnRound
          < controller.readBroadcast(Channels.ATTACKERS_MAX_SPAWN_ROUND);

      int channel = attacking ? Channels.ATTACK_TARGET
          : Channels.DEFENSE_TARGET;

      MapLocation newTarget = intToLocation(
          controller.readBroadcast(channel),
          controller.getHQLocation());

      if (!newTarget.equals(target)) {
        useBugNavigator = false;
        target = newTarget;
      } else if (myLoc.distanceSquaredTo(target) < bugInitialDistanceSquared) {
        useBugNavigator = false;
      }

      if (useBugNavigator) {
        if (!moveLikeABug()) {
          useBugNavigator = false;
        }
      }

      if (!useBugNavigator) {
        moveWithPotential();
      }
    }
  }

  private void moveWithPotential() {
    MapLocation[] locs = getTraversableAdjacentMapLocations(true);
    MapLocation[] pos = new MapLocation[] { target };
    double[] posCharges = new double[] { 1.0 };
    MapLocation[] neg = getRobotLocations(enemies);
    double[] negCharges = new double[enemies.length];
    Arrays.fill(negCharges, 1.0);
    MapLocation target = maxPotentialMapLocation(
        locs, pos, posCharges, neg, negCharges);
    if (target != null || !controller.move(myLoc.directionTo(target))) {
      startBugNavigation();
    }
  }

  private Direction getBestLaunchDirection(RobotInfo[] enemies) {
    MapLocation myLoc = getLocation();
    // TODO: Don't be retarded
    return myLoc.directionTo(enemies[0].location);
  }
}
