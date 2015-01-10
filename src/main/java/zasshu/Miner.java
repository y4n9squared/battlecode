/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.AbstractRobot;
import zasshu.core.Controller;
import zasshu.core.FieldConfiguration;
import zasshu.core.InfluenceField;
import zasshu.core.PotentialField;
import zasshu.core.PotentialNavigator;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;

public final class Miner extends AbstractRobot {

  private final PotentialNavigator navigator;
  private final PotentialField potentialField;
  private final InfluenceField influenceField;

  private int mineCounter = 0;

  /**
   * Constructs a {@code Beaver} object.
   *
   * @param c controller
   */
  public Miner(Controller c) {
    super(c);
    potentialField = new PotentialField(gameState, new FieldConfiguration(
          c.getTeam(), c.getAttackRadiusSquared(), RobotType.BEAVER));
    influenceField = new InfluenceField();
    navigator = new PotentialNavigator(potentialField);
  }

  @Override protected void runHelper() {
    if (controller.isCoreReady()) {
      if (mineCounter % 2 == 0) {
        controller.mine();
      } else {
        move();
      }

      mineCounter++;
    }
  }

  private void move() {
    updateOreState();

    MapLocation myLoc = controller.getLocation();
    Direction[] dirs = Direction.values();
    for (int i = 8; --i >= 0;) {
      if (!controller.canMove(dirs[i])) {
        dirs[i] = Direction.NONE;
      }
    }

    Direction dir = navigator.getNextStep(controller.getLocation(), dirs);
    controller.move(dir);
  }

  private void updateInfluenceField() {
    influenceField.clear();
    influenceField.addTeammate(controller.mySpawn());
    MapLocation[] teammates = controller.nearbyTeammateLocations();
    for (int i = teammates.length; --i >= 0;) {
      influenceField.addTeammate(teammates[i]);
    }

    influenceField.addEnemy(controller.enemySpawn());
    MapLocation[] enemies = controller.nearbyEnemyLocations();
    for (int i = enemies.length; --i >= 0;) {
      influenceField.addEnemy(enemies[i]);
    }
  }

  private void updateOreState() {
    MapLocation[] locs = MapLocation.getAllMapLocationsWithinRadiusSq(
        controller.getLocation(), 2);
    double[] oreValues = new double[locs.length];

    for (int i = locs.length; --i >= 0;) {
      oreValues[i] = controller.senseOre(locs[i]);
    }

    gameState.updateOre(locs, oreValues);
  }
}
