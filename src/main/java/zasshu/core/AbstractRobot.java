/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

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
}
