package com.example.smartplayer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GestureView extends View {

    float floats[];
    private Paint paint;

    public GestureView(@NonNull Context context) {
        super(context);
    }

    public float[] getFloats() {
        return floats;
    }

    public void setFloats(float[] floats) {
        this.floats = floats;
    }

    public GestureView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width=640;
        int height=480;
        setMeasuredDimension(width,height);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint=new Paint();
        paint.reset();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(10);
        paint.setTextSize(40);
        canvas.scale(0.75f,0.75f,50,50);
        if(floats!=null&&floats.length>0){
            canvas.drawPoints(floats,paint);
        }else{
            canvas.drawText(getContext().getString(R.string.GesturePrompt),80,240,paint);
        }
        invalidate();
    }
}
