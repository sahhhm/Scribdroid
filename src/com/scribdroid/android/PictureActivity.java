package com.scribdroid.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class PictureActivity extends Activity {

    private ImageView iv;
    private MyApp appState;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.picture);
        
        appState = (MyApp) getApplicationContext();
        iv = (ImageView) findViewById(R.id.imageView_picture);
    }
    
    public void onResume() {
        super.onResume();
        
        Bitmap bm = appState.getScribbler().takePicture();
        iv.setImageBitmap(bm);
        
    }
}
