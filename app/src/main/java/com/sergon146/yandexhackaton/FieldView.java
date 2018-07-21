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
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.sergon146.yandexhackaton.Labirint.maze;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class FieldView extends View {

    private static final float CELL_SIZE = 24.0f;
    private static final float BALL_RADIUS = 8.0f;
    private static final float HOLE_RADIUS = 10.0f;

    final PointF ball = new PointF();
    final PointF hole = new PointF();

    private static final Paint holePaint;

    private final Bitmap ballBitmap;

    private static final Paint blockPaint;

    static {
        holePaint = new Paint();
        holePaint.setStrokeWidth(4.0f); // TODO
        holePaint.setStyle(Paint.Style.STROKE);
        holePaint.setColor(Color.DKGRAY);
        holePaint.setAntiAlias(true);

        blockPaint = new Paint();
        blockPaint.setColor(Color.BLACK);
        blockPaint.setStyle(Paint.Style.FILL);
        blockPaint.setAntiAlias(true);
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

    boolean[][] field;

    final int fieldSize;

    int fl = 0;
    int ft = 0;

    final int rows;
    final int cols;

    public FieldView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        ballBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ball);

        final float scale = getContext().getResources().getDisplayMetrics().density;
        ballRadius = scale * BALL_RADIUS;
        holeRadius = scale * HOLE_RADIUS;

        cellSize = scale * CELL_SIZE;

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        cols = (int) (metrics.widthPixels / cellSize);
        rows = (int) (metrics.heightPixels / cellSize);

        fieldSize = (int) (Math.max(metrics.widthPixels, metrics.heightPixels) / cellSize);

        reset();
    }

    @Nullable
    ValueAnimator animator;

    boolean caught = true;

    private void reset() {
        field = maze.driver(fieldSize);
        caught = false;
        multiplier = 1.0f;

        fl = 0;
        ft = 0;

        boolean found = false;

        int x = 0, y = 0;

        float halfSize = cellSize / 2.0f;

        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field.length; j++) {
                if (!field[i][j]) {
                    if (!found) {
                        found = true;
                        ball.x = (i + 1) * cellSize - halfSize;
                        ball.y = (j + 1) * cellSize - halfSize;
                    }
                    x = i;
                    y = j;
                }
            }
        }
        hole.x = (x + 1) * cellSize - halfSize;
        hole.y = (y + 1) * cellSize - halfSize;
        holePaint.setColor(Color.DKGRAY);
    }

    private void restart() {
        reset();
        invalidate();
    }

    public void update(float dx, float dy) {
        if (!caught) {
            int i = (int) (ball.x / cellSize);
            int j = (int) (ball.y / cellSize);

            if (i - fl > cols / 2) {
                if (fl < field.length - cols) {
                    fl++;
                }
            }
            else if (i - fl < cols / 2) {
                if (fl > 0) {
                    fl--;
                }
            }

            if (j - ft > rows / 2) {
                if (ft < field.length - rows) {
                    ft++;
                }
            } else if (j - ft < rows / 2) {
                if (ft > 0) {
                    ft--;
                }
            }

            if (dx > 0) {
                dx = Math.min(dx, +cellSize);
            } else if (dx < 0) {
                dx = Math.max(dx, -cellSize);
            }

            if (dy > 0) {
                dy = Math.min(dy, +cellSize);
            } else if (dy < 0) {
                dy = Math.max(dy, -cellSize);
            }

            final int x, y;
            if (dx < 0) {
                x = (int) ((ball.x + dx - ballRadius) / cellSize);
            } else if (dx > 0) {
                x = (int) ((ball.x + dx + ballRadius) / cellSize);
            } else {
                x = i;
            }

            if (dy < 0) {
                y = (int) ((ball.y + dy - ballRadius) / cellSize);
            } else if (dy > 0) {
                y = (int) ((ball.y + dy + ballRadius) / cellSize);
            } else {
                y = j;
            }

            if (0 <= x && x < field.length) {

            } else {
                return;
            }

            if (0 <= y && y < field.length) {

            } else {
                return;
            }

            if (x == i && y == j) {
                // Перемещаемся в той же клетке
                ball.x += dx;
                ball.y += dy;
            } else if (field[x][j] && !field[i][y]) {
                ball.y += dy;
            } else if (field[i][y] && !field[x][j]) {
                ball.x += dx;
            } else if (!field[x][j] && Math.abs(dx) >= Math.abs(dy)) {
                ball.x += dx;
            } else if (!field[i][y] && Math.abs(dy) >= Math.abs(dx)) {
                ball.y += dy;
            } else {
                return;
            }

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
        }
    }

    float lastX = 0, lastY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            lastX = event.getX();
            lastY = event.getY();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            update(event.getX() - lastX, event.getY() - lastY);
            lastX = event.getX();
            lastY = event.getY();
            return true;
        }
        return super.onTouchEvent(event);
    }

    //    @SuppressLint("ClickableViewAccessibility")
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_MOVE && !caught) {
//            ball.x = Math.min(Math.max(event.getX(), ballRadius), getWidth() - ballRadius);
//            ball.y = Math.min(Math.max(event.getY(), ballRadius), getHeight() - ballRadius);
//            invalidate();
//
//            if (Math.hypot(ball.x - hole.x, ball.y - hole.y) < (holeRadius - ballRadius)) {
//                caught = true;
//                if (animator != null) animator.cancel();
//                animator = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(500);
//                animator.setInterpolator(new AccelerateDecelerateInterpolator());
//                animator.addUpdateListener(animator -> {
//                    multiplier = (float) animator.getAnimatedValue();
//                    invalidate();
//                });
//                animator.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        holePaint.setColor(Color.YELLOW);
//                        new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog_Alert)
//                                .setTitle("GOTCHA")
//                                .setPositiveButton("Next Level", null)
//                                .setNegativeButton("Exit", null)
//                                .setOnDismissListener(dialogInterface -> restart())
//                                .show();
//                    }
//                });
//                animator.start();
//            }
//        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            return true;
//        }
//        return super.onTouchEvent(event);
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = fl; i < cols + fl; i++) {
            for (int j = ft; j < rows + ft; j++) {
                final int x = i - fl;
                final int y = j - ft;
                if (field[i][j]) {
                    canvas.drawRect(x * cellSize, y * cellSize, (x + 1) * cellSize, (y + 1) * cellSize, blockPaint);
                }
            }
        }

        final float left = fl * cellSize;
        final float top = ft * cellSize;

        // Относительный координаты
        final float holeX = hole.x - left;
        final float holeY = hole.y - top;

        canvas.drawOval(holeX - holeRadius, holeY - holeRadius, holeX + holeRadius, holeY + holeRadius, holePaint);

        final float ballX = ball.x - left;
        final float ballY = ball.y - top;

        float radius = ballRadius * multiplier;
        @SuppressLint("DrawAllocation")
        RectF rectF = new RectF(ballX - radius, ballY - radius, ballX + radius, ballY + radius);

        canvas.drawBitmap(ballBitmap, null, rectF, null);
    }
}
