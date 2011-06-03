package com.scribdroid.android;



import android.app.Activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;


public class ControllerActivity extends Activity {
    // Debugging
    private static final String TAG = "ControllerActivity";
    private static final boolean D = true;
	
    private MyApp appState;
    private int[] iconXY;
    private float translate, rotate;
    private float threshold;
    
    
    private MyEnclosure enclosure;
	private Resources res; 
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
	    setContentView(R.layout.controller);

	    translate = rotate = 0.0f;
	    threshold = 0.10f;
	    appState = ((MyApp)getApplicationContext());
	    res = getResources();
	    
	    RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.controller_layout);

    	//enclosure = new MyEnclosure(this, mLayout.getWidth()/2, mLayout.getHeight()/2, mLayout.getWidth()/2);
	    enclosure = new MyEnclosure(this);
	    mLayout.addView(enclosure);  

    }
  
    @Override
    protected void onStart() {
      super.onStart();  
    }    
    
    @Override
    protected void onResume() {
      super.onResume();
    }

    @Override
    protected void onStop() {
      super.onStop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
	    RelativeLayout rl = (RelativeLayout) findViewById(R.id.controller_layout);
	    
	    iconXY = new int[] {(rl.getWidth()) / 2 , (rl.getHeight()) / 2 }; 
        float[] values = new float[] {0,0};
        
        int action = me.getAction();
        float currentX = me.getX();
        float currentY = me.getY();
        
        
        //Make sure robot moves only if user touches in enclosure
        if (enclosure.inEnclosure(currentX, currentY)) {
        	
	        if (appState.getScribbler().isConnected() && action == MotionEvent.ACTION_DOWN) {        	
	        	if (D) Log.d(TAG, "ACTION_DOWN: X = " + currentX + "Y = " + currentY);
	        	
	        	enclosure.setTouchX(currentX);
	        	enclosure.setTouchy(currentY);
	        	enclosure.postInvalidate();
	        	
	            appState.getScribbler().setMoving(true);
	            values = calculateDirection(currentX, currentY);
	            move(values);
	        	
	        }
        }
        
        if (appState.getScribbler().isConnected() && action == MotionEvent.ACTION_UP) {
            if (D) Log.d(TAG, "ACTION_UP: X = " + currentX + " Y = " + currentY);
            
        	enclosure.setTouchX(rl.getWidth()/2);
        	enclosure.setTouchy(rl.getHeight()/2);
        	enclosure.postInvalidate();
            
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
		if (translate > 0) rotate *= -1;

		if (D) 
		    Log.d(TAG, "Moving... Trans = " + translate + " Rot = " + rotate);
		
		//appState.getScribbler().move(values[0], values[1]);
		appState.getScribbler().move(translate, rotate);
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
   
    
    private class MyEnclosure extends View {
		private float x; /* center x */
    	private float y; /* center y */
    	private float touchX; /* x-coordinate of where user touched */
    	private float touchY; /* y-coordinate of where user touched */
    	private float r;
        private final Paint mPaint = new Paint();

    	public MyEnclosure(Context context) {
	        this(context, 0, 0, 0, 0, 0);
	        mPaint.setColor(0xFFFF0000);
    	}        
        
    	public MyEnclosure(Context context, float x, float y, float r, float touchX, float touchY) {
			super(context);
	        this.x = x;
	        this.y = y;
	        this.r = r;
	        this.touchX = touchX;
	        this.touchY = touchY;
		}
    	
    	private void setTouchX (float tx) {
    		this.touchX = tx;
    	}
    	
    	private void setTouchy (float ty) {
    		this.touchY = ty;
    	}
    	
    	 @Override
    	 protected void onDraw(Canvas canvas) {
    	     super.onDraw(canvas);
             mPaint.setStyle(Style.STROKE);
    	     canvas.drawCircle(x, y, r, mPaint);
    	    
    	     if (touchX == 0 && touchY == 0) {
    	    	 canvas.drawLine(0, 0, 0, 0, mPaint);
    	     } else {
    	    	 canvas.drawLine(x, y, touchX, touchY, mPaint); 
    	     }
    	     
    	     mPaint.setStyle(Style.FILL_AND_STROKE);
    	     canvas.drawCircle(x, y, 5, mPaint);
    	 }
    	 
    	 /**
    	  * 
    	  * @param tx - the x-coordinate to test enclosure of
    	  * @param ty - the y-coordinate to test enclosure of
    	  * @return true if tx and ty are in the current enclosure
    	  */
    	 private boolean inEnclosure(float tx, float ty) {
    		 if ( Math.pow(tx - this.x, 2) + Math.pow(ty - this.y, 2) < Math.pow(this.r, 2) ) {
    			 return true;
    		 }
    		 return false; 
    	 }
    	 

	    @Override
	    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	        super.onSizeChanged(w, h, oldw, oldh);
	        
	        this.invalidate();
	        
	        this.x = w/2;
	        this.y = h/2;
	        this.r = w/2;
	    }
	    
    }
    
}
