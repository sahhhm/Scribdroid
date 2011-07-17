package com.scribdroid.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

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

        x = w / 2;
        y = h / 2;
        r = w / 2;
    }

    @Override
    public abstract boolean onTouchEvent(MotionEvent me);

}
