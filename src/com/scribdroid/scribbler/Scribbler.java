package com.scribdroid.scribbler;

import java.io.IOException;
import java.lang.reflect.Method;

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
	private BluetoothSocket sock;
	
	public Scribbler(String aMac){
		this.macAddress = aMac;
		this.setConnected(false);
		this.sock = null;
	}
	
	public void connect() throws Exception {
        BluetoothDevice scrib = BluetoothAdapter.getDefaultAdapter().
            getRemoteDevice(this.macAddress);
        Method m = scrib.getClass().getMethod("createRfcommSocket",
            new Class[] { int.class });
        sock = (BluetoothSocket)m.invoke(scrib, Integer.valueOf(1));
        if (D) Log.d(TAG, "Connecting");
        try {
        	sock.connect();
        } catch (Exception e){
        	Log.e(TAG, "Error Connecting");
        	try {
        		sock.close();
        	} catch (Exception e2){
        		Log.e(TAG,"Error closing socket after error connecting");
        	}
        }
        if (D) Log.d(TAG, "Connected");
        this.setConnected(true);
    }
    
    public void disconnect() {
    	try {
	    	if (sock != null) {
	    		sock.close();
	    		this.setConnected(false);
	    		if (D) Log.d(TAG, "Socket Close");
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
}
