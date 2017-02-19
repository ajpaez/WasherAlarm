package com.apr.washeralarm.components.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.apr.washeralarm.components.SharedPreference.SharedPreferenceReaderSingleton;
import com.apr.washeralarm.components.services.SendMailService;

import static android.app.Notification.PRIORITY_DEFAULT;

public class NotificationCenter
{

    public static final int NOTIFICATION_TYPE_LOCAL_END_WASH = 1;
    public static final int NOTIFICATION_TYPE_EXTERNAL_EMAIL_END_WASH = 2;

    private static NotificationManager mNotificationManager;
    private static Context mContext;
    private static Uri soundUriDefault;

    public NotificationCenter(Context context, NotificationManager mNotificationManager)
    {
        NotificationCenter.mNotificationManager = mNotificationManager;
        NotificationCenter.mContext = context;
        soundUriDefault = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    public static void showNotification(NotificationCenterDto notificationCenterDto)
    {

        if (mNotificationManager == null || mContext == null || notificationCenterDto == null)
        {
            //TODO
        }
        else
        {
            switch (notificationCenterDto.getType()){
                case NOTIFICATION_TYPE_LOCAL_END_WASH:
                    launchLocalNotification(notificationCenterDto);
                    break;
                case NOTIFICATION_TYPE_EXTERNAL_EMAIL_END_WASH:
                    launchExternalNotification(notificationCenterDto);
                    break;
            }
        }
    }

    private static void launchLocalNotification(NotificationCenterDto notificationCenterDto)
    {
        NotificationCompat.Builder myNotification = new NotificationCompat.Builder(mContext);
        myNotification.setSmallIcon(notificationCenterDto.getIcon());
        myNotification.setContentTitle(notificationCenterDto.getTitle());
        myNotification.setContentText(notificationCenterDto.getText());
        myNotification.setContentInfo(notificationCenterDto.getInfo());

        myNotification.setPriority(PRIORITY_DEFAULT);
        myNotification.setSound(soundUriDefault);
        myNotification.setLights(Color.MAGENTA, 3000, 500);
        myNotification.setVibrate(new long[]{0, 500, 500, 500});

        //Generamos la notificacion
        Notification notification = myNotification.build();

        //Llamamos al gestor de notificaciones para lanzarla
        mNotificationManager.notify(notificationCenterDto.getIdNotification(), notification);
    }

    private static void launchExternalNotification(NotificationCenterDto notificationCenterDto)
    {
        String emailRecipent = SharedPreferenceReaderSingleton.getInstance().getEmailRecipentNotification();

        if (!emailRecipent.equals(notificationCenterDto.getEmail()))
        {
            //Creating SendMail object
            SendMailService sm = new SendMailService(mContext, notificationCenterDto.getEmail(), notificationCenterDto.getTitle(), notificationCenterDto.getText());

            //Executing sendmail to send email
            sm.execute();
        }




    }


}

