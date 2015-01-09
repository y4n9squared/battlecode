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

public final class Beaver extends AbstractRobot {

  private final PotentialNavigator navigator;
  private final PotentialField potentialField;
  private final InfluenceField influenceField;

  private int buildCounter = 0;

  /**
   * Constructs a {@code Beaver} object.
   *
   * @param c controller
   */
  public Beaver(Controller c) {
    super(c);
    potentialField = new PotentialField(gameState, new FieldConfiguration(
          c.getTeam(), c.getAttackRadiusSquared(), RobotType.BEAVER));
    influenceField = new InfluenceField();
    navigator = new PotentialNavigator(potentialField);
  }

  @Override protected void runHelper() {
    if (controller.isCoreReady()) {
      boolean built = false;

      if (buildCounter > 3) {
        if (controller.canAffordToBuild(RobotType.BARRACKS)) {
          build(RobotType.BARRACKS);
          built = true;
        }

        buildCounter -= 2;
      }

      if (!built) {
        if (buildCounter % 2 == 0) {
          controller.mine();
        } else {
          move();
        }

        buildCounter++;
      }
    }
  }

  private void build(RobotType type) {
    updateInfluenceField();

    MapLocation myLoc = controller.getLocation();
    Direction[] dirs = Direction.values();
    Direction bestDir = Direction.NONE;
    double maxInfluence = 0.0;
    for (int i = 8; --i >= 0;) {
      if (!controller.canBuild(dirs[i], type)) {
        continue;
      }

      double influence = influenceField.influence(myLoc.add(dirs[i]));

      if (influence > maxInfluence) {
        bestDir = dirs[i];
        maxInfluence = influence;
      }
    }

    controller.build(bestDir, type);
  }

  private void move() {
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
}
