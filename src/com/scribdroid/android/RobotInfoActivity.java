package com.scribdroid.android;

import java.util.HashMap;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class RobotInfoActivity extends Activity {
    // Debugging
    private static final String TAG = "RobotInfoActivity";
    private static final boolean D = true;
    
    private MyApp appState;

	private TextView name, battery;
	private TextView obstacleLeft, obstacleCenter, obstacleRight;
	private TextView lightLeft, lightCenter, lightRight;
	private TextView irLeft, irRight;
	private TextView lineLeft, lineRight;
	private float batteryValue;
	private int[] obstacleValues, lightValues, irValues, lineValues;
    private String nameValue;
    
    private Handler handler;
    
    private ToggleButton button;
    private Runnable r;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    setContentView(R.layout.infotable);

	    appState = ((MyApp)getApplicationContext());	    
	    handler = new Handler();
	    
	    // Initialize the textviews that will be updated when requested
		name = (TextView) findViewById(R.id.name_value);
		battery = (TextView) findViewById(R.id.battery_value);
		obstacleLeft = (TextView) findViewById(R.id.obstacle_left_value);
		obstacleCenter = (TextView) findViewById(R.id.obstacle_center_value);
		obstacleRight = (TextView) findViewById(R.id.obstacle_right_value);
		lightLeft = (TextView) findViewById(R.id.light_left_value);
		lightCenter = (TextView) findViewById(R.id.light_center_value);
		lightRight = (TextView) findViewById(R.id.light_right_value);
		irRight = (TextView) findViewById(R.id.ir_right_value);
		irLeft = (TextView) findViewById(R.id.ir_left_value);
		lineLeft = (TextView) findViewById(R.id.line_left_value);
		lineRight = (TextView) findViewById(R.id.line_right_value);
        button = (ToggleButton) findViewById(R.id.toggleTimer);


        r=new Runnable()
        {
            public void run() 
            {
            	// Update only of scribbler is connected
            	if (appState.getScribbler().isConnected()) {
            		Log.i(TAG, "Populating Values");
            	    MainTabWidget.titleProgressBar.setVisibility(View.VISIBLE);
	            	updateValues();
	            	populate();
	        	    MainTabWidget.titleProgressBar.setVisibility(View.INVISIBLE);
            	} else {
            		Log.e(TAG, "Disconnected unexpectedly...");
            		button.setChecked(false);
            	}
            	// Keep polling until user decides to stop or leaves activity
            	if (button.isChecked()) {
            		handler.postDelayed(this, appState.getScribbler().getRefreshRate());
            	}
            }
        };	        

        button.setChecked(true);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              if (button.isChecked())
          		  handler.postDelayed(r, appState.getScribbler().getRefreshRate());
            }
          });
	        
		handler.postDelayed(r, appState.getScribbler().getRefreshRate());
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (button.isChecked()) {
			handler.postDelayed(r, appState.getScribbler().getRefreshRate());
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		button.setChecked(false);
	    MainTabWidget.titleProgressBar.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		button.setChecked(false);
	    MainTabWidget.titleProgressBar.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * Function that properly updates variables containing
	 * information for the sensors.
	 */
	private void updateValues() {
     	HashMap<String, int[]> hm = appState.getScribbler().getAll();
     	lineValues = hm.get("LINE");	         
		irValues = hm.get("IR");
		lightValues = hm.get("LIGHT");
		nameValue = appState.getScribbler().getName();
		batteryValue = appState.getScribbler().getBattery();
		obstacleValues = appState.getScribbler().getObstacle("all");		
	}
	
	/**
	 * Function that properly populates the UI with the
	 * current sensors values.
	 */
	private void populate() {
		// Update Name
		name.setText(nameValue);
	
		// Update Battery
		battery.setText(Float.toString(batteryValue));
		if (batteryValue < 6.2) {
			battery.setTextColor(Color.RED);
		} else if (batteryValue < 7) {
			battery.setTextColor(Color.YELLOW);
		} else {
			battery.setTextColor(Color.GREEN);
		}
		
		// Update Obstacle Values
		obstacleLeft.setText(Integer.toString(obstacleValues[0]));
		obstacleCenter.setText(Integer.toString(obstacleValues[1]));
		obstacleRight.setText(Integer.toString(obstacleValues[2]));
		
		// Update Light Values
		lightLeft.setText(Integer.toString(lightValues[0]));
		lightCenter.setText(Integer.toString(lightValues[1]));
		lightRight.setText(Integer.toString(lightValues[2]));
		
		// Update IR Values
		irLeft.setText(Integer.toString(irValues[0]));
		irRight.setText(Integer.toString(irValues[1]));
		
		// Update Line Values
		lineLeft.setText(Integer.toString(lineValues[0]));
		lineRight.setText(Integer.toString(lineValues[1]));			
	}
}
