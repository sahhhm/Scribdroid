
package com.scribdroid.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
//import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
//import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Highly experimental class testing random things
 * related to pictures as needed
 */
public class PictureTestActivity extends Activity {

    // Debugging
    private static final String TAG = "PictureTestActivity";
    private static final boolean D = true;

    private MyApp appState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.picturetest);
        
        appState = (MyApp) getApplicationContext();

        final ImageView iv = (ImageView) findViewById(R.id.imageView1);
 
        File dir = getFilesDir();
        Log.i(TAG, "Directory: " + dir.toString());
        String[] files = fileList();
        for (String s : files) {
            Log.d(TAG, "File: " + dir.toString() + "/" + s);
        }
        
        iv.setImageBitmap(BitmapFactory.decodeFile(dir.toString() + "/" + files[0].toString()));
    }   
    
}
