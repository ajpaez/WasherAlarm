package com.apr.washeralarm;

import android.app.Application;
import android.app.NotificationManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.apr.washeralarm.components.SharedPreference.SharedPreferenceReaderSingleton;
import com.apr.washeralarm.components.notification.NotificationCenter;
import com.apr.washeralarm.utils.Utils;

public class App extends Application
{
    private static String TAG = App.class.getName();

    @Override
    public void onCreate()
    {
        super.onCreate();


        // ============ NOTIFICATION CENTER AND PUSH NOTIFICATION =============
        if (Utils.isRunningMarshmallowOrLater())
        {
            Log.i(TAG, "Instanciando el notification center");
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            new NotificationCenter(getApplicationContext(), notificationManager);
        }
        // ============ END =============


        // ============ SHARED PREFERENCES SINGLETON AND SET DEFAULT VALUES =============
        SharedPreferenceReaderSingleton.getInstance(getApplicationContext());
        PreferenceManager.setDefaultValues(this, R.xml.pref_notification, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_service, false);
        // ==================================== END =====================================
    }
}
