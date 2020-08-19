package com.synway.passive.location.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Author：Libin on 2020/8/19 16:50
 * Email：1993911441@qq.com
 * Describe：
 */
public class LineView extends View {
    private Paint paint;
    private int[] colors = {0xFFFFFF, 0x0000FF, 0xFF0000};
    public LineView(Context context) {
        super(context);
    }

    public LineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        SweepGradient sweepGradient = new SweepGradient(50, 50, colors, null);
        paint.setShader(sweepGradient);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF rectF = new RectF();
        canvas.drawArc(rectF,180,180,false,paint);
    }
}
