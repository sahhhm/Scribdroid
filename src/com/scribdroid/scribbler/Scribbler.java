package com.scribdroid.scribbler;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

import com.scribdroid.scribbler.SetCommands.LED;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class Scribbler {
    // Debugging
    private static final String TAG = "Scribbler";
    private static final boolean D = true;
	
	private String macAddress;
	private boolean connected;
	private byte[] lastSensors;
	private SetCommands setCommands;
	private GetCommands getCommands;
	private BluetoothSocket sock;
	private boolean isMoving;
	private int refreshRate; // Number of milliseconds to wait before new request
	
	public Scribbler() {
		this(null);
	}
	
	public Scribbler(String aMac){
		this.macAddress = aMac;
		this.setConnected(false);
		this.sock = null;
		this.setCommands = null;
		this.getCommands = null;
		this.refreshRate = 1000;
	}

	
	public boolean connect() throws Exception {
		boolean ret = false;
		BluetoothDevice scrib = BluetoothAdapter.getDefaultAdapter()
				.getRemoteDevice(this.macAddress);
		Method m = scrib.getClass().getMethod("createRfcommSocket",
				new Class[] { int.class });
		sock = (BluetoothSocket) m.invoke(scrib, Integer.valueOf(1));
		if (D)
			Log.d(TAG, "Connecting");
		try {
			this.sock.connect();
			this.setCommands = new SetCommands(this);
			this.getCommands = new GetCommands(this);
			this.setConnected(true);
			ret = true;
			if (D)
				Log.d(TAG, "Connected");
		} catch (Exception e) {
			this.setConnected(false);
			Log.e(TAG, "Error Connecting");
			try {
				sock.close();
			} catch (Exception e2) {
				Log.e(TAG, "Error closing socket after error connecting");
			}	
		}
		return ret;
	}
    
    public void disconnect() {
    	try {
	    	if (sock != null) {
	    		sock.close();
	    		this.setConnected(false);
	    		if (D) Log.d(TAG, "Socket Closed. Now Disconnected.");
	    	}
    	} catch (IOException e) {
    		Log.e(TAG, "Error closing socket while disconnecting");
    	}
    }
    
	/**
	 * @param connected the connected to set
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
	
	public void setSocket(BluetoothSocket aSock){
		this.sock = aSock;
	}
	
	public BluetoothSocket getSocket(){
		return this.sock;
	}

	/**
	 * @param lastSensors the lastSensors to set
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

    public void beep(float frequency, float duration){
    	if (setCommands != null) setCommands._setSpeaker((int)frequency, (int)(duration*1000));
    }
    
    public void beep(float frequency1, float frequency2, float duration){
    	if (setCommands != null) setCommands._setSpeaker2((int)frequency1,(int)frequency2, (int)(duration*1000));
    }
    
    public void setLED(LED position, boolean on){
    	if (setCommands != null) setCommands._setLED(position, on);
    }
    
    public void move(double translate, double rotate){
    	if (setCommands != null) setCommands._move(translate, rotate);
    }
    
    public void turnLeft(double amount){
    	if (setCommands != null) setCommands._move(0, -amount);
    }
    
    public void turnRight(double amount){
    	if (setCommands != null) setCommands._move(0, amount);
    }
    
    public void forward(double amount){
    	if (setCommands != null) setCommands._move(amount, 0);
    }
    
    public void backward(double amount){
    	if (setCommands != null) setCommands._move(-amount, 0);
    }

	public void setMoving(boolean isMoving) {
		this.isMoving = isMoving;
	}

	public boolean isMoving() {
		return isMoving;
	}
	
	public int getRefreshRate() {
		return refreshRate;
	}
	
	public void setRefreshRate(int r) {
		this.refreshRate  = r;
	}
	
	public byte[] takePicture(){
		byte[] ba = null;
 		if (getCommands != null) 
			ba =  getCommands.getArray();
 		return ba;
	}

	public float getBattery() {
		byte[] ba = null;
		int unmodified;
		float value = 0;
		if (getCommands != null) {
			ba = getCommands.getBattery();
		
			unmodified = ((ba[0] & 0xFF) << 8 | (ba[1] & 0xFF));
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
	 * Each associated array for a key is of variable length, depending on the type of sensor.
	 * IR array has size 2; LIGHT array has size 3; LINE array has size 2; STALL array
	 * has size 1;
	 * @return hashmap with keys: "IR", "LINE", "LIGHT", and "STALL"
	 */
	public HashMap<String, int[]> getAll() {
		HashMap<String, int[]> hm = new HashMap<String, int[]>();
		int[] v;
		
		if (getCommands != null) {
			v = getCommands.getAll();
			hm.put("IR", new int[] {v[0], v[1]});
			hm.put("LIGHT", new int[] {v[2], v[3], v[4]});
			hm.put("LINE", new int[] {v[5], v[6]});
			hm.put("STALL", new int[] {v[7]});
		}		
		return hm;
	}
}
