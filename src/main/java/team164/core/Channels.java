/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package team164.core;

/**
 * A class of channel constants.
 *
 * @author Yang Yang
 */
public class Channels {

  public static final int NUM_BEAVERS = 100;

  /**
   * Following choices exist for value in this channel.
   * - 0 signifying to attack HQ
   * - index of tower starting at 1, so 1 => tower[0]
   */
  public static final int ATTACK_TARGET_INDEX = 200;

  public static final int ATTACK_DISTANCE = 202;

  private Channels() {
  }
}
