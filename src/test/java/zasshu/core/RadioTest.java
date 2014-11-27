/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import static org.junit.Assert.*;

import org.junit.*;

/**
 * Unit tests for {@link Radio}.
 *
 * @author Yang Yang
 */
public class RadioTest {

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
