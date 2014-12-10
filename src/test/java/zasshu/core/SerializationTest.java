/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static zasshu.core.Serialization.*;

import battlecode.common.*;

import org.junit.*;
import org.mockito.stubbing.*;
import org.mockito.invocation.*;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Unit tests for the {@link Serialization} library.
 *
 * @author Holman Gao
 * @author Yang Yang
 */
public class SerializationTest {

  @Test public void testPad0() {
    byte[] arr = {};
    byte[] expected = {0x04, 0x04, 0x04, 0x04};
    assertArrayEquals(expected, pad(arr));
  }

  @Test public void testPad1() {
    byte[] arr = {0x01};
    byte[] expected = {0x01, 0x03, 0x03, 0x03};
    assertArrayEquals(expected, pad(arr));
  }

  @Test public void testPad3() {
    byte[] arr = {0x01, 0x02, 0x03};
    byte[] expected = {0x01, 0x02, 0x03, 0x01};
    assertArrayEquals(expected, pad(arr));
  }

  @Test public void testPad4() {
    byte[] arr = {0x01, 0x02, 0x03, 0x04};
    byte[] expected = {0x01, 0x02, 0x03, 0x04, 0x04, 0x04, 0x04, 0x04};
    assertArrayEquals(expected, pad(arr));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnpad1() {
    byte[] arr = {0x01, 0x02, 0x03, 0x02};
    unpad(arr);
  }

  @Test public void testUnpad2() {
    byte[] arr = {0x01, 0x02, 0x02, 0x02};
    byte[] expected = {0x01, 0x02};
    assertArrayEquals(expected, unpad(arr));
  }

  @Test public void testUnpad3() {
    byte[] arr = {0x01, 0x02, 0x03, 0x04, 0x04, 0x04, 0x04, 0x04};
    byte[] expected = {0x01, 0x02, 0x03, 0x04};
    assertArrayEquals(expected, unpad(arr));
  }
}
