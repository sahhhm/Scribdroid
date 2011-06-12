package com.scribdroid.scribbler;

import android.util.Log;

public class GetCommands {
	
	private static final String TAG = "GetCommands";
	private static final boolean D = true;
	
	private Scribbler s;
	
	private static final int PACKET_LENGTH = 9;
	
	private static final int GET_LIGHT_ALL = 70;
	private static final int GET_IR_ALL = 73;
    private static final int GET_NAME1 = 78;
    private static final int GET_NAME2 = 64;
    private static final int GET_IMAGE = 83;
    private static final int GET_BATTERY = 89;
	
	public GetCommands(Scribbler aScrib){
		this.s = aScrib;
	}
	
	public byte[] getArray(){
		byte[] line;
		
		int width = 256;
		int height = 192;
		int size = width * height;
		
		ReadWrite._write(s.getSocket(), s.isConnected(), new byte[] { (byte) GET_IMAGE });
		
		line = ReadWrite._read(s.getSocket(), s.isConnected(), size);
		if (D) Log.d(TAG,"Finished Reading--getArray");
		
		return line;
	}
	
	/**
	 * 
	 * @return - byte[] consisting of the 16 bytes that make up the robot name
	 */
	public int[] getName() {
		int[] ba, ba1, ba2;
		int retSize = 8;

		// Get both halves of the name
		ba1 = _get(new byte[] { (byte) GET_NAME1 }, retSize, "byte");
		ba2 = _get(new byte[] { (byte) GET_NAME2 }, retSize, "byte");

		// Combine both halves of the name
		ba = new int[ba1.length + ba2.length];
		System.arraycopy(ba1, 0, ba, 0, ba1.length);
		System.arraycopy(ba2, 0, ba, ba1.length, ba2.length);
		return ba;
	}
	
	public byte[] getBattery() {
		byte[] battval;
		int retSize = 2;
		
		ReadWrite._writeFluke(s.getSocket(), s.isConnected(), new byte[] { (byte) GET_BATTERY });
		battval = ReadWrite._read(s.getSocket(), s.isConnected(), retSize);
		
		if (D) Log.i(TAG, "Battery Done");
		return battval;
		
	}
	
	public int[] getIR() {
		int[] ba = null; 
		int numBytes = 2;
		
		ba = this._get(new byte[] { (byte) GET_IR_ALL }, numBytes, "byte");
		
		if (D) Log.i(TAG, "IR Done");
		return ba;
	}
	
	public int[] getLight() {
		int[] ba = null;
		int numBytes = 6;
		
		ba = this._get(new byte[] { (byte) GET_LIGHT_ALL }, numBytes, "word");
		
		if (D) Log.i(TAG, "LIGHT Done");
		return ba;
	}
	
	/**
	 * Function that gets contents of a request to the robot
	 * @param ba Byte array containing the message to be sent to the robot
	 * @param numBytes the number of bytes in the robots message
	 * @param getType the way the read bytes need to be formatted (byte or word)
	 * @return an int[] containing the requested message from the robot
	 */
	private int[] _get(byte[] ba, int numBytes, String getType) {
		int[] ret;
		byte[] temp;
		
		// Write Message
		ReadWrite._write(s.getSocket(), s.isConnected(), ba);
		
		// Read the Message Echo
		temp = ReadWrite._read(s.getSocket(), s.isConnected(), PACKET_LENGTH);
		Log.d(TAG, "ECHO READ: "+ temp.length + " -> " + ba2s(temp));

		// Read contents of what's desired
		temp = ReadWrite._read(s.getSocket(), s.isConnected(), numBytes);

		// Convert the received bytes if needed
		if (getType.toLowerCase().equals("byte")) {
			ret = new int[temp.length];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = temp[i];
			}
		} else if (getType.toLowerCase().equals("word")) { 
			int c = 0;
			ret = new int[numBytes/2];
			
			if (D) Log.d(TAG, "GET[before word modify]: " + ba2s(temp));
			
			for (int i = 0; i < numBytes; i=i+2) {
				ret[c] = (((temp[i] & 0xFF) << 8) | (temp[i+1] & 0xFF));
				c++;
			}
		} else {
			Log.e(TAG, "Cannot _get type: " + getType);
			ret = new int[] {};
		}
		
		if (D) Log.d(TAG, "GET: " + int2str(ret));
		return ret;
	}
	
   private static String ba2s(byte[] ba){
    	StringBuilder sb = new StringBuilder("[ ");
    	for (byte b : ba) {
    		sb.append(Integer.toHexString(b & 0xff)).append(" ");
    	}
    	sb.append("]");
    	return sb.toString();
    } 
    
    private static String int2str(int[] ba){
    	StringBuilder sb = new StringBuilder("[ ");
    	for (int b : ba) {
    		sb.append(Integer.toHexString(b)).append(" ");
    	}
    	sb.append("]");
    	return sb.toString();
    }	
}
