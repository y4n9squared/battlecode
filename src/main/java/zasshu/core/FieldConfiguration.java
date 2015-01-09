package zasshu.core;

import battlecode.common.RobotType;
import battlecode.common.Team;

/**
 * A collection of field parameters.
 *
 * @author Yang Yang
 */
public class FieldConfiguration {
  private final Team team;
  private final int attackRadiusSquared;
  private final RobotType robotType;

  public FieldConfiguration(Team t, int radius, RobotType type) {
    team = t;
    attackRadiusSquared = radius;
    robotType = type;
  }

  public Team getTeam() {
    return team;
  }

  public int getAttackRadiusSquared() {
    return attackRadiusSquared;
  }

  public RobotType getRobotType() {
    return robotType;
  }
}
