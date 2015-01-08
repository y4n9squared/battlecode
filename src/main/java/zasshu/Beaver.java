/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.AbstractRobot;
import zasshu.core.Controller;
import zasshu.core.InfluenceField;
import zasshu.core.PotentialField;
import zasshu.core.PotentialNavigator;

import battlecode.common.Direction;
import battlecode.common.MapLocation;

public final class Beaver extends AbstractRobot {

  private final PotentialNavigator navigator;
  private final PotentialField potentialField;
  private final InfluenceField influenceField;

  /**
   * Constructs a {@code Beaver} object.
   *
   * @param c controller
   */
  public Beaver(Controller c) {
    super(c);
    potentialField = new PotentialField(controller.getAttackRadiusSquared());
    influenceField = new InfluenceField();
    navigator = new PotentialNavigator(potentialField);
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
      updatePotentialField();

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

  private void updatePotentialField() {
    potentialField.clear();
    potentialField.addSource(controller.enemySpawn());
    MapLocation[] enemies = controller.nearbyEnemyLocations();
    for (int i = enemies.length; --i >= 0;) {
      potentialField.addSource(enemies[i]);
    }
    // TODO: Add friendly obstacles
  }
}
