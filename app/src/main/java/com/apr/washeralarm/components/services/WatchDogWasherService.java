package com.apr.washeralarm.components.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.apr.washeralarm.components.SharedPreference.SharedPreferenceReaderSingleton;
import com.apr.washeralarm.ui.dashboard.MainPresenter;

public class WatchDogWasherService extends Service implements SensorEventListener
{
    private static final int MS_SENSOR_DELAY = 30 * 1000;

    private static final String TAG = WatchDogWasherService.class.getSimpleName();

    private Messenger mMessengerHandler;
    private final IBinder mServiceBinder = new WatchDogWasherBinder();
    private Thread mThread;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private long lastUpdateSensorValue = 0;
    private float last_x, last_y, last_z;
    private boolean stop = false;
    private long timeSinceStop = 0;

    private int timeDelay;
    private int sensitivity;

    public WatchDogWasherService()
    {
        super();
        timeDelay = SharedPreferenceReaderSingleton.getInstance().getTimeToNotification();
        timeDelay = timeDelay * 60 * 1000;
        sensitivity = SharedPreferenceReaderSingleton.getInstance().getSensitivityToNotification();
    }

    @Override
    public void onCreate()
    {
        Log.d(TAG, "The service is being created");

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        // Creando el hilo sobre el que se ejecutara el servicio
        ServiceThread mServiceThread = new ServiceThread();
        mThread = new Thread(mServiceThread);
        mThread.start();

        super.onCreate();
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "The service is no longer used and is being destroyed");
        mSensorManager.unregisterListener(this);

        // Parando el hilo de forma segura
        if (mThread != null)
        {
            //mThread.join();
            mThread.interrupt();
            mThread = null;
            Log.d(TAG, "HILO PARADO");
        }
        super.onDestroy();
    }

    // ~ Binder
    // ======================================================================================

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent)
    {
        Bundle extras = intent.getExtras();

        Log.d(TAG, "A client is binding to the service with bindService()");

        if (extras != null)
        {
            Log.d(TAG, "onBind with extra");
            mMessengerHandler = (Messenger) extras.get(MainPresenter.WATCH_DOG_HANDLER);
        }

        return this.mServiceBinder;
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class WatchDogWasherBinder extends Binder
    {
        public WatchDogWasherService getService()
        {
            // Return this instance of LocalService so clients can call public methods
            return WatchDogWasherService.this;
        }
    }

    // ~ Thread
    // ======================================================================================
    private final class ServiceThread implements Runnable
    {
        private volatile boolean running = true;

        @Override
        public void run()
        {
            if (mAccelerometer != null)
            {
                while (running)
                {
                    long curTime = System.currentTimeMillis();
                    long timeStop = curTime - timeSinceStop;
                    boolean stopByTime = timeStop > timeDelay;
                    if (stop && stopByTime)
                    {
                        sendMessageToClient(MainPresenter.WATCH_DOG_HANDLER_MESSAGE_STOP);
                        running = false;
                    }
                }
            }
            else
            {
                // Failure! No magnetometer.
            }
        }
    }

    // ~ Messenger
    // ======================================================================================

    public void sendMessageToClient(int message)
    {
        try
        {
            Message messenger = new Message();
            messenger.what = message;
            mMessengerHandler.send(messenger);
        }
        catch (Exception e)
        {

        }
    }

    // ~ SensorEventListener
    // ======================================================================================
    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        if (Sensor.TYPE_ACCELEROMETER == sensorEvent.sensor.getType())
        {
            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdateSensorValue) > MS_SENSOR_DELAY)
            {
                lastUpdateSensorValue = curTime;

                //see image -> https://developer.android.com/images/axis_device.png
                Log.d(TAG,
                        "SensorEvent from sensor: " + sensorEvent.sensor.getName() + ", value [x]: " + sensorEvent.values[0] + ", [y]" + sensorEvent.values[1] + ", [z]" + sensorEvent.values[2]);

                if (isStop(last_x, sensorEvent.values[0]))
                {
                    if (timeSinceStop == 0)
                    {
                        timeSinceStop = System.currentTimeMillis();
                    }
                    stop = true;
                }
                else
                {
                    stop = false;
                    timeSinceStop = 0;
                }

                // explanation https://developer.android.com/guide/topics/sensors/sensors_motion.html#sensors-motion-accel
                last_x = sensorEvent.values[0];
                last_y = sensorEvent.values[1];
                last_z = sensorEvent.values[2];
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {// Do something here if sensor accuracy changes.

    }

    private boolean isStop(float lastValue, float value)
    {
        return Math.abs(Math.floor(lastValue * 100) - Math.floor(value * 100)) < ( 2 + (sensitivity / 100));
    }
}

