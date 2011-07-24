package com.scribdroid.android;

import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;

public class Preferences extends PreferenceActivity {

  private boolean D = true;
  private static final String TAG = "Preferences";
  private SharedPreferences settings;
  private Editor edit;
  private Resources res;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.layout.preferences);

    settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    edit = settings.edit();
    res = getResources();

    final CheckBoxPreference autoRefreshPref = (CheckBoxPreference) findPreference("autoRefreshPref");
    final EditTextPreference refreshPref = (EditTextPreference) findPreference("refreshPref");

    // initialize refresh rate to correct state
    if (autoRefreshPref.isChecked()) refreshPref.setEnabled(true);
    else refreshPref.setEnabled(false);
    
    // disable refresh rate if user switches to manual mode
    autoRefreshPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (refreshPref.isEnabled()) refreshPref.setEnabled(false);
        else refreshPref.setEnabled(true);
        return true;
      }
    });    
    
    // Set Refresh Rate text value to current value when user opens dialog
    refreshPref.setText(settings.getString(res.getString(R.string.refresh_rate_pref),
        res.getString(R.string.default_refresh_rate)));
    refreshPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        edit.putString(res.getString(R.string.refresh_rate_pref), newValue.toString());
        edit.commit();
        if(D) Log.i(TAG, "Set Refresh Rate to: " + newValue.toString());
        return true;
      }
    });
  }
}
