package ru.dante.scpfoundation.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.receivers.ReceiverTimer;

/**
 * Created for MyApplication by Dante on 20.03.2016  18:13.
 */
public class NotificationUtils
{
    private static final int ID=999;
    private static final String LOG = NotificationUtils.class.getSimpleName();
    public static final String RECEIVER_TIMER_ACTION = "ru.dante.scpfoundation.receivers.ReceiverTimer";

    public static void setAlarm(Context ctx)
    {
        Log.i(LOG, "Setting alarm");
//        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.KITKAT){
//            return;
//        }
        PreferenceManager.setDefaultValues(ctx, R.xml.pref_design, true);
        PreferenceManager.setDefaultValues(ctx, R.xml.pref_notification, true);
        PreferenceManager.setDefaultValues(ctx, R.xml.pref_system, true);


        cancelAlarm(ctx);
        final AlarmManager am = (AlarmManager) ctx.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intentToTimerReceiver = new Intent(ctx.getApplicationContext(), ReceiverTimer.class);
        intentToTimerReceiver.setAction(RECEIVER_TIMER_ACTION);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx.getApplicationContext(),
                ID,
                intentToTimerReceiver, PendingIntent.FLAG_CANCEL_CURRENT);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        int periodInMinutes = Integer.parseInt(pref.getString(ctx.getString(R.string.pref_notifications_key_period), "30"));
        Log.i(LOG, "setting alarm with period " + periodInMinutes);
        long periodInMiliseconds = periodInMinutes * 60 * 1000;
//        //TODO test
//        periodInMiliseconds = 1000 * 20;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + periodInMiliseconds, periodInMiliseconds, pendingIntent);
        } else
        {
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + periodInMiliseconds, periodInMiliseconds, pendingIntent);
        }
    }

    public static void checkAlarm(Context ctx)
    {
        Log.i(LOG, "checkAlarm");
//        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.KITKAT){
//            return;
//        }
        PreferenceManager.setDefaultValues(ctx, R.xml.pref_design, true);
        PreferenceManager.setDefaultValues(ctx, R.xml.pref_notification, true);
        PreferenceManager.setDefaultValues(ctx, R.xml.pref_system, true);

        Intent intent2check = new Intent(ctx.getApplicationContext(), ReceiverTimer.class);
        intent2check.setAction(RECEIVER_TIMER_ACTION);
        boolean alarmUp = (PendingIntent.getBroadcast(ctx.getApplicationContext(), ID, intent2check,
                PendingIntent.FLAG_NO_CREATE) != null);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);

        if (alarmUp)
        {
            //Log.d("myTag", "Alarm is already active");
            Log.i(LOG, "Alarm is already active");
            boolean isNotificationOn = pref.getBoolean(ctx.getString(R.string.pref_notifications_key_enable), false);
            if (!isNotificationOn)
            {
                Log.i(LOG, "But must not be, so...");
                NotificationUtils.cancelAlarm(ctx);
            } else
            {
                Log.i(LOG, "So do nothing");
            }
        } else
        {
            Log.i(LOG, "Alarm IS NOT active");

            boolean isNotificationOn = pref.getBoolean(ctx.getString(R.string.pref_notifications_key_enable), false);
            if (isNotificationOn)
            {
                Log.i(LOG, "But must be, so...");
                NotificationUtils.setAlarm(ctx);
            } else
            {
                Log.i(LOG, "So do nothing");
            }
        }
    }

    public static void cancelAlarm(Context ctx)
    {
        Log.i(LOG, "Canceling alarm");
//        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.KITKAT){
//            return;
//        }
        final AlarmManager am = (AlarmManager) ctx.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intentToTimerReceiver = new Intent(ctx.getApplicationContext(), ReceiverTimer.class);
       intentToTimerReceiver.setAction(RECEIVER_TIMER_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx.getApplicationContext(), ID,
                intentToTimerReceiver,
                PendingIntent.FLAG_CANCEL_CURRENT);

        am.cancel(pendingIntent);
        pendingIntent.cancel();
    }
}