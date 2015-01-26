/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164.core;

import battlecode.common.RobotType;

/**
 * A class of channel constants.
 *
 * @author Yang Yang
 */
public class Channels {

  public static final int TARGET_LOCATION = 200;
  public static final int ATTACK_DISTANCE = 202;

  public static final int TOWER_HELP = 300;

  /**
   * Start channel for attendance. Channel {@code ATTENDANCE +
   * RobotType.ordinal()} is the number of allied robots of type {@code
   * RobotType}.
   */
  private static final int ATTENDANCE_START = 100;

  /**
   * Returns the channel number for the attedance of the specified type.
   *
   * @param type robot type
   * @return channel number
   */
  public static int getCountChannel(RobotType type) {
    return ATTENDANCE_START + type.ordinal();
  }

  private Channels() {
  }
}
