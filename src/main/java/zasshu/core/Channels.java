/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

/**
 * A class of channel constants.
 *
 * @author Yang Yang
 */
public class Channels {

  public static final int NUM_BEAVERS = 100;

  /*
   * This is either:
   * - 0 signifying to attack HQ
   * - index of tower starting at 1, so 1 => tower[0]
   */
  public static final int ATTACK_TARGET_INDEX = 200;

  public static final int ATTACK_DISTANCE = 202;

  private Channels() {
  }
}
