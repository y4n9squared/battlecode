package zasshu.core;

import zasshu.util.MapLocationSet;

import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;

/**
 * A container for dynamic game data.
 *
 * @author Yang Yang
 */
public class GameState {

  private RobotInfo[] nearbyUnits;
  private MapLocation[] nearbyObstacles;
  private MapLocation[] trail;

  public void updateVision(RobotInfo[] units, MapLocation[] obstacles) {
    nearbyUnits = units;
    nearbyObstacles = obstacles;
  }

  public void updateTrail(MapLocation[] trail) {
    this.trail = trail;
  }

  public RobotInfo[] nearbyUnits() {
    return nearbyUnits;
  }

  public MapLocation[] nearbyObstacles() {
    return nearbyObstacles;
  }

  public MapLocation[] trail() {
    return trail;
  }
}
