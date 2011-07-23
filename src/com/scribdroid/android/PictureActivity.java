package com.scribdroid.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PictureActivity extends Activity {

    private final String TAG = "PictureActivity";
    private final Boolean D = true;
    
    private ImageView iv;
    
    // taking picture resources
    private ProgressBar pb; 
    private TextView loadingMessage;
    
    // save/cancel resources
    private Button buttonSave;
    private Button buttonCancel;
    
    // Edit name resources
    private TextView textViewPName;
    private EditText editTextName;
    
    
    private MyApp appState;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.picture);
        
        appState = (MyApp) getApplicationContext();
        
        iv = (ImageView) findViewById(R.id.imageView_picture);
        pb = (ProgressBar) findViewById(R.id.progressBar_picture);
        loadingMessage = (TextView) findViewById(R.id.textView_takingPicture);
        buttonSave = (Button) findViewById(R.id.button_savePicture);
        buttonCancel = (Button) findViewById(R.id.button_cancelPicture);
        textViewPName = (TextView) findViewById(R.id.textView_pictureName);
        editTextName = (EditText) findViewById(R.id.editText_pictureName);
        
        // implement cancel onClick
        buttonCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            } 
        });
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

                // set the image in the imageview
                iv.setImageBitmap(bm);
                
                // Show image and picture options
                iv.setVisibility(View.VISIBLE);
                buttonSave.setVisibility(View.VISIBLE);
                buttonCancel.setVisibility(View.VISIBLE);
                textViewPName.setVisibility(View.VISIBLE);
                editTextName.setVisibility(View.VISIBLE);

            } else {
                Log.e(TAG, "Error taking picture...");
            }
        }

    }    
}
