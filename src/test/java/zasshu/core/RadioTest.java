/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import battlecode.common.*;

import org.junit.*;
import org.mockito.stubbing.*;
import org.mockito.invocation.*;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Unit tests for {@link Radio}.
 *
 * @author Yang Yang
 */
public class RadioTest {

  private static class SerializableObject implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String val = "unique id";

    @Override public int hashCode() {
      return 17 + 31 * val.hashCode();
    }

    @Override public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      return this.val == ((SerializableObject) obj).val;
    }
  }

  @Test public void identity() {
    RobotController mockRC = mock(RobotController.class, RETURNS_SMART_NULLS);

    try {
      final HashMap<Integer, Integer> mockChannels = new HashMap<Integer, Integer>();

      doAnswer(new Answer<Void>() {
        @Override public Void answer(InvocationOnMock invocation) {
          Object[] args = invocation.getArguments();
          mockChannels.put((Integer)(args[0]), (Integer)(args[1]));
          return null;
        }
      }).when(mockRC).broadcast(anyInt(), anyInt());

      doAnswer(new Answer<Object>() {
        @Override public Object answer(InvocationOnMock invocation) {
          Object[] args = invocation.getArguments();
          return mockChannels.get((Integer)(args[0])).intValue();
        }
      }).when(mockRC).readBroadcast(anyInt());

      mockRC.broadcast(5, 4);
      assertEquals(mockRC.readBroadcast(5), 4);

      Controller controller = new Controller(mockRC);
      Radio radio = new Radio(controller);

      SerializableObject obj = new SerializableObject();
      radio.broadcast(77, obj);
      assertEquals(obj, radio.recv(77));
    } catch (GameActionException e) {
      fail("GameActionException thrown");
    }
  }

  @Test public void testPad0() {
    byte[] arr = {};
    byte[] expected = {0x04, 0x04, 0x04, 0x04};
    assertArrayEquals(expected, Radio.pad(arr));
  }

  @Test public void testPad1() {
    byte[] arr = {0x01};
    byte[] expected = {0x01, 0x03, 0x03, 0x03};
    assertArrayEquals(expected, Radio.pad(arr));
  }

  @Test public void testPad3() {
    byte[] arr = {0x01, 0x02, 0x03};
    byte[] expected = {0x01, 0x02, 0x03, 0x01};
    assertArrayEquals(expected, Radio.pad(arr));
  }

  @Test public void testPad4() {
    byte[] arr = {0x01, 0x02, 0x03, 0x04};
    byte[] expected = {0x01, 0x02, 0x03, 0x04, 0x04, 0x04, 0x04, 0x04};
    assertArrayEquals(expected, Radio.pad(arr));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStrip1() {
    byte[] arr = {0x01, 0x02, 0x03, 0x02};
    Radio.strip(arr);
  }

  @Test public void testStrip2() {
    byte[] arr = {0x01, 0x02, 0x02, 0x02};
    byte[] expected = {0x01, 0x02};
    assertArrayEquals(expected, Radio.strip(arr));
  }

  @Test public void testStrip3() {
    byte[] arr = {0x01, 0x02, 0x03, 0x04, 0x04, 0x04, 0x04, 0x04};
    byte[] expected = {0x01, 0x02, 0x03, 0x04};
    assertArrayEquals(expected, Radio.strip(arr));
  }

  @Test public void testPack0() {
    byte[] arr = {0x00, 0x00, 0x00, 0x01};
    assertEquals(1, Radio.pack(arr));
  }

  @Test public void testUnpack0() {
    byte[] expected = {0x00, 0x00, 0x00, 0x01};
    assertArrayEquals(expected, Radio.unpack(1));
  }

  @Test public void testPackUnpack() {
    assertEquals(191, Radio.pack(Radio.unpack(191)));
  }
}
