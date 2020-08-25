package com.synway.passive.location.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.synway.passive.location.R;

/**
 * Author：Libin on 2020/8/19 16:50
 * Email：1993911441@qq.com
 * Describe：能量信息表盘
 */
public class CircleProgressView extends View {
    private Paint bottomRingPaint;  //底部渐变圆环
    private Paint topRingPaint;     //上部渐变圆环
    private Paint bottomMaskPaint; //底部黑色圆环,覆盖渐变圆环
    private Paint topMaskPaint;    //底部黑色圆环,覆盖渐变圆环
    private Paint bgBlackPaint;   //外层黑色背景圆环
    private Paint bgCirclePaint;   //内层白色圆
    private Paint bgRedPaint;      //中间红色圆环
    private Paint bgGrayPaint;     //内层灰色圆环
    private Paint bottomArrowPaint; //底部箭头
    private Paint topArrowPaint;    //上部箭头
    private Paint linePaint;    //刻度线
    private int[] colors = {Color.WHITE, Color.BLUE, Color.RED};
    private RectF gradientRectF;  //渐变圆环区域
    private RectF bgBlackRectF;  //外部圆环区域
    private RectF bgRedRectF;        //中间红色圆环区域
    private RectF bgGrayRectF;            //内层灰色圆环区域
    private float radius;   //半径
    private ValueAnimator mAnimator;  //动画
    private int progress = 150;      //动画进度，0-150
    private Context context;
    private String value = "0";  //值
    private Paint valuePaint;  //能量值paint
    private Paint tipPaint;    //能量信息paint

    public CircleProgressView(Context context) {
        super(context);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        bottomRingPaint = new Paint();
        bottomRingPaint.setAntiAlias(true);
        bottomRingPaint.setStyle(Paint.Style.STROKE);
        bottomRingPaint.setStrokeWidth(50);


        topRingPaint = new Paint();
        topRingPaint.setAntiAlias(true);
        topRingPaint.setStyle(Paint.Style.STROKE);
        topRingPaint.setStrokeWidth(50);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(ContextCompat.getColor(context, R.color.gray_1d1));
        linePaint.setStrokeWidth(3);

        bottomMaskPaint = new Paint();
        bottomMaskPaint.setAntiAlias(true);
        bottomMaskPaint.setStyle(Paint.Style.STROKE);
        bottomMaskPaint.setColor(Color.BLACK);
        bottomMaskPaint.setStrokeWidth(52);

        topMaskPaint = new Paint();
        topMaskPaint.setAntiAlias(true);
        topMaskPaint.setStyle(Paint.Style.STROKE);
        topMaskPaint.setColor(Color.BLACK);
        topMaskPaint.setStrokeWidth(52);

        bgBlackPaint = new Paint();
        bgBlackPaint.setAntiAlias(true);
        bgBlackPaint.setStyle(Paint.Style.STROKE);
        bgBlackPaint.setColor(ContextCompat.getColor(context, R.color.gray_1d1));
        bgBlackPaint.setStrokeWidth(80);

        bgCirclePaint = new Paint();
        bgCirclePaint.setAntiAlias(true);
        bgCirclePaint.setStyle(Paint.Style.FILL);
        bgCirclePaint.setColor(Color.WHITE);


        bgRedPaint = new Paint();
        bgRedPaint.setAntiAlias(true);
        bgRedPaint.setStyle(Paint.Style.STROKE);
        bgRedPaint.setColor(Color.RED);
        bgRedPaint.setStrokeWidth(10);

        bgGrayPaint = new Paint();
        bgGrayPaint.setAntiAlias(true);
        bgGrayPaint.setStyle(Paint.Style.STROKE);
        bgGrayPaint.setColor(ContextCompat.getColor(context, R.color.gray_fc));
        bgGrayPaint.setStrokeWidth(20);

        bottomArrowPaint = new Paint();
        bottomArrowPaint.setAntiAlias(true);
        bottomArrowPaint.setStyle(Paint.Style.FILL);
        bottomArrowPaint.setColor(Color.RED);

        topArrowPaint = new Paint();
        topArrowPaint.setAntiAlias(true);
        topArrowPaint.setStyle(Paint.Style.FILL);
        topArrowPaint.setColor(Color.RED);

        valuePaint = new Paint();
        valuePaint.setAntiAlias(true);


        valuePaint.setStyle(Paint.Style.FILL);
        valuePaint.setColor(ContextCompat.getColor(context, R.color.gray_1a3));
        valuePaint.setTextAlign(Paint.Align.CENTER);
        valuePaint.setTextSize(180);

        tipPaint = new Paint();
        tipPaint.setAntiAlias(true);
        tipPaint.setStyle(Paint.Style.FILL);
        tipPaint.setColor(Color.BLACK);
        tipPaint.setTextAlign(Paint.Align.CENTER);
        tipPaint.setTextSize(40);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        gradientRectF = new RectF(45, 45, w - 45, h - 45);
        bgBlackRectF = new RectF(40, 40, w - 40, h - 40);
        bgRedRectF = new RectF(85, 85, w - 85, h - 85);
        bgGrayRectF = new RectF(100, 100, w - 100, h - 100);

        //半径
        radius = w * 0.5f;

        //底部圆环渐变
        SweepGradient bottomSweepGradient = new SweepGradient(gradientRectF.centerX(),
                gradientRectF.centerY(), colors, new float[]{0f, 0.25f, 0.5f});
        bottomRingPaint.setShader(bottomSweepGradient);

        //上部圆环渐变
        SweepGradient topSweepGradient = new SweepGradient(gradientRectF.centerX(),
                gradientRectF.centerY(), colors, new float[]{0.5f, 0.75f, 1f});
        topRingPaint.setShader(topSweepGradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画外部圆环
        canvas.drawArc(bgBlackRectF, 0, 360, false, bgBlackPaint);
        //画底部圆环
        canvas.drawArc(gradientRectF, 15, 150, false, bottomRingPaint);
        //画上部圆环
        canvas.drawArc(gradientRectF, 195, 150, false, topRingPaint);


        canvas.save();
        //移动canvas
        canvas.translate(radius, radius);
        //刻度线
        for (int i = 0; i < 180; i++) {
            canvas.drawLine(radius - 70, 0, radius - 20, 0, linePaint);
            canvas.rotate(2);
        }
        //操作完成后恢复状态
        canvas.restore();

        //遮罩圆环，覆盖渐变圆环
        canvas.drawArc(gradientRectF, 14 + progress, 152 - progress, false, bottomMaskPaint);
        canvas.drawArc(gradientRectF, 194 + progress, 152 - progress, false, topMaskPaint);

        //内部白色圆
        canvas.drawCircle(bgBlackRectF.centerX(), bgBlackRectF.centerY(), radius - 110, bgCirclePaint);
        //中部红色圆环
        canvas.drawArc(bgRedRectF, 0, 360, false, bgRedPaint);
        //内部灰色圆环
        canvas.drawArc(bgGrayRectF, 0, 360, false, bgGrayPaint);


        //箭头角度
        double degree = Math.toDegrees(Math.asin(10f / (radius - 80))) / 2;
        //底部箭头，三角坐标
        Path bottomPath = new Path();
        bottomPath.moveTo((float) (radius + (radius - 70) * Math.cos(Math.toRadians(progress + 15))),
                (float) (radius + (radius - 70) * Math.sin(Math.toRadians(progress + 15))));// 此点为多边形的起点
        bottomPath.lineTo((float) (radius + (radius - 80) * Math.cos(Math.toRadians(progress - degree + 15))),
                (float) (radius + (radius - 80) * Math.sin(Math.toRadians(progress - degree + 15))));
        bottomPath.lineTo((float) (radius + (radius - 80) * Math.cos(Math.toRadians(progress + degree + 15))),
                (float) (radius + (radius - 80) * Math.sin(Math.toRadians(progress + degree + 15))));
        bottomPath.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(bottomPath, bottomArrowPaint);

        //上部箭头
        Path topPath = new Path();
        topPath.moveTo((float) (radius + (radius - 70) * Math.cos(Math.toRadians(progress + 195))),
                (float) (radius + (radius - 70) * Math.sin(Math.toRadians(progress + 195))));// 此点为多边形的起点
        topPath.lineTo((float) (radius + (radius - 80) * Math.cos(Math.toRadians(progress - degree + 195))),
                (float) (radius + (radius - 80) * Math.sin(Math.toRadians(progress - degree + 195))));
        topPath.lineTo((float) (radius + (radius - 80) * Math.cos(Math.toRadians(progress + degree + 195))),
                (float) (radius + (radius - 80) * Math.sin(Math.toRadians(progress + degree + 195))));
        topPath.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(topPath, topArrowPaint);


        //文本居中显示
        float valueBaseLineY = radius + Math.abs(valuePaint.ascent() + valuePaint.descent()) / 2;
        canvas.drawText(value, gradientRectF.centerX(), valueBaseLineY, valuePaint);

        float tipBaseLineY = radius * 1.45f;
        canvas.drawText("能量信息", gradientRectF.centerX(), tipBaseLineY, tipPaint);
    }


    /**
     *   开始动画，0-150°，实时更新圆环
     */
    public void startAnim() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mAnimator = ValueAnimator.ofInt(0, 150);
        mAnimator.setRepeatCount(-1);
        mAnimator.setDuration(1700);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (int) animation.getAnimatedValue();
                postInvalidate();//触发onDraw
            }
        });
        mAnimator.start();
    }

    //停止动画
    public void stopAnim() {
        if (mAnimator != null) {
            mAnimator.cancel();
            progress = 150;
            postInvalidate();
        }
    }


    //设置值
    public void setValue(String value) {
        this.value = value;
        postInvalidate();
    }

}
