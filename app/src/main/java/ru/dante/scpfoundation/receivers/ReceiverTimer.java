package ru.dante.scpfoundation.receivers;


import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;

import ru.dante.scpfoundation.Article;
import ru.dante.scpfoundation.Const;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.activities.ActivityMain;
import ru.dante.scpfoundation.utils.NotificationUtils;
import ru.dante.scpfoundation.utils.parsing.DownloadNewArticles;

public class ReceiverTimer extends BroadcastReceiver implements DownloadNewArticles.UpdateArticlesList
{
    private static final String LOG = ReceiverTimer.class.getSimpleName();
    private Context ctx;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(LOG, "onReceive " + intent.getAction());
//        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.KITKAT){
//            return;
//        }
        if (NotificationUtils.RECEIVER_TIMER_ACTION.equals(intent.getAction()))
        {
            this.ctx = context;
            download();
        }
    }

    protected void download()
    {
        DownloadNewArticles downloadNewArticles = new DownloadNewArticles(1,this, ctx);
        downloadNewArticles.execute();
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void update(ArrayList<Article> listArticles)
    {
        if (listArticles == null)
        {
            return;
        }
        PreferenceManager.setDefaultValues(ctx, R.xml.pref_design, true);
        PreferenceManager.setDefaultValues(ctx, R.xml.pref_notification, true);
        PreferenceManager.setDefaultValues(ctx, R.xml.pref_system, true);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        boolean isNotFirstLoading = sharedPreferences.contains(ctx.getString(R.string.pref_key_first_article_url));
        Log.i(LOG, "isNotFirstLoading: " + isNotFirstLoading);
        if (isNotFirstLoading)
        {
            String firstArticleUrl = sharedPreferences.getString(ctx.getString(R.string.pref_key_first_article_url), "");
            Log.i(LOG, "firstArticleUrl: " + firstArticleUrl);
            for (int i = 0; i < listArticles.size(); i++)
            {
                String currentArticlesUrl = listArticles.get(i).getURL();
                if (firstArticleUrl.equals(currentArticlesUrl))
                {
                    if (i == 0)
                    {
                        Log.d(LOG, i + " Новых статей не обнаружено");
                    } else
                    {
                        Log.d(LOG, i + " новых статей");
                        int newArticlesCounter = sharedPreferences.getInt(ctx.getResources().getString(R.string.key_new_articles_counter), 0);
                        newArticlesCounter += i;
                        sharedPreferences.edit().putInt(ctx.getResources().getString(R.string.key_new_articles_counter), newArticlesCounter).commit();
                        if (newArticlesCounter >= 30)
                        {
                            Log.d(LOG, "обнаружено больше");
                            sendNotification(String.valueOf(Const.DEFAULT_NUM_OF_ARTICLE_OF_PAGE), listArticles);
                        } else
                        {
                            sendNotification(String.valueOf(i), listArticles);
                        }
                    }
                    break;
                } else
                {
                    if (i == listArticles.size() - 1)
                    {
                        Log.d(LOG, "обнаружено больше");
                        if (!firstArticleUrl.equals(""))
                        {
                            sendNotification(String.valueOf(Const.DEFAULT_NUM_OF_ARTICLE_OF_PAGE), listArticles);
                        }
                    }
                }
            }
        }
        sharedPreferences.edit().putString(ctx.getString(R.string.pref_key_first_article_url), listArticles.get(0).getURL()).commit();
    }

    public void sendNotification(String newQuont, ArrayList<Article> dataFromWeb)
    {
        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);

        //icon appears in device notification bar and right hand corner of notification
        builder.setSmallIcon(R.drawable.scp_icon);

        //Set the text that is displayed in the status bar when the notification first arrives.
        builder.setTicker(dataFromWeb.get(0).getTitle());

        // This intent is fired when notification is clicked
        Intent intent = new Intent(ctx, ActivityMain.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pendingIntent);

        // Large icon appears on the left of the notification
//        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));

        // Content title, which appears in large type at the top of the notification
        //		builder.setContentTitle("Новые статьи");

        // Content text, which appears in smaller text below the title
        //				builder.setContentText("Новые статьи");

        // The subtext, which appears under the text on newer devices.
        // This will show-up in the devices with Android 4.2 and above only
        builder.setSubText(dataFromWeb.get(0).getTitle());//"Всего новых статей:");

        builder.setAutoCancel(true);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        if (newQuont.equals(String.valueOf(Const.DEFAULT_NUM_OF_ARTICLE_OF_PAGE)))
        {
            newQuont = "более 30";
            String[] events = new String[dataFromWeb.size()];
            inboxStyle.setBigContentTitle("Новые статьи:");
            // Moves events into the expanded layout
            for (int i = 0; i < events.length; i++)
            {
                events[i] = dataFromWeb.get(i).getTitle();
                inboxStyle.addLine(events[i]);
            }
            builder.setNumber(30);
        } else
        {
            //to test
            //newQuont = "10";
            String[] events = new String[Integer.parseInt(newQuont)];
            // Sets a title for the Inbox in expanded layout
            inboxStyle.setBigContentTitle("Новые статьи: " + newQuont);
            // Moves events into the expanded layout
            for (int i = 0; i < events.length; i++)
            {
                events[i] = dataFromWeb.get(i).getTitle();
                inboxStyle.addLine(events[i]);
            }
            builder.setNumber(Integer.parseInt(newQuont));
        }

        // Moves the expanded layout object into the notification object.
        builder.setStyle(inboxStyle);
        ////////////

        // Content title, which appears in large type at the top of the notification
        builder.setContentTitle("Новые статьи: " + newQuont);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        if (pref.getBoolean(ctx.getString(R.string.pref_notifications_key_vibrate), false))
        {
            builder.setVibrate(new long[]{500, 500, 500, 500, 500});
        }
        //LED
        if (pref.getBoolean(ctx.getString(R.string.pref_notifications_key_led), false))
        {
            builder.setLights(Color.WHITE, 3000, 3000);
        }

        //Sound
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);

        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        // Will display the notification in the notification bar
        notificationManager.notify(100, builder.build());
    }

}