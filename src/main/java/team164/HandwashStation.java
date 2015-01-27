/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164;

import team164.core.Controller;

/**
 * Lol.
 *
 * @author Holman Gao
 */

public final class HandwashStation {

  private Controller controller;

  /**
   * Constructs a {@code HandwashStation} robot.
   *
   * @param c controller
   */
  public HandwashStation(Controller c) {
    controller = c;
    controller.setIndicatorString(0, "WOO IM ALIVE!");
  }

  /**
   * Entry-point for Battlecode server.
   */
  public void run() {
    while (true) {
      controller.yield();
    }
  }

}
