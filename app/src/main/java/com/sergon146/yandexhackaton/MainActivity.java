package com.sergon146.yandexhackaton;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ShapeDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    //CustomDrawableView mCustomDrawableView = null;
    ShapeDrawable mDrawable = new ShapeDrawable();
    public float xPosition, xAcceleration, xVelocity = 0.0f;
    public float yPosition, yAcceleration, yVelocity = 0.0f;
    private Bitmap mBitmap;
    private Bitmap mWood;
    private SensorManager sensorManager = null;
    public float frameTime = 0.666f;
    private FieldView fieldView;

    private Boolean isSensor = false;

    Thread thread;
    int coef = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Set FullScreen & portrait
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Get a reference to a SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_GAME);

        //mCustomDrawableView = new CustomDrawableView(this);
        setContentView(R.layout.activity_main);

        fieldView = findViewById(R.id.field);
        fieldView.setListener(v -> finish());
        fieldView.setGotchaListener(v -> {
            stopService(new Intent(this, BackgroundSoundService.class));

            Intent svc = new Intent(this, GotchaSoundService.class);
            startService(svc);
        });
        fieldView.setRestartListener(v -> {
            stopService(new Intent(this, GotchaSoundService.class));
            stopService(new Intent(this, BackgroundSoundService.class));
            Intent svc = new Intent(this, BackgroundSoundService.class);
            startService(svc);
        });
        fieldView.setKeepScreenOn(true);

        //Calculate Boundry
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        String gameMode = getIntent().getStringExtra(WelcomeActivity.LEVEL_EXTRA);
        isSensor = getIntent().getBooleanExtra(WelcomeActivity.SENSOR_EXTRA, false);
        thread = new Thread((Runnable) () -> {
            for (; ; ) {
                try {
                    coef = GameMode.getCoefficient(gameMode, coef);
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        thread.start();

        Intent svc = new Intent(this, BackgroundSoundService.class);
        startService(svc);
    }

    private void updateBall(int speedCoef) {
        //Calculate new speed
        xVelocity += (xAcceleration * speedCoef * frameTime);
        yVelocity += (yAcceleration * speedCoef * frameTime);

        //Calc distance travelled in that time
        float xS = (xVelocity / 2) * frameTime;
        float yS = (yVelocity / 2) * frameTime;

        //Add to position negative due to sensor
        //readings being opposite to what we want!
        xPosition += xS;
        yPosition -= yS;

        fieldView.update(xS, -yS);

//        Log.d("ball", String.format("x:%f y:%f", xPosition, yPosition));
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(isSensor)
        {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
                //Set sensor values as acceleration

                xAcceleration = sensorEvent.values[1];
                yAcceleration = -sensorEvent.values[0];
                //            Log.d("sensor", String.format("x:%f y:%f", xAcceleration, yAcceleration));

                updateBall(coef);
            }
        }
    }

    // I've chosen to not implement this method
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        // Unregister the listener
        sensorManager.unregisterListener(this);
        thread.interrupt();
        super.onStop();
    }

    /*public class CustomDrawableView extends View {
        public CustomDrawableView(Context context) {
            super(context);
            //Bitmap ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
            final float dstWidth = 8f;
            final float dstHeight = 8f;
            //mBitmap = Bitmap.createScaledBitmap(ball, dstWidth, dstHeight, true);
            //mWood = BitmapFactory.decodeResource(getResources(), R.drawable.wood);
        }

        protected void onDraw(Canvas canvas) {
            final Bitmap bitmap = mBitmap;
            canvas.drawBitmap(mWood, 0, 0, null);
            canvas.drawBitmap(bitmap, xPosition, yPosition, null);
            invalidate();
        }
    }*/

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, BackgroundSoundService.class));
    }
}
