package team164.util;

import battlecode.common.Clock;
import battlecode.common.RobotType;

/**
 * A class to time elapsed bytecode usage across multiple rounds.
 *
 * @author Yang Yang
 */
public final class Timer {

  private final RobotType type;
  private int startByteCount;
  private int startRound;

  /**
   * Constructs a timer object for the given robot type.
   *
   * @param t robot type
   */
  public Timer(RobotType t) {
    type = t;
  }

  /**
   * Starts the timer.
   */
  public void start() {
    startByteCount = Clock.getBytecodeNum();
    startRound = Clock.getRoundNum();
  }

  /**
   * Stops the timer.
   *
   * @return elapsed bytecode usage since start
   */
  public int stop() {
    return (type.bytecodeLimit - startByteCount)
      + (Clock.getRoundNum() - startRound - 1) * type.bytecodeLimit
      + Clock.getBytecodeNum();
  }
}
