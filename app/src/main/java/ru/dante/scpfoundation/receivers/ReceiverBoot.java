package ru.dante.scpfoundation.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ru.dante.scpfoundation.utils.NotificationUtils;

public class ReceiverBoot extends BroadcastReceiver
{
    private static final String LOG = ReceiverBoot.class.getName();
//    private Context ctx;

    @Override
    public void onReceive(Context ctx, Intent intent)
    {
        Log.d(LOG, "onReceive " + intent.getAction());
//        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.KITKAT){
//            return;
//        }

//        this.ctx = ctx;

        NotificationUtils.checkAlarm(ctx);
    }

}