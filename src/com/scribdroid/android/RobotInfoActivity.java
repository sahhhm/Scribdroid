package com.scribdroid.android;

import android.app.Activity;
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
	          Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
	              Toast.LENGTH_SHORT).show();
	        }
	      });
	}
	
}
