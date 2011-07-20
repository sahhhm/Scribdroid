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
        iv.getLayoutParams().width = 256;
        iv.getLayoutParams().height = 192;
        Button b = (Button) findViewById(R.id.button1);
        b.setOnClickListener(new OnClickListener() {  
            public void onClick(View v) {  
                Log.d(TAG, "clicked");
                byte[] ba = appState.getScribbler().takePicture();
                Log.d(TAG, "ba length: " + ba.length);

                /*
                YuvImage yuv = new YuvImage(ba, ImageFormat.NV21, 256, 192, null);
               
                //Log.d(TAG, "Width: " + yuv.getWidth());
                //Log.d(TAG, "HEIGHT: " + yuv.getHeight());
                
                try {
                    File file = new File(Environment.getExternalStorageDirectory()
                        .getPath() + "/outYNV21.jpg");
                    FileOutputStream filecon;
               
                    filecon = new FileOutputStream(file);
                    yuv.compressToJpeg(
                            new Rect(0, 0, yuv.getWidth(), yuv.getHeight()), 100,
                            filecon);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    Log.e(TAG, e.getMessage());
                }
                */

                Bitmap bm = Bitmap.createBitmap(256, 192, Bitmap.Config.ARGB_8888);
                int w = 256;
                int h = 192;
                int vy, vu, y1v, y1u, uy, uv, y2u, y2v;
                int V=0, Y = 0, U = 0;
                
                for (int i = 0; i < h; i++) {
                    for (int j = 0; j < w; j++){
                        if (j >= 3) {
                            vy = -1;
                            vu = 2;
                            y1v = -1;
                            y1u = -3;
                            uy = -1;
                            uv = -2;
                            y2u = -1;
                            y2v = -3;
                        } else {
                            vy = 1; 
                            vu = 2; 
                            y1v = 3; 
                            y1u = 1; 
                            uy = 1; 
                            uv = 2; 
                            y2u = 3; 
                            y2v = 1;
                        }
                        if ((j % 4) == 0) {
                            V = ba[i * w + j] & 0xff;
                            Y = ba[i * w + j + vy] & 0xff;
                            U = ba[i * w + j + vu] & 0xff;  
                        } else if ((j % 4) == 1) {
                            Y = ba[i * w + j] & 0xff;
                            V = ba[i * w + j + y1v] & 0xff;
                            U = ba[i * w + j + y1u] & 0xff;
                        } else if ((j % 4) == 2) {
                            U = ba[i * w + j] & 0xff;
                            Y = ba[i * w + j + uy] & 0xff;
                            V = ba[i * w + j + uv] & 0xff;
                        } else if ((j % 4) == 3) {
                            Y = ba[i * w + j] & 0xff;
                            U = ba[i * w + j + y2u] & 0xff;
                            V = ba[i * w + j + y2v] & 0xff;
                        }
                        U = U - 128;
                        V = V - 128;
                        Y = Y;
                        
                        bm.setPixel(j, i, Color.rgb(
                                (int)Math.max(Math.min(Y + 1.13983 * V, 255), 0),
                                (int)Math.max(Math.min(Y - 0.39466 * U - 0.58060 * V, 255), 0),
                                (int)Math.max(Math.min(Y + 2.03211 * U, 255), 0)));
                        
                    }
                }
                
                Log.d(TAG, "FINISHED making bitmap");
                iv.setImageBitmap(bm);
                
                

            }  
        });  
    }   
    
}
