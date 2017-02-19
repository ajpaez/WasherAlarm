package com.apr.washeralarm.ui.dashboard;

import com.apr.washeralarm.ui.helpers.BasePresenter;
import com.apr.washeralarm.ui.helpers.BaseView;

public interface MainContract
{

    interface View extends BaseView<Presenter>
    {
        void result(int result);
    }

    interface Presenter extends BasePresenter
    {

        void startStopWash();

        void sendNotification(int notificationTypeEndWash);
    }


}
