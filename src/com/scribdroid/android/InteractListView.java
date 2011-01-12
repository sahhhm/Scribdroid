package com.scribdroid.android;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.scribdroid.scribbler.SetCommands.LED;

public class InteractListView extends ListActivity {


	private MyApp appState;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);

	  appState = ((MyApp)getApplicationContext());
	  
	  setListAdapter(new ArrayAdapter<String>(this, R.layout.interact_list_item, COUNTRIES));

	  ListView lv = getListView();
	  lv.setTextFilterEnabled(true);

	  lv.setOnItemClickListener(new OnItemClickListener() {
	    public void onItemClick(AdapterView<?> parent, View view,
	        int position, long id) {
	    	appState.getScribbler().backward(1);
	    }
	  });
	}
	static final String[] COUNTRIES = new String[] {
	    "Test"
	  };
}
