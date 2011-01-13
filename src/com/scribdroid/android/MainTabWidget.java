package com.scribdroid.android;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.scribdroid.scribbler.Scribbler;

public class MainTabWidget extends TabActivity {
    // Debugging
    private static final String TAG = "MainTabWidget";
    private static final boolean D = true;
    
	private Resources res;
    private MyApp appState;
    private TextView connectivity;
	
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;   
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

	    setContentView(R.layout.main);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.custom_title);
        connectivity = (TextView) findViewById(R.id.connectivity);
	    
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab
	    
	    res = getResources(); // Resource object to get Drawables
	    appState = ((MyApp)getApplicationContext());
        connectivity.setText(res.getString(R.string.not_connected));

	    intent = new Intent().setClass(this, ControllerActivity.class);
	    spec = tabHost.newTabSpec("controller").setIndicator("Controller",
                res.getDrawable(R.drawable.ic_tab_controller))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    tabHost.setCurrentTab(0);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.connection_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.connect:
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            return true;
	    case R.id.disconnect:
	    	final Scribbler s = appState.getScribbler();
	    	if (s != null && s.isConnected()) {
		    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    	builder.setMessage("Are you sure you want to disconnect?")
		    	       .setCancelable(false)
		    	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	                s.disconnect();
		    	    	    	Toast.makeText(getApplicationContext(), res.getString(R.string.success_disconnect),
		    	    		  	          Toast.LENGTH_SHORT).show(); 
		    	    	        connectivity.setText(res.getString(R.string.not_connected));

		    	           }
		    	       })
		    	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		    	           public void onClick(DialogInterface dialog, int id) {
		    	                dialog.cancel();	
		    	           }
		    	       });
		    	AlertDialog alert = builder.create();
		    	alert.show();
	    	} else {
    	    	Toast.makeText(getApplicationContext(), res.getString(R.string.are_not_connected),
  		  	          Toast.LENGTH_SHORT).show();    
	    	}
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the selected device's MAC address
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
    	    	if (D) Log.d(TAG, "User Requested" + address); 
                
    	    	//Create New Scribbler
    	    	Scribbler newScribbler = new Scribbler(address);
    	    	
    	    	//Connect New Scribbler
    	    	try {
					 if (newScribbler.connect()) {
		    	    	//Persist New Scribbler
		    	        appState.setScribbler(newScribbler);
		    	        connectivity.setText(res.getString(R.string.connected));

		    	        if (D) Log.d(TAG, "Scribbler Persisted");
					 } else {
			    	    Toast.makeText(getApplicationContext(), "Error Connecting to " + address,
			    		 	          Toast.LENGTH_SHORT).show();
			            connectivity.setText(res.getString(R.string.not_connected));

			    	 }
				} catch (Exception e) {
					Log.e(TAG, "Connection Failed");
			        connectivity.setText(res.getString(R.string.not_connected));
				}
            }
            break;
        }
    }
	

}