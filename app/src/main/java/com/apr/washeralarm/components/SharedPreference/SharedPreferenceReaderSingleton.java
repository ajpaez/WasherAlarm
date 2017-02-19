package com.apr.washeralarm.components.SharedPreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.apr.washeralarm.R;


public class SharedPreferenceReaderSingleton
{

    private static SharedPreferenceReaderSingleton sSharedPrefs;
    private SharedPreferences mDefaultSharedPreferences;
    private static Context mContext;

    private SharedPreferenceReaderSingleton(Context context)
    {
        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);//context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        mContext = context;
    }

    public static SharedPreferenceReaderSingleton getInstance(Context context)
    {
        if (sSharedPrefs == null)
        {
            sSharedPrefs = new SharedPreferenceReaderSingleton(context.getApplicationContext());
        }
        return sSharedPrefs;
    }

    public static SharedPreferenceReaderSingleton getInstance()
    {
        if (sSharedPrefs != null) {
            return sSharedPrefs;
        }

        throw new IllegalArgumentException("Should use getInstance(Context) at least once before using this method.");
    }


    public boolean sendLocalNotification(){
        String key = mContext.getString(R.string.pref_notificacion_local_key);
        return mDefaultSharedPreferences.getBoolean(key, false);
    }

    public boolean sendExternalNotification(){
        String key = mContext.getString(R.string.pref_notificacion_externa_key);
        return mDefaultSharedPreferences.getBoolean(key, false);
    }

    public String getEmailSenderNotification(){
        String key = mContext.getString(R.string.pref_notificacion_externa_email_sender_key);
        return mDefaultSharedPreferences.getString(key, "");
    }
    public String getPasswordEmailSenderNotification(){
        String key = mContext.getString(R.string.pref_notificacion_externa_email_password_key);
        return mDefaultSharedPreferences.getString(key, "");
    }
    public String getEmailRecipentNotification(){
        String key = mContext.getString(R.string.pref_notificacion_externa_email_recipient_key);
        return mDefaultSharedPreferences.getString(key, "");
    }

    public int getTimeToNotification(){
        String key = mContext.getString(R.string.pref_tiempo_key);
        return mDefaultSharedPreferences.getInt(key, 1);
    }
    public int getSensitivityToNotification(){
        String key = mContext.getString(R.string.pref_sensibilidad_key);
        return mDefaultSharedPreferences.getInt(key, 1);


    }

}
