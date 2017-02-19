package com.apr.washeralarm.ui.dashboard;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.util.Log;

import com.apr.washeralarm.R;
import com.apr.washeralarm.components.SharedPreference.SharedPreferenceReaderSingleton;
import com.apr.washeralarm.components.notification.NotificationCenter;
import com.apr.washeralarm.components.notification.NotificationCenterDto;
import com.apr.washeralarm.components.services.WatchDogWasherService;
import com.apr.washeralarm.ui.utils.ActivityUtils;

public class MainPresenter implements MainContract.Presenter
{
    private static final String TAG = MainPresenter.class.getSimpleName();

    public static final String WATCH_DOG_HANDLER = "WATCH_DOG_HANDLER";
    public static final int WATCH_DOG_HANDLER_MESSAGE_STOP = 256;

    private MainContract.View mMainView;
    private Context mContext;

    WatchDogWasherService mService = null;
    /**
     * Flag indicating whether we have called bind on the service.
     */
    boolean mBound;

    public MainPresenter(@NonNull MainContract.View mainView, @NonNull Context applicationContext)
    {
        mContext = applicationContext;
        mMainView = mainView;
    }

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            WatchDogWasherService.WatchDogWasherBinder binder = (WatchDogWasherService.WatchDogWasherBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className)
        {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            mBound = false;
        }
    };

    @Override
    public void startStopWash()
    {
        // Bind to the service
        if (!mBound)
        {
            Log.d(TAG, "bind service");
            Messenger messenger = new Messenger(mHandler);
            Intent intent = new Intent(mContext, WatchDogWasherService.class);
            intent.putExtra(WATCH_DOG_HANDLER, messenger);
            mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
        // Unbind from the service
        else if (mBound)
        {
            Log.d(TAG, "unbind a service");
            mContext.unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void sendNotification(int notificationTypeEndWash)
    {
        NotificationCenterDto mNotificationCenterDto = null;
        String titulo = mContext.getString(R.string.notificacion_fin_lavado_titulo);
        String texto = mContext.getString(R.string.notificacion_fin_lavado_texto);
        String info = mContext.getString(R.string.notificacion_fin_lavado_info);

        switch (notificationTypeEndWash){
            case NotificationCenter.NOTIFICATION_TYPE_LOCAL_END_WASH:
                if (SharedPreferenceReaderSingleton.getInstance().sendLocalNotification())
                {
                    mNotificationCenterDto = new NotificationCenterDto.NotificationBuilder(1, titulo, texto, NotificationCenter.NOTIFICATION_TYPE_LOCAL_END_WASH).smallIcon(R.drawable.ic_washing_machine).info(
                            info).buildLocal();
                }
            break;
            case NotificationCenter.NOTIFICATION_TYPE_EXTERNAL_EMAIL_END_WASH:
                if (SharedPreferenceReaderSingleton.getInstance().sendExternalNotification() && ActivityUtils.checkPermissionInternet(mContext))
                {
                    String email = SharedPreferenceReaderSingleton.getInstance().getEmailRecipentNotification();
                    mNotificationCenterDto = new NotificationCenterDto.NotificationBuilder(1, titulo, texto, NotificationCenter.NOTIFICATION_TYPE_EXTERNAL_EMAIL_END_WASH).email(
                            email).buildExternal();
                }
                break;
        }

        NotificationCenter.showNotification(mNotificationCenterDto);
    }

    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MainPresenter.WATCH_DOG_HANDLER_MESSAGE_STOP:
                    startStopWash();
                    mMainView.result(WATCH_DOG_HANDLER_MESSAGE_STOP);

                    break;
            }
        }
    };
}
