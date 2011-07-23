package com.scribdroid.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ProgressBar;
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
    public static TextView connectivity;
    public static ProgressBar titleProgressBar;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.main);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.custom_title);
        connectivity = (TextView) findViewById(R.id.rightText);
        titleProgressBar = (ProgressBar) findViewById(R.id.title_progress_bar);

        // The activity TabHost
        TabHost tabHost = getTabHost();

        // Reusable TabSpec for each tab
        TabHost.TabSpec spec;

        // Reusable Intent
        Intent intent;

        res = getResources();

        // Set dummy scribbler. will be replaced once user connects
        appState = (MyApp) getApplicationContext();
        appState.setScribbler(new Scribbler());
        connectivity.setText(res.getString(R.string.not_connected));

        // Add Controller Activity to TabHost
        intent = new Intent().setClass(this, ControllerActivity.class);
        spec = tabHost
                .newTabSpec("controller")
                .setIndicator("Controller",
                        res.getDrawable(R.drawable.ic_tab_controller))
                .setContent(intent);
        tabHost.addTab(spec);

        // Add Robot Info Activity Group to TabHost
        intent = new Intent().setClass(this, RobotInfoGroup.class);
        spec = tabHost
                .newTabSpec("info")
                .setIndicator("Info",
                        res.getDrawable(R.drawable.ic_tab_controller))
                .setContent(intent);
        tabHost.addTab(spec);
        
        // Add Robot Info Activity Group to TabHost
        intent = new Intent().setClass(this, PictureGalleryActivity.class);
        spec = tabHost
                .newTabSpec("Picture")
                .setIndicator("Picture",
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
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        s.disconnect();
                                        Toast.makeText(
                                                getApplicationContext(),
                                                res.getString(R.string.success_disconnect),
                                                Toast.LENGTH_SHORT).show();
                                        connectivity.setText(res
                                                .getString(R.string.not_connected));

                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                Toast.makeText(getApplicationContext(),
                        res.getString(R.string.are_not_connected),
                        Toast.LENGTH_SHORT).show();
            }
            return true;
        case R.id.preferences:
            Intent prefsIntent = new Intent(this, Preferences.class);
            startActivity(prefsIntent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the selected device's MAC address
                String address = data.getExtras().getString(
                        DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                if (D) Log.d(TAG, "User Requested" + address);

                // Create New Scribbler
                Scribbler newScribbler = new Scribbler(address);

                // Connect New Scribbler
                try {
                    if (newScribbler.connect()) {
                        // Persist New Scribbler
                        appState.setScribbler(newScribbler);
                        connectivity
                                .setText(res.getString(R.string.connected));

                        Toast.makeText(getApplicationContext(),
                                "Successful Connecting to " + address,
                                Toast.LENGTH_SHORT).show();

                        // Successful Connection beeps
                        Scribbler s = appState.getScribbler();
                        s.beep(784, .03f);
                        s.beep(880, .03f);
                        s.beep(698, .03f);
                        s.beep(349, .03f);
                        s.beep(523, .03f);

                        Log.i(TAG, "Scribbler Persisted");

                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Error Connecting to " + address,
                                Toast.LENGTH_SHORT).show();
                        connectivity.setText(res
                                .getString(R.string.not_connected));

                    }
                } catch (Exception e) {
                    Log.e(TAG, "Connection Failed");
                    connectivity
                            .setText(res.getString(R.string.not_connected));
                }
            }
            break;
        }
    }
    
    public static void emphasizeConnectivity() {
        ScaleAnimation s = new ScaleAnimation(0.75f, 1, 0.75f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        s.setDuration(500);
        connectivity.startAnimation(s);
    }
    
}