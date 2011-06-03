package com.scribdroid.scribbler;

import android.util.Log;

public class GetCommands {
	
	private static final String TAG = "GetCommands";
	private static final boolean D = true;
	
	private Scribbler s;
	
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
		
		return battval;
		
	}
	

}
