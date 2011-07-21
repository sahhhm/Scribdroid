package com.scribdroid.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PictureActivity extends Activity {

    private final String TAG = "PictureActivity";
    private final Boolean D = true;
    
    private ImageView iv;
    private ProgressBar pb; 
    private TextView loadingMessage;
    
    private MyApp appState;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.picture);
        
        appState = (MyApp) getApplicationContext();
        
        iv = (ImageView) findViewById(R.id.imageView_picture);
        pb = (ProgressBar) findViewById(R.id.progressBar_picture);
        loadingMessage = (TextView) findViewById(R.id.textView_takingPicture);
    }
    
    public void onResume() {
        super.onResume();

        new TakePictureTask().execute();
    }
    
    /**
     * AsyncTask to take picture.
     */
    private class TakePictureTask extends AsyncTask<Void, Void, Bitmap> {

        protected Bitmap doInBackground(Void... params) {
            return appState.getScribbler().takePicture();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            // Show the ProgressBar and associated text
            pb.setVisibility(View.VISIBLE);
            loadingMessage.setVisibility(View.VISIBLE);  
        }        
        
        protected void onPostExecute(Bitmap bm) {
            if (bm != null) {
                Log.i(TAG, "Picture Success");
                
                // Hide ProgressBar and associated text
                pb.setVisibility(View.INVISIBLE);
                loadingMessage.setVisibility(View.INVISIBLE);
                
                // Show image and picture options
                iv.setVisibility(View.VISIBLE);
                iv.setImageBitmap(bm);

            } else {
                Log.e(TAG, "Error taking picture...");
            }
        }

    }    
}
