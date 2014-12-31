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

public final class Soldier extends AbstractRobot {

  private final PotentialNavigator navigator;
  private final PotentialField field;

  public Soldier(Controller c) {
    super(c);
    field = new PotentialField(controller.getTerrainMap(),
        controller.getAttackRadiusMaxSquared());
    navigator = new PotentialNavigator(field, controller.getTerrainMap());
  }

  @Override protected void runHelper() {
    updatePotentialField();
    Direction dir = navigator.getNextStep(controller.getLocation());
    controller.move(dir);
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
