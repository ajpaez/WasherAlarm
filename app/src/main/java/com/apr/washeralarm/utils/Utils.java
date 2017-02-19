package com.apr.washeralarm.utils;

import android.os.Build;

public class Utils
{

    private static String TAG = Utils.class.getName();

    public static boolean isRunningMarshmallowOrLater()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }



}
