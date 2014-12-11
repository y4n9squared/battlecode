/*
 * MIT Battlecode 2015
 * Copyright © 2014-2015 Holman Gao, Yang Yang. All Rights Reserved.
 */

package zasshu.core;

import java.io.*;

/**
 * {@code Serialization} is a static library for serializing and deserialing
 * objects to byte arrays so that they can be broadcast to Battlecode channels.
 * <p>
 * Unlike default Java serialization, byte arrays handeled through this library
 * always coincide with 4-byte words. PKCS#5 padding is used when the object's
 * serialized length is less than the boundary for a word.
 *
 * @author Yang Yang
 */
public final class Serialization {

  /**
   * Returns a deserialzed object. If an error occurs during deserialization or
   * the pad is invalid, the return value will be {@code null}.
   *
   * @param arr PCKS#5-padded byte array to be deserialized
   */
  public static Object fromByteArray(byte[] arr) {
    Object obj = null;
    ByteArrayInputStream byteStream = new ByteArrayInputStream(unpad(arr));
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

  /**
   * Returns a PCKS#5-padded byte array of the serialized object.
   *
   * @param obj object to be serialized
   */
  public static byte[] toByteArray(Object obj) {
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    try {
      ObjectOutputStream stream = new ObjectOutputStream(byteStream);
      stream.writeObject(obj);
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return pad(byteStream.toByteArray());
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
  static byte[] unpad(byte[] arr) {
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
}
