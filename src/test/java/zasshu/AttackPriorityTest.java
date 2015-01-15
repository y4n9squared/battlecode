/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu;

import static org.junit.Assert.*;

import zasshu.core.AbstractRobot;

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
  public static final Set<String> requiredTypes = new HashSet<String>() {{
    for (RobotType t : RobotType.values()) {
      add(t.name());
    }
  }};

  @SuppressWarnings("serial")
  public static final List<Class<? extends AbstractRobot>> robotTypes
      = new ArrayList<Class<? extends AbstractRobot>>() {{
    add(Drone.class);
    add(Soldier.class);
  }};
  // CHECKSTYLE.ON

  @Test public void testAttackPriority() throws Exception {
    for (Class<? extends AbstractRobot> robotType : robotTypes) {
      Class<?>[] classes = robotType.getDeclaredClasses();
      for (Class<?> cls : classes) {
        if (cls.getName().contains("$AttackPriority")) {
          Class<?> enumCls = Class.forName(cls.getName());
          for (Object obj : enumCls.getEnumConstants()) {
            assertTrue(requiredTypes.contains(obj.toString()));
          }
        }
      }
    }
  }
}
