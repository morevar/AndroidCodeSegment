package com.example.android.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceView;

import com.example.android.R;

public class ImageSurface extends SurfaceView {

    Bitmap icon;

    public ImageSurface(Context context) {
        super(context, null);
    }

    public ImageSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void drawImage(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(icon, 100, 100, new Paint());
    }
}
