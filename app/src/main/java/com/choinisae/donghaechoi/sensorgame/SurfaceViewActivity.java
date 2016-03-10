package com.choinisae.donghaechoi.sensorgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by donghaechoi on 2016. 3. 10..
 */
public class SurfaceViewActivity extends AppCompatActivity implements SensorEventListener {

    private static int deviceWidth, deviceHeight;
    private Sensor mAcceleroMeter;
    private float accelXValue;
    private float accelYValue;
    private SensorManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventSufaceView mySurfaceView = new EventSufaceView(this);
        setContentView(mySurfaceView);

        mManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        mAcceleroMeter = mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        DisplayMetrics display = getApplicationContext().getResources().getDisplayMetrics();
        deviceWidth = display.widthPixels;
        deviceHeight = display.heightPixels;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == mAcceleroMeter.getType()) {
            accelXValue = event.values[0];
            accelYValue = event.values[1];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class EventSufaceView extends SurfaceView implements SurfaceHolder.Callback {

        private SurfaceThread thread;

        @Override
        public boolean performClick() {
            return super.performClick();
        }

        public EventSufaceView(Context context) {
            super(context);
            init();
        }

        public EventSufaceView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public EventSufaceView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        private void init() {
            getHolder().addCallback(this);
            thread = new SurfaceThread(getHolder(), this);
            setFocusable(true);
        }

        public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        }

        public void surfaceCreated(SurfaceHolder holder) {
            thread.setRunning(true);
            thread.start();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            boolean retry = true;
            thread.setRunning(false);
            while (retry) {
                try {
                    thread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class SurfaceThread extends Thread {

        private SurfaceHolder mThreadSurfaceHolder;
        private EventSufaceView mThreadSurfaceView;
        private boolean myThreadRun = false;

        public SurfaceThread(SurfaceHolder surfaceHolder, EventSufaceView surfaceView) {
            mThreadSurfaceHolder = surfaceHolder;
            mThreadSurfaceView = surfaceView;
        }

        public void setRunning(boolean b) {
            myThreadRun = b;
        }

        @Override
        public void run() {
            while (myThreadRun) {
                Canvas c = null;
                try {
                    c = mThreadSurfaceHolder.lockCanvas(null);
                    synchronized (mThreadSurfaceHolder) {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                        c.drawBitmap(bitmap, accelXValue, accelYValue, null);
                    }
                } finally {
                    if (c != null) {
                        mThreadSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }
}

