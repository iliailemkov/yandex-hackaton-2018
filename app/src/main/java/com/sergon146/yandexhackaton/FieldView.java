package com.sergon146.yandexhackaton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class FieldView extends View {

    final PointF ball = new PointF();
    final PointF hole = new PointF();

    private static final Paint ballPaint;
    private static final Paint holePaint;

    private final Bitmap ballBitmap;

    static {
        ballPaint = new Paint();
        ballPaint.setColor(Color.WHITE);
        ballPaint.setAntiAlias(true);

        holePaint = new Paint();
        holePaint.setStrokeWidth(4.0f);
        holePaint.setStyle(Paint.Style.STROKE);
        holePaint.setColor(Color.DKGRAY);
        holePaint.setAntiAlias(true);
    }

    final float ballRadius;
    float multiplier = 1.0f;

    final float holeRadius;

    public FieldView(Context context) {
        this(context, null);
    }

    public FieldView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FieldView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

//        setBackgroundColor(Color.GREEN);

        ballBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ball);

        final float scale = getContext().getResources().getDisplayMetrics().density;
        ballRadius = scale * 16.0f;
        holeRadius = scale * 24.0f;

        ball.x = ballRadius * 2.5f;
        ball.y = ballRadius * 2.5f;
    }

    @Nullable
    ValueAnimator animator;

    boolean caught = false;

    private void restart() {
        caught = false;
        multiplier = 1.0f;
        ball.x = ball.y = ballRadius * 1.5f;
        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE && !caught) {
            ball.x = Math.min(Math.max(event.getX(), ballRadius), getWidth() - ballRadius);
            ball.y = Math.min(Math.max(event.getY(), ballRadius), getHeight() - ballRadius);
            invalidate();

            if (Math.hypot(ball.x - hole.x, ball.y - hole.y) < (holeRadius - ballRadius)) {
                caught = true;
                if (animator != null) animator.cancel();
                animator = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(500);
                animator.addUpdateListener(animator -> {
                    multiplier = (float) animator.getAnimatedValue();
                    invalidate();
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        holePaint.setColor(Color.YELLOW);
                        new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert)
                                .setTitle("GOTCHA")
                                .setPositiveButton("Next Level", null)
                                .setNegativeButton("Exit", null)
                                .setOnDismissListener(dialogInterface -> restart())
                                .show();
                    }
                });
                animator.start();
            } else {
                if (animator != null) animator.cancel();
                multiplier = 1.0f;
                holePaint.setColor(Color.DKGRAY);
            }
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        hole.x = getWidth() / 2.0f;
        hole.y = getHeight() / 2.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        canvas.drawOval(hole.x - holeRadius, hole.y - holeRadius, hole.x + holeRadius, hole.y + holeRadius, holePaint);


        float radius = ballRadius * multiplier;
        @SuppressLint("DrawAllocation")
        RectF rectF = new RectF(ball.x - radius, ball.y - radius, ball.x + radius, ball.y + radius);
//        canvas.drawCircle(ball.x, ball.y, ballRadius, ballPaint);
        canvas.drawBitmap(ballBitmap, null, rectF, null);
    }
}
