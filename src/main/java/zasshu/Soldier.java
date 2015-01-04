/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.AbstractRobot;
import zasshu.core.Controller;
import zasshu.core.Navigator;
import zasshu.core.PotentialField;
import zasshu.core.PotentialNavigator;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Robot;

public final class Soldier extends AbstractRobot {

  private final PotentialNavigator navigator;
  private final PotentialField field;

  public Soldier(Controller c) {
    super(c);
    field = new PotentialField(controller.getAttackRadiusMaxSquared());
    navigator = new PotentialNavigator(field, controller.getTerrainMap());
  }

  @Override protected void runHelper() {
    if (controller.isActive()) {
      Robot[] enemies = controller.nearbyAttackableEnemies();
      if (enemies.length > 0) {
        controller.attack(enemies[0]);
      } else {
        updatePotentialField();
        Direction dir = navigator.getNextStep(controller.getLocation());
        controller.move(dir);
      }
    }

    controller.yield();
  }

  private void updatePotentialField() {
    field.clear();
    field.addSource(controller.enemySpawn());
    MapLocation[] enemies = controller.nearbyEnemyLocations();
    for (int i = enemies.length; --i >= 0;) {
      field.addSource(enemies[i]);
    }
    // TODO: Add friendly obstacles
  }
}
