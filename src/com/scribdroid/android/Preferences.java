package com.scribdroid.android;

import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.os.Bundle;

public class Preferences extends PreferenceActivity {
	
	private boolean D = true;
	private static final String TAG = "Preferences";
    private MyApp appState; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.preferences);    

	    appState = ((MyApp)getApplicationContext());	    
        
        // Set Refresh Rate text value to current value when user opens 
	    final EditTextPreference refreshPref = (EditTextPreference) findPreference("refreshPref");
        refreshPref.setText(Integer.toString(appState.getScribbler().getRefreshRate()));
        refreshPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				appState.getScribbler().setRefreshRate(Integer.parseInt(refreshPref.getText()));
				Log.i(TAG, "Set Refresh Rate to: " + refreshPref.getText());
				return true;
			}
		});
	}
}	


