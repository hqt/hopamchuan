package com.example.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AndroidSurface extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;
    public String chord;

    public AndroidSurface(Context context, String chord) {
        super(context);
        this.chord = chord;

        holder = this.getHolder();
        holder.addCallback(this);
    }

    public AndroidSurface(Context context, AttributeSet attrs) {

        super(context, attrs);

        this.chord = "square";
        holder = this.getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        onDrawing(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        onDrawing(holder);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void onDrawing(SurfaceHolder holder) {
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) {
            Log.i("Debug", "canvas is null");
        }
        else {
            onDrawing(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void onDrawing(Canvas canvas) {
        if (chord.equals("circle")) {
            Paint paint = new Paint();
            paint.setColor(Color.BLUE);
            paint.setAntiAlias(true);
            canvas.drawCircle(50, 50, 80, paint);
        }

        if (chord.equals("square")) {
            Paint paint = new Paint();
            paint.setColor(Color.BLUE);
            paint.setAntiAlias(true);
            canvas.drawRect(50, 50, 100, 100, paint);
        }
    }

    private void reDraw(String chord) {
        this.chord = chord;
        onDrawing(holder);
    }

    public void reDraw() {
        onDrawing(holder);
    }

}
