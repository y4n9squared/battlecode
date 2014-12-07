/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.*;

import battlecode.common.*;

public final class Soldier extends AbstractRobot {

  private final Navigator navigator;

  public Soldier(Controller c) {
    super(c);
    navigator = new BugNavigator(controller.getMap());
    navigator.setDestination(controller.enemySpawn());
  }

  @Override protected void runHelper() {
    Direction dir = navigator.getNextStep(controller.getLocation());
    controller.move(dir);
  }
}
