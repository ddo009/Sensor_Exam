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
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by donghaechoi on 2016. 3. 10..
 */
public class SurfaceViewActivity extends AppCompatActivity implements SensorEventListener {

    private static int deviceWidth;
    private static int deviceHeight;
    private Sensor mAcceleroMeter;
    private static float accelXValue = 0;
    private static float accelYValue = 0;
    private SensorManager mManager;
    private final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventSurfaceView view = new EventSurfaceView(this);
        setContentView(view);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        mAcceleroMeter = mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mManager.registerListener(this, mAcceleroMeter, SensorManager.SENSOR_DELAY_GAME);

        DisplayMetrics display = getApplicationContext().getResources().getDisplayMetrics();
        deviceWidth = display.widthPixels;
        deviceHeight = display.heightPixels;

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventSurfaceView view = new EventSurfaceView(this);
        setContentView(view);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == mAcceleroMeter.getType()) {
            accelXValue -= event.values[0];
            accelYValue += event.values[1];
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class EventSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceThread thread;
        private Bitmap mBitmap;

        @Override
        public boolean performClick() {
            return super.performClick();
        }

        public EventSurfaceView(Context context) {
            super(context);
            init();
        }

        public EventSurfaceView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public EventSurfaceView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        private void init() {
            getHolder().addCallback(this);
            thread = new SurfaceThread(getHolder(), this);
//            setFocusable(true);
        }

        @Override
        protected void onDraw(Canvas canvas) {
//            canvas.drawBitmap(mBitmap, accelXValue, accelYValue, null);
//            invalidate();
        }

        public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        }

        public void surfaceCreated(SurfaceHolder holder) {
            thread.setRunning(true);
            thread.start();
        }

//        public void move(float x, float y) {
//            accelXValue -= (x * 4f);
//            accelYValue += (y * 4f);
//            invalidate();
//        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            boolean retry = true;
            thread.interrupt();
            thread.setRunning(false);
            while (retry) {
                try {
                    thread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    Toast.makeText(getApplication(), "스레드 종료", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class SurfaceThread extends Thread {

        private SurfaceHolder mThreadSurfaceHolder;
        private EventSurfaceView mThreadSurfaceView;
        private boolean myThreadRun = false;
        private Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        public SurfaceThread(SurfaceHolder surfaceHolder, EventSurfaceView surfaceView) {
            mThreadSurfaceHolder = surfaceHolder;
            mThreadSurfaceView = surfaceView;
        }

        public void setRunning(boolean bool) {
            myThreadRun = bool;
        }

        @Override
        public void run() {
            while (myThreadRun) {
                Canvas canvas = null;
                try {
                    canvas = mThreadSurfaceHolder.lockCanvas(null);
                    if (canvas != null) {
                        synchronized (mThreadSurfaceHolder) {
//                        Log.d(TAG, "run: " + accelXValue);
//                        Log.d(TAG, "run: " + accelYValue);
                            int size = mBitmap.getWidth();
                            if (accelXValue < 0) {
                                accelXValue = 0;
                            } else if (deviceWidth < size + accelXValue) {
                                accelXValue = deviceWidth - size;
                            }
                            if (accelYValue < 0) {
                                accelYValue = 0;
                            } else if (deviceHeight < size + accelYValue) {
                                accelYValue = deviceHeight - size;
                            }
                            canvas.drawBitmap(mBitmap, accelXValue, accelYValue, null);
                        }
                    }
                } finally {
                    if (canvas != null) {
                        mThreadSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

}

