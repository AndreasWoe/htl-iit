package at.htlwels.btlescanner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GaugeView extends View {
    private Paint paint, paintBezel, paintDial, paintNeedle, paintCenter;
    private Paint textPaint;
    private int value = 50; // Default value

    public GaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);

        paintBezel = new Paint();
        paintBezel.setColor(Color.GREEN);
        paintBezel.setStrokeWidth(5);
        paintBezel.setStyle(Paint.Style.STROKE);

        paintNeedle = new Paint();
        paintNeedle.setColor(Color.BLACK);
        paintNeedle.setStrokeWidth(10);
        paintNeedle.setStyle(Paint.Style.STROKE);
        paintNeedle.setStrokeCap(Paint.Cap.ROUND);

        paintDial = new Paint();
        paintDial.setColor(Color.LTGRAY);
        paintDial.setStrokeWidth(5);
        paintDial.setStyle(Paint.Style.FILL);

        paintCenter = new Paint();
        paintCenter.setColor(Color.DKGRAY);
        paintCenter.setStrokeWidth(1);
        paintCenter.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(25);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2 - 20;

        // Draw the dial
        canvas.drawCircle(width / 2, height / 2, radius, paintDial);
        // Draw the bezel
        canvas.drawCircle(width / 2, height / 2, radius, paintBezel);

        // Draw the needle
        double angle = Math.toRadians((value / 100.0) * 360 - 90);
        float needleX = (float) (width / 2 + radius * Math.cos(angle));
        float needleY = (float) (height / 2 + radius * Math.sin(angle));
        canvas.drawLine(width / 2, height / 2, needleX, needleY, paintNeedle);

        // Draw the center
        canvas.drawCircle(width / 2, height / 2, radius / 8, paintCenter);

        // Draw the scale
        for (int i = 0; i <= 9; i++) {
            double scaleAngle = Math.toRadians((i / 10.0) * 360 - 90);
            float startX = (float) (width / 2 + (radius - 20) * Math.cos(scaleAngle));
            float startY = (float) (height / 2 + (radius - 20) * Math.sin(scaleAngle));
            float endX = (float) (width / 2 + radius * Math.cos(scaleAngle));
            float endY = (float) (height / 2 + radius * Math.sin(scaleAngle));
            canvas.drawLine(startX, startY, endX, endY, paint);

            // Draw the scale labels
            float labelX = (float) (width / 2 + (radius - 40) * Math.cos(scaleAngle));
            float labelY = (float) (height / 2 + (radius - 40) * Math.sin(scaleAngle)) + 10;
            canvas.drawText(String.valueOf(i * 10), labelX, labelY, textPaint);
        }
    }

    public void setValue(int value) {
        this.value = value;
        invalidate(); // Redraw the view
    }
}