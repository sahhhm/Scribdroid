package com.scribdroid.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.widget.RelativeLayout;

abstract class Enclosure extends View {
    private float x; /* center x */
    private float y; /* center y */
    private float touchX; /* x-coordinate of where user touched */
    private float touchY; /* y-coordinate of where user touched */
    private float r;
    private final Paint mPaint = new Paint();

    public Enclosure(Context context) {
        this(context, 0, 0, 0, 0, 0);
        mPaint.setColor(0xFFFF0000);
    }

    public Enclosure(Context context, float x, float y, float r, float touchX,
            float touchY) {
        super(context);
        this.x = x;
        this.y = y;
        this.r = r;
        this.touchX = touchX;
        this.touchY = touchY;
    }

    public void setTouchX(float tx) {
        touchX = tx;
        ;
    }

    public void setTouchY(float ty) {
        touchY = ty;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getR() {
        return r;
    }

    public float getTouchX() {
        return touchX;
    }

    public float getTouchY() {
        return touchY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        this.invalidate();

        double controllerScale = .85;
        double bottomScale = 1 - controllerScale;

        // Update the controller layout to match current screen size
        ControllerActivity.controllerArea
                .setLayoutParams(new RelativeLayout.LayoutParams(w,
                        (int) (h * controllerScale)));
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w,
                (int) (h * bottomScale));
        lp.addRule(RelativeLayout.BELOW, R.id.controller_area);
        ControllerActivity.controllerBottomArea.setLayoutParams(lp);

        x = w / 2;
        y = ((int) (h * controllerScale)) / 2;
        r = ((int) (h * controllerScale)) / 2;
    }

    /**
     * Function that returns an OnTouchListener representing the actions that
     * should follow if a user touches the particular enclosure.
     * 
     * @return OnTouchListener for the enclosure
     */
    public abstract OnTouchListener getOnTouchListener();
}
