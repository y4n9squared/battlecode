/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import battlecode.common.*;

import java.io.*;

/**
 * A {@code Radio} that provides an {@code Object} abstraction on top of
 * Battlecode's channels which only allow storing integers.
 *
 * A {@code Radio} stores an object's serialized bytestream across multiple
 * channels.
 *
 * @author Yang Yang
 */
public final class Radio {

  private final Controller controller;

  public Radio(Controller c) {
    controller = c;
  }

  public void broadcast(int channel, Object obj) {
    try {
      int size = writeChannels(toByteArray(obj), channel);
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
  }

  public Object recv(int channel) {
    Object obj = null;
    // TODO(Yang): Handle size. Hardcoded for now.
    byte[] arr = readChannels(channel, 26);
    ByteArrayInputStream byteStream = new ByteArrayInputStream(arr);
    try {
      ObjectInputStream stream = new ObjectInputStream(byteStream);
      try {
        obj = stream.readObject();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return obj;
  }

  private int writeChannels(byte[] arr, int start) {
    arr = pad(arr);  // ~200 bytecodes
    // TODO(Yang): Need to do some check here to determine if the array being
    // passed is too large (i.e. will overflow channel buffer).
    int numWritten = 0;
    for (int i = arr.length / 4; --i >= 0;) {
      byte[] tmp = new byte[4];
      System.arraycopy(arr, 4 * i, tmp, 0, 4);
      int data = pack(tmp);
      if (controller.broadcast(start + i, data)) {
        ++numWritten;
      }
    }
    return numWritten;
  }

  private byte[] readChannels(int start, int size) {
    byte[] arr = new byte[size * 4];
    for (int i = 0; i < size; ++i) {
      byte[] val = unpack(controller.readBroadcast(start + i));
      arr[4 * i] = val[0];
      arr[4 * i + 1] = val[1];
      arr[4 * i + 2] = val[2];
      arr[4 * i + 3] = val[3];
    }
    return strip(arr);
  }

  private byte[] toByteArray(Object obj) throws IOException {
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    ObjectOutputStream stream = new ObjectOutputStream(byteStream);
    stream.writeObject(obj);
    stream.close();
    return byteStream.toByteArray();
  }

  /**
   * Appends a pad to the byte array according to the PKCS#5 padding scheme.
   *
   * Cost: ~200 bytecodes
   *
   * @param arr unpadded byte array
   * @return PKCS#5-padded byte array
   */
  static byte[] pad(byte[] arr) {
    int paddedLength = (arr.length | 3) + 1;
    byte val = (byte) (paddedLength - arr.length);
    byte[] padded = new byte[paddedLength];
    System.arraycopy(arr, 0, padded, 0, arr.length);
    for (int i = 0; i < val; ++i) {
      padded[arr.length + i] = val;
    }
    return padded;
  }

  /**
   * Strips the PKCS#5 padding from the specified byte array.
   *
   * Throws {@code IllegalArgumentException} if the PKCS#5 pad is invalid.
   *
   * @param arr PKCS#5-padded byte array
   * @return unpadded byte array
   */
  static byte[] strip(byte[] arr) {
    int unpaddedLength = arr.length - arr[arr.length - 1];
    for (int i = unpaddedLength; i < arr.length; ++i) {
      if (arr[i] != arr[arr.length - 1]) {
        throw new IllegalArgumentException("Invalid PKCS#5 padding.");
      }
    }
    byte[] unpadded = new byte[unpaddedLength];
    System.arraycopy(arr, 0, unpadded, 0, unpaddedLength);
    return unpadded;
  }

  /**
   * Cost: 33 bytecodes
   */
  static int pack(byte[] arr) {
    return (((0xFF & arr[0]) << 24) | ((0xFF & arr[1]) << 16) | ((0xFF & arr[2]) << 8) | (0xFF & arr[3]));
  }

  static byte[] unpack(int val) {
    byte[] arr = new byte[4];
    arr[0] = (byte) (val >> 24);
    arr[1] = (byte) (val >> 16);
    arr[2] = (byte) (val >> 8);
    arr[3] = (byte) val;
    return arr;
  }
}
