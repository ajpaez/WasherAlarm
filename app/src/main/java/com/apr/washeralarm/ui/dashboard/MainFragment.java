package com.apr.washeralarm.ui.dashboard;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import com.apr.washeralarm.R;
import com.apr.washeralarm.components.notification.NotificationCenter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements MainContract.View
{

    private MainContract.Presenter mPresenter;

    private ImageView imgStartTop;
    private VideoView videoFondo;
    private boolean run = false;

    public MainFragment()
    {
    }

    public static MainFragment newInstance()
    {
        return new MainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        imgStartTop = (ImageView) root.findViewById(R.id.imgStartStop);
        imgStartTop.setOnClickListener(new View.OnClickListener()
                                       {
                                           @Override
                                           public void onClick(View v)
                                           {
                                               runListenerServiceWasher();
                                           }
                                       }
        );

        videoFondo  = (VideoView) root.findViewById(R.id.videoFondo);
        videoFondo.setVisibility(View.INVISIBLE);
        videoFondo.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.rain_window_final));
        videoFondo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVolume(0,0);
                mp.setLooping(true);
            }
        });


        return root;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (videoFondo != null){
            videoFondo.start();
        }
    }


    private void runListenerServiceWasher(){
        if (run){
            modoOff();
        }else
        {
            modoOn();
        }
        run = !run;

        mPresenter.startStopWash();
    }

    private void modoOff(){
        imgStartTop.setImageResource(R.drawable.ic_power_button_off);
        videoFondo.setVisibility(View.INVISIBLE);
        videoFondo.stopPlayback();
    }


    private void modoOn(){
        imgStartTop.setImageResource(R.drawable.ic_power_button_on);
        videoFondo.setVisibility(View.VISIBLE);
        videoFondo.start();
    }

    @Override
    public void result(int result)
    {
        switch (result)
        {
            case MainPresenter.WATCH_DOG_HANDLER_MESSAGE_STOP:

                modoOff();
                mPresenter.sendNotification(NotificationCenter.NOTIFICATION_TYPE_LOCAL_END_WASH);
                mPresenter.sendNotification(NotificationCenter.NOTIFICATION_TYPE_EXTERNAL_EMAIL_END_WASH);
                run = false;
                break;
        }
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter)
    {
        mPresenter = presenter;
    }
}
