package com.scribdroid.scribbler;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

import com.scribdroid.scribbler.SetCommands.LED;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class Scribbler {
  // Debugging
  private static final String TAG = "Scribbler";
  private static final boolean D = false;

  private String macAddress;
  private boolean connected;
  private byte[] lastSensors;
  private SetCommands setCommands;
  private GetCommands getCommands;
  private BluetoothSocket sock;
  private boolean isMoving;

  public Scribbler() {
    this(null);
  }

  public Scribbler(String aMac) {
    macAddress = aMac;
    setConnected(false);
    sock = null;
    setCommands = null;
    getCommands = null;
  }

  public boolean connect() throws Exception {
    boolean ret = false;
    BluetoothDevice scrib = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(
        macAddress);
    Method m = scrib.getClass()
        .getMethod("createRfcommSocket", new Class[] { int.class });
    sock = (BluetoothSocket) m.invoke(scrib, Integer.valueOf(1));
    if (D) Log.d(TAG, "Connecting");
    try {
      sock.connect();
      setCommands = new SetCommands(this);
      getCommands = new GetCommands(this);
      setConnected(true);
      ret = true;
      if (D) Log.d(TAG, "Connected");
    } catch (Exception e) {
      setConnected(false);
      if (D) Log.e(TAG, "Error Connecting");
      try {
        sock.close();
      } catch (Exception e2) {
        if (D) Log.e(TAG, "Error closing socket after error connecting");
      }
    }
    return ret;
  }

  public void disconnect() {
    try {
      if (sock != null) {
        sock.close();
        setConnected(false);
        if (D) Log.d(TAG, "Socket Closed. Now Disconnected.");
      }
    } catch (IOException e) {
      if (D) Log.e(TAG, "Error closing socket while disconnecting");
    }
  }

  /**
   * @param connected
   *          the connected to set
   */
  public void setConnected(boolean connected) {
    this.connected = connected;
  }

  /**
   * @return the connected
   */
  public boolean isConnected() {
    return connected;
  }

  public void setSocket(BluetoothSocket aSock) {
    sock = aSock;
  }

  public BluetoothSocket getSocket() {
    return sock;
  }

  /**
   * @param lastSensors
   *          the lastSensors to set
   */
  public void setLastSensors(byte[] lastSensors) {
    this.lastSensors = lastSensors;
  }

  /**
   * @return the lastSensors
   */
  public byte[] getLastSensors() {
    return lastSensors;
  }

  public void beep(float frequency, float duration) {
    if (setCommands != null)
      setCommands._setSpeaker((int) frequency, (int) (duration * 1000));
  }

  public void beep(float frequency1, float frequency2, float duration) {
    if (setCommands != null)
      setCommands._setSpeaker2((int) frequency1, (int) frequency2,
          (int) (duration * 1000));
  }

  public void setLED(LED position, boolean on) {
    if (setCommands != null) setCommands._setLED(position, on);
  }

  public void move(double translate, double rotate) {
    if (setCommands != null) setCommands._move(translate, rotate);
  }

  public void turnLeft(double amount) {
    if (setCommands != null) setCommands._move(0, -amount);
  }

  public void turnRight(double amount) {
    if (setCommands != null) setCommands._move(0, amount);
  }

  public void forward(double amount) {
    if (setCommands != null) setCommands._move(amount, 0);
  }

  public void backward(double amount) {
    if (setCommands != null) setCommands._move(-amount, 0);
  }

  public void setMoving(boolean isMoving) {
    this.isMoving = isMoving;
  }

  public boolean isMoving() {
    return isMoving;
  }

  public Bitmap takePicture() {
    Bitmap bm = null;
    byte[] ba;

    if (getCommands != null) {
      ba = getCommands.getPictureArray();

      bm = Bitmap.createBitmap(256, 192, Bitmap.Config.ARGB_8888);
      int w = 256;
      int h = 192;
      int vy, vu, y1v, y1u, uy, uv, y2u, y2v;
      int V = 0, Y = 0, U = 0;

      for (int i = 0; i < h; i++) {
        for (int j = 0; j < w; j++) {
          if (j >= 3) {
            vy = -1;
            vu = 2;
            y1v = -1;
            y1u = -3;
            uy = -1;
            uv = -2;
            y2u = -1;
            y2v = -3;
          } else {
            vy = 1;
            vu = 2;
            y1v = 3;
            y1u = 1;
            uy = 1;
            uv = 2;
            y2u = 3;
            y2v = 1;
          }
          if ((j % 4) == 0) {
            V = ba[i * w + j] & 0xff;
            Y = ba[i * w + j + vy] & 0xff;
            U = ba[i * w + j + vu] & 0xff;
          } else if ((j % 4) == 1) {
            Y = ba[i * w + j] & 0xff;
            V = ba[i * w + j + y1v] & 0xff;
            U = ba[i * w + j + y1u] & 0xff;
          } else if ((j % 4) == 2) {
            U = ba[i * w + j] & 0xff;
            Y = ba[i * w + j + uy] & 0xff;
            V = ba[i * w + j + uv] & 0xff;
          } else if ((j % 4) == 3) {
            Y = ba[i * w + j] & 0xff;
            U = ba[i * w + j + y2u] & 0xff;
            V = ba[i * w + j + y2v] & 0xff;
          }
          U = U - 128;
          V = V - 128;
          // Y = Y;

          bm.setPixel(j, i, Color.rgb((int) Math.max(Math.min(Y + 1.13983 * V, 255), 0),
              (int) Math.max(Math.min(Y - 0.39466 * U - 0.58060 * V, 255), 0),
              (int) Math.max(Math.min(Y + 2.03211 * U, 255), 0)));
        }
      }
    }

    return bm;
  }

  public float getBattery() {
    byte[] ba = null;
    int unmodified;
    float value = 0;
    if (getCommands != null) {
      ba = getCommands.getBattery();

      unmodified = (ba[0] & 0xFF) << 8 | ba[1] & 0xFF;
      value = unmodified / 20.9813f;

      if (D) Log.d(TAG, "getBattery -> " + value);
    }
    return value;
  }

  public int[] getIR(String type) {
    int[] ba, ret = null;

    type = type.toLowerCase();
    if (getCommands != null) {
      ba = getCommands.getIR();

      if (type.equals("left")) {
        ret = new int[1];
        ret[0] = ba[0];
      } else if (type.equals("right")) {
        ret = new int[1];
        ret[0] = ba[1];
      } else {
        ret = ba;
      }
    }
    return ret;
  }

  public int[] getLight(String type) {
    int[] ba, ret = null;

    type = type.toLowerCase();
    if (getCommands != null) {
      ba = getCommands.getLight();

      if (type.equals("left")) {
        ret = new int[1];
        ret[0] = ba[0];
      } else if (type.equals("center")) {
        ret = new int[1];
        ret[0] = ba[1];
      } else if (type.equals("right")) {
        ret = new int[1];
        ret[0] = ba[2];
      } else {
        ret = ba;
      }
    }
    return ret;
  }

  public int[] getObstacle(String type) {
    int[] ba, ret = null;

    type = type.toLowerCase();
    if (getCommands != null) {
      ba = getCommands.getObstacle();

      if (type.equals("left")) {
        ret = new int[1];
        ret[0] = ba[0];
      } else if (type.equals("center")) {
        ret = new int[1];
        ret[0] = ba[1];
      } else if (type.equals("right")) {
        ret = new int[1];
        ret[0] = ba[2];
      } else {
        ret = ba;
      }
    }
    return ret;
  }

  /**
   * Function properly gets and converts the robots name
   * 
   * @return - String representing the robots trimmed name
   */
  public String getName() {
    int[] ba;
    StringBuilder build;
    String name = null;

    if (getCommands != null) {
      // Get the proper bytes and convert them to characters
      ba = getCommands.getName();
      build = new StringBuilder(ba.length);

      for (int i = 0; i < ba.length; i++)
        build.append((char) ba[i]);
      name = build.toString().trim();
    }
    if (D) Log.d(TAG, "Scribbler Name Read: " + name);
    return name;
  }

  /**
   * Function returns all of the scribbler sensors (not fluke) in a hashmap.
   * Each associated array for a key is of variable length, depending on the
   * type of sensor. IR array has size 2; LIGHT array has size 3; LINE array has
   * size 2; STALL array has size 1;
   * 
   * @return hashmap with keys: "IR", "LINE", "LIGHT", and "STALL"
   */
  public HashMap<String, int[]> getAll() {
    HashMap<String, int[]> hm = new HashMap<String, int[]>();
    int[] v;

    if (getCommands != null) {
      v = getCommands.getAll();
      hm.put("IR", new int[] { v[0], v[1] });
      hm.put("LIGHT", new int[] { v[2], v[3], v[4] });
      hm.put("LINE", new int[] { v[5], v[6] });
      hm.put("STALL", new int[] { v[7] });
    }
    return hm;
  }
}
