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

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;

public final class Soldier extends Unit {

  private final PotentialField potentialField;
  private final InfluenceField influenceField;

  public Soldier(Controller c) {
    super(c);
    potentialField = new PotentialField(gameState, new FieldConfiguration(
          c.getTeam(), c.getAttackRadiusSquared(), RobotType.SOLDIER));
    influenceField = new InfluenceField();
  }

  @Override protected void runHelper() {
    updateInfluenceField();

    if (influenceField.influence(controller.getLocation()) > 0) {
      attack();
    } else {
      retreat();
    }
  }

  private void attack() {
    boolean attacked = false;
    if (controller.isWeaponReady()) {
      attacked = controller.attackLowest();
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

      Direction dir = getNextStep(
          potentialField, controller.getLocation(), dirs);
      controller.move(dir);
    }
  }

  /* TODO */
  private void retreat() {
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
