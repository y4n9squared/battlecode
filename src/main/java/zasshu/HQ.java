/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import zasshu.core.AbstractRobot;
import zasshu.core.Controller;

public final class HQ extends AbstractRobot {

  public HQ(Controller controller) {
    super(controller);
  }

  @Override protected void runHelper() {
    if (controller.isWeaponReady()) {
      // TODO: Attack intelligently
    }

    if (controller.isCoreReady()) {
      // TODO: Spawn beavers if there are less than desired number
    }

    // TODO: Transfer supply
  }
}
