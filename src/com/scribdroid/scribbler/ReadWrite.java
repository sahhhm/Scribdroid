package com.scribdroid.scribbler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ReadWrite {
  private static final String TAG = "ReadWrite";
  private static final boolean D = true;

  private static final int PACKET_LENGTH = 9;
  
  public static void _write(BluetoothSocket sock, boolean connected, byte[] values) {
    if (connected) {
      OutputStream out = null;
      try {
        out = sock.getOutputStream();
      } catch (IOException e) {
        if (D) Log.e(TAG, "Error getting output stream!");
      }

      // Ensure correctly-sized packet
      ByteBuffer b = ByteBuffer.allocate(PACKET_LENGTH).put(values);
      while (b.position() < b.limit()) {
        b.put((byte) 0);
      }

      try {
        out.write(b.array());
        if (D) Log.d(TAG, "Wrote: " + ba2s(b.array()));
      } catch (IOException e) {
        if (D) Log.e(TAG, "Error Writing: " + ba2s(b.array()));
      }

    }
  }

  public static void _writeFluke(BluetoothSocket sock, boolean connected, byte[] values) {
    if (connected) {
      OutputStream out = null;
      try {
        out = sock.getOutputStream();
      } catch (IOException e) {
        if (D) Log.e(TAG, "Error getting output stream!");
      }

      // Only write what the code-- no message size specified
      ByteBuffer b = ByteBuffer.allocate(values.length).put(values);

      try {
        out.write(b.array());
        if (D) Log.d(TAG, "Wrote[Fluke]: " + ba2s(b.array()));
      } catch (IOException e) {
        if (D) Log.e(TAG, "Error Writing: " + ba2s(b.array()));
      }
    }
  }

  public static byte[] _read(BluetoothSocket sock, Boolean connected, int numBytes) {
    ByteBuffer buf = ByteBuffer.allocate(numBytes);

    if (connected) {
      InputStream in = null;
      try {
        in = sock.getInputStream();
      } catch (IOException e1) {
        if (D) Log.d(TAG, "Error Opening input stream");
      }

      byte[] fake = new byte[numBytes];
      for (int i = 0; i < numBytes; i++)
        fake[i] = 0;

      byte[] buffer = new byte[numBytes];
      int read = 0;

      try {
        while (buf.hasRemaining()) {
          read = in.read(buffer);
          
          for (int i = 0; i < read; i++) {
            int b = buffer[i] & 0xff;
            try {
              buf.put((byte) b);
            } catch (BufferOverflowException be) {
              // Fix bug where robot sends too much information back
              //if (D) Log.e(TAG, be.getMessage().toString());
              if (D) Log.i(TAG, "Trying to gracefully disconnect rather than crash");
              return null;
            }
          }
        }
        if (D) Log.d(TAG, "Read " + buf.position() + " bytes: " + ba2s(buf.array()));
      } catch (IOException e) {
        if (D) Log.e(TAG, "Error Reading" + e.getMessage());
      }
    }
    return buf.array();
  }

  private static String ba2s(byte[] ba) {
    StringBuilder sb = new StringBuilder("[ ");
    for (byte b : ba) {
      sb.append(Integer.toHexString(b & 0xff)).append(" ");
    }
    sb.append("]");
    return sb.toString();
  }
}
