package com.scribdroid.scribbler;

import android.util.Log;

public class GetCommands {
	
	private static final String TAG = "GetCommands";
	private static final boolean D = true;
	
	private Scribbler s;
	
	private static final int PACKET_LENGTH = 9;
	
	private static final int GET_IR_ALL = 73;
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
	
	public byte[] getBattery() {
		byte[] battval;
		int retSize = 2;
		
		ReadWrite._writeFluke(s.getSocket(), s.isConnected(), new byte[] { (byte) GET_BATTERY });
		battval = ReadWrite._read(s.getSocket(), s.isConnected(), retSize);
		
		if (D) Log.i(TAG, "Battery Done");
		return battval;
		
	}
	
	public byte[] getIR() {
		byte[] ba = null; 
		ba = this._get(new byte[] { (byte) GET_IR_ALL }, 2, "byte");
		
		if (D) Log.i(TAG, "IR Done");
		return ba;
	}
	
	private byte[] _get(byte[] ba, int numBytes, String getType) {
		byte[] ret;
		byte[] temp;
		
		ReadWrite._write(s.getSocket(), s.isConnected(), ba);
		
		//Read Echo
		temp = ReadWrite._read(s.getSocket(), s.isConnected(), PACKET_LENGTH);

		Log.d(TAG, "ECHO READ: "+ temp.length + " -> " + ba2s(temp));

		
		ret = ReadWrite._read(s.getSocket(), s.isConnected(), numBytes);

		/*
		if (getType.toLowerCase().equals("byte")) {
			ret = temp;
		} else if (getType.toLowerCase().equals("word")) { 
			ret = new byte[numBytes/2];
			int c = 0;
			for (int i = 0; i < numBytes; i=i+2) {
				ret[c] = ((byte)( temp[i] << 8 | temp[i+1]));
				c++;
			}
		} else {
			ret = temp;
		}
		*/
		if (D) Log.d(TAG, "GET: " + ba2s(ret));
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
}
