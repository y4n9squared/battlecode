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
  private MapLocation[] oreLocs;
  private double[] oreValues;

  public void updateVision(RobotInfo[] units, MapLocation[] obstacles) {
    nearbyUnits = units;
    nearbyObstacles = obstacles;
  }

  public void updateTrail(MapLocation[] trail) {
    this.trail = trail;
  }

  public void updateOre(MapLocation[] oreLocs, double[] oreValues) {
    this.oreLocs = oreLocs;
    this.oreValues = oreValues;
  }

  public double lookupOre(MapLocation loc) {
    for (int i = oreLocs.length; --i >= 0;) {
      if (oreLocs[i].equals(loc)) {
        return oreValues[i];
      }
    }

    return 0.0;
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
