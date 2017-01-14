package ru.kuchanov.scp2.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.manager.MyNotificationManager;
import timber.log.Timber;

public class ReceiverBoot extends BroadcastReceiver {

    @Inject
    protected MyNotificationManager mMyNotificationManager;

    @Override
    public void onReceive(Context ctx, Intent intent) {
        Timber.d("onReceive with action: %s", intent.getAction());

        MyApplication.getAppComponent().inject(this);
        mMyNotificationManager.checkAlarm();
    }
}