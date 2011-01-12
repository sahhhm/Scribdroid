package com.scribdroid.android;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class ControllerActivity extends Activity {
    // Debugging
    private static final String TAG = "ControllerActivity";
    private static final boolean D = true;
	
    private MyApp appState;
    private int[] iconXY;
    private float translate, rotate;
    private float threshold;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    setContentView(R.layout.controller);

	    translate = rotate = 0.0f;
	    threshold = 0.10f;
	    appState = ((MyApp)getApplicationContext());
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
	    //ImageView iv = (ImageView) findViewById(R.id.walle);

	    RelativeLayout rl = (RelativeLayout) findViewById(R.id.controller_layout);
	    
	    iconXY = new int[] {(rl.getWidth()) / 2 , (rl.getHeight()) / 2 }; 
        float[] values = new float[] {0,0};
        
        int action = me.getAction();
        float currentX = me.getX();
        float currentY = me.getY();
        
        if (action == MotionEvent.ACTION_DOWN && !appState.getScribbler().isMoving()) { 
        	if (D) Log.d(TAG, "ACTION_DOWN: X = " + currentX + "Y = " + currentY);
            
            appState.getScribbler().setMoving(true);
            values = calculateDirection(currentX, currentY);
            move(values);
        }
        
        if (action == MotionEvent.ACTION_UP) {
            if (D) Log.d(TAG, "ACTION_UP: X = " + currentX + " Y = " + currentY);
            
            appState.getScribbler().setMoving(false);
            values = new float[] {0, 0};
            move(values);
        }
        return true;
      }
    
	private void move(float[] values) {
		translate = values[0];
		if (translate < 0.0f) {
			translate += threshold;
		} else if (translate > 0.0f) {
			translate -= threshold;
		}

		rotate = values[1];
		if (rotate < 0.0f) {
			rotate += threshold;
		} else if (rotate > 0.0f) {
			rotate -= threshold;
		}

		if (D)
			Log.d(TAG, "Moving... Trans = " + translate + " Rot = " + rotate);
		appState.getScribbler().move(values[0], values[1]);
	}
    
    private float[] calculateDirection(float xPos, float yPos) {
    	float trans = (iconXY[1] - yPos) / iconXY[1];
    	float rot = (iconXY[0] - xPos) / iconXY[0];
    	
    	if (Math.abs(trans) < threshold) {
    		trans = 0.0f;
    	}
    	if (Math.abs(rot) < threshold){
    		rot = 0.0f;
    	}
    	
    	if(D) Log.d(TAG, "Calculated: translate= " + trans + " rotate = " + rot);

    	return new float[] {trans, rot};
    }
}
