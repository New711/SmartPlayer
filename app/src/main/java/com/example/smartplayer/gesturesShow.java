package com.example.smartplayer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class gesturesShow extends View {
    String direction;
    int fingers;
    Paint paint;

    public gesturesShow(Context context,String direction,int fingers) {
        super(context);
        this.direction=direction;
        this.fingers=fingers;
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
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10);
        paint.setTextSize(40);
        canvas.scale(0.75f,0.75f,50,50);
        canvas.drawText("手势方向:"+direction+"  手指个数:"+fingers,80,240,paint);
        invalidate();
    }
}
