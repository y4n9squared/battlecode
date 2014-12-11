/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static zasshu.core.Controller.*;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.Team;

import org.junit.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Unit tests for {@link Controller}.
 *
 * @author Holman Gao
 * @author Yang Yang
 */
public class ControllerTest {

  private static class SerializableObject implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String val;

    public SerializableObject(String str) {
      val = str;
    }

    @Override public int hashCode() {
      return 17 + 31 * val.hashCode();
    }

    @Override public boolean equals(Object obj) {
      if (obj == null || this.getClass() != obj.getClass()) {
        return false;
      }
      return val.equals(((SerializableObject) obj).val);
    }
  }

  private RobotController rc;
  HashMap<Integer, Integer> mockChannels;

  @Before public void setUp() {
    rc = mock(RobotController.class, RETURNS_SMART_NULLS);
    mockChannels = new HashMap<Integer, Integer>();
    try {
      doAnswer(new Answer<Void>() {
        @Override public Void answer(InvocationOnMock invocation) {
          Object[] args = invocation.getArguments();
          mockChannels.put((Integer)(args[0]), (Integer)(args[1]));
          return null;
        }
      }).when(rc).broadcast(anyInt(), anyInt());

      doAnswer(new Answer<Object>() {
        @Override public Object answer(InvocationOnMock invocation) {
          Object[] args = invocation.getArguments();
          return mockChannels.get((Integer)(args[0])).intValue();
        }
      }).when(rc).readBroadcast(anyInt());
    } catch (GameActionException e) {
      fail("GameActionException thrown.");
    }

    when(rc.getMapWidth()).thenReturn(20);
    when(rc.getMapHeight()).thenReturn(20);
    when(rc.getTeam()).thenReturn(Team.A);
  }

  @Test public void testReadWriteChannels() {
    Controller controller = new Controller(rc);
    SerializableObject obj = new SerializableObject("unique ID");
    int size = controller.writeChannels(77, obj);
    if (size == -1) {
      fail("Error writing to channels.");
    }
    assertEquals(obj, controller.readChannels(77, size));
  }

  @Test public void testPack0() {
    byte[] arr = {0x00, 0x00, 0x00, 0x01};
    assertEquals(1, pack(arr));
  }

  @Test public void testUnpack0() {
    byte[] expected = {0x00, 0x00, 0x00, 0x01};
    assertArrayEquals(expected, unpack(1));
  }

  @Test public void testPackUnpack() {
    assertEquals(191, pack(unpack(191)));
  }
}
