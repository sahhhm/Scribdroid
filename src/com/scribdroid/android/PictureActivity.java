package com.scribdroid.android;

import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
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
import android.widget.Toast;

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

    private Bitmap image;

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

        // image is null until taken
        image = null;

        // implement cancel onClick
        buttonCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // implement save onClick
        buttonSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (image != null) {
                    // save image to internal storage
                    try {
                        String path = editTextName.getText().toString()
                                + ".jpg";
                        FileOutputStream fos = openFileOutput(path,
                                Context.MODE_PRIVATE);
                        image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                        fos.close();

                        // Let the user know picture saved successfully and
                        // finish
                        Toast.makeText(
                                getBaseContext(),
                                getResources().getString(
                                        R.string.successful_save),
                                Toast.LENGTH_LONG).show();
                        Log.i(TAG, "Saved Picture");
                        finish();

                    } catch (Exception e) {
                        Toast.makeText(
                                getBaseContext(),
                                getResources().getString(
                                        R.string.unsuccessful_save),
                                Toast.LENGTH_LONG).show();
                        Log.e(TAG, e.getMessage());
                    }
                } else {
                    Toast.makeText(
                            getBaseContext(),
                            getResources().getString(
                                    R.string.unsuccessful_save),
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, "There does not seem to be an image to save");
                }
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

                // assign the newly created bitmap so we can save
                image = bm;

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
