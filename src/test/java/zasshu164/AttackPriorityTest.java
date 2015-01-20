/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu164;

import static org.junit.Assert.*;

import zasshu164.core.AbstractRobot;

import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

import org.junit.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class AttackPriorityTest {

  // CHECKSTYLE.OFF: False positives for double-brace initilization
  @SuppressWarnings("serial")
  public static final List<Class<? extends AbstractRobot>> robotTypes
      = new ArrayList<Class<? extends AbstractRobot>>() {{
    add(Drone.class);
    add(Soldier.class);
    add(Tank.class);
  }};
  // CHECKSTYLE.ON

  @Test public void testAttackPriority() throws Exception {
    for (Class<? extends AbstractRobot> robotType : robotTypes) {
      Class<?>[] classes = robotType.getDeclaredClasses();
      for (Class<?> cls : classes) {
        if (cls.getName().contains("$AttackPriority")) {
          Class<?> enumCls = Class.forName(cls.getName());

          HashSet<String> myTypes = new HashSet<String>();
          for (Object obj : enumCls.getEnumConstants()) {
            myTypes.add(obj.toString());
          }

          for (RobotType t : RobotType.values()) {
            assertTrue(myTypes.contains(t.name()));
          }
        }
      }
    }
  }
}
