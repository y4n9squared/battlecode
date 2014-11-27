/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.*;

import battlecode.common.*;

import java.io.*;

public final class HQ extends AbstractRobot {

  public HQ(Controller controller) {
    super(controller);
  }

  @Override protected void runHelper() {
    if (Clock.getRoundNum() == 50) {
      radio.broadcast(0, new SerializableObject());
    }
    if (controller.senseRobotCount() < 1) {
      Direction dir = controller.enemyDirection();
      controller.spawn(dir);
    }
  }
}
