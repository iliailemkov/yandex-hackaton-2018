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
import android.view.animation.AccelerateDecelerateInterpolator;

import com.sergon146.yandexhackaton.Labirint.maze;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class FieldView extends View {

    private static final float CELL_SIZE = 48.0f;
    private static final float BALL_RADIUS = 16.0f;
    private static final float HOLE_RADIUS = 24.0f;

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
    final float cellSize;

    public FieldView(Context context) {
        this(context, null);
    }

    public FieldView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    final boolean[][] field;

    public FieldView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        ballBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ball);

        final float scale = getContext().getResources().getDisplayMetrics().density;
        ballRadius = scale * BALL_RADIUS;
        holeRadius = scale * HOLE_RADIUS;

        cellSize = scale * CELL_SIZE;

        ball.x = ballRadius * 2.5f;
        ball.y = ballRadius * 2.5f;

        reset();

        int cols = Math.round(context.getResources().getDisplayMetrics().widthPixels / cellSize);
        field = maze.driver(cols);
    }

    @Nullable
    ValueAnimator animator;

    boolean caught = false;

    private void reset() {
        caught = false;
        multiplier = 1.0f;
        ball.x = ball.y = ballRadius * 2.5f;
        holePaint.setColor(Color.DKGRAY);
    }

    private void restart() {
        reset();
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
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
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

        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field.length; j++) {
//                canvas.drawRect(i * cellSize, j * cellSize, (i + 1) * cellSize, (j + 1) * cellSize, );
            }
        }


        canvas.drawOval(hole.x - holeRadius, hole.y - holeRadius, hole.x + holeRadius, hole.y + holeRadius, holePaint);


        float radius = ballRadius * multiplier;
        @SuppressLint("DrawAllocation")
        RectF rectF = new RectF(ball.x - radius, ball.y - radius, ball.x + radius, ball.y + radius);
//        canvas.drawCircle(ball.x, ball.y, ballRadius, ballPaint);
        canvas.drawBitmap(ballBitmap, null, rectF, null);
    }
}
