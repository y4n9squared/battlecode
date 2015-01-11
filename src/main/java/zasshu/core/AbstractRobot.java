/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import battlecode.common.Direction;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

/**
 * Abstract skeletal implementation of the {@code Robot} interface.
 *
 * @author Holman Gao
 * @author Yang Yang
 */
public abstract class AbstractRobot implements Robot {

  protected final Controller controller;
  protected final GameState gameState;

  protected AbstractRobot(Controller ctrl) {
    controller = ctrl;
    gameState = new GameState();
  }

  @Override public void run() {
    while (true) {
      try {
        runHelper();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        controller.yield();
      }
    }
  }

  protected abstract void runHelper();

  /**
   * Attacks the lowest {@code target} in range.
   *
   * @return {@code true} if a robot was attacked
   */
  protected boolean attackLowest() {
    RobotInfo[] enemies = controller.nearbyAttackableEnemies();
    if (enemies.length == 0) {
      return false;
    }

    int indexToAttack = 0;
    double minHealth = Double.POSITIVE_INFINITY;

    for (int i = enemies.length; --i >= 0;) {
      double health = enemies[i].health;
      if (health < minHealth) {
        indexToAttack = i;
        minHealth = health;
      }
    }
    return controller.attack(enemies[indexToAttack]);
  }

  protected int teammatesOfType(RobotType type) {
    RobotInfo[] robots = controller.getNearbyRobots();

    int counter = 0;
    for (int i = robots.length; --i >= 0;) {
      if (type == robots[i].type && controller.getTeam() == robots[i].team) {
        counter++;
      }
    }
    return counter;
  }

  /**
   * Returns the direction to the enemy spawn location.
   *
   * @return direction to enemy spawn location
   */
  protected Direction enemyDirection() {
    return controller.getLocation().directionTo(controller.enemySpawn());
  }

}
