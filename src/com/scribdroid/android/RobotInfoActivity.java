package com.scribdroid.android;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RobotInfoActivity extends ListActivity {

    // Debugging
    private static final String TAG = "RobotInfoActivity";
    private static final boolean D = true;
    
    private MyApp appState;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
	    appState = ((MyApp)getApplicationContext());
	    
	    String[] informations = getResources().getStringArray(R.array.information_array);
	    setListAdapter(new ArrayAdapter<String>(this, R.layout.info_list_item, informations));
	    
	    ListView lv = getListView();
	    lv.setTextFilterEnabled(true);
	    
	    
	    lv.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	          // When clicked, show a toast with the TextView text
	          CharSequence text = ((TextView) view).getText();
	          if (D) Log.d(TAG, "Clicked On: " + text);
	          
	          if (appState.getScribbler().isConnected()) {
	        	  if (text.equals("IR_ALL")) {
		        	  int[] ir = appState.getScribbler().getIR("all");
		        	  Toast.makeText(getApplicationContext(), "IR LEFT: " + ir[0] + "\n" + "IR RIGHT: " + ir[1], Toast.LENGTH_SHORT).show();
		          } else if (text.equals("BATTERY")){ 
		        	  Toast.makeText(getApplicationContext(), "BATTERY: " + appState.getScribbler().getBattery(), Toast.LENGTH_SHORT).show();
		          } else if (text.equals("ROBOT_NAME")) {
		        	  Toast.makeText(getApplicationContext(), "Name: " + appState.getScribbler().getName(), Toast.LENGTH_SHORT).show();		        	  
		          } else if (text.equals("LIGHT_ALL")) { 
		        	  int[] light = appState.getScribbler().getLight("all");
		        	  Toast.makeText(getApplicationContext(), "LIGHT LEFT: " + light[0] + "\n" + "LIGHT CENTER: " + light[1] + "\n" + "LIGHT RIGHT: " + light[2], Toast.LENGTH_SHORT).show();		        	  
		          } else if (text.equals("OBSTACLE_ALL")) { 
		        	  int[] obs = appState.getScribbler().getObstacle("all");
		        	  Toast.makeText(getApplicationContext(), "OBSTACLE LEFT: " + obs[0] + "\n" + "OBSTACLE CENTER: " + obs[1] + "\n" + "OBSTACLE RIGHT: " + obs[2], Toast.LENGTH_SHORT).show();		        	  
		          }
		          else {
		        	  Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();	        	  
		          }
	          }
	        }
	      });
	}
	
}
