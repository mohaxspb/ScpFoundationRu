package ru.dante.scpfoundation;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ru.dante.scpfoundation.activities.ActivityMain;
import ru.dante.scpfoundation.utils.OfflineUtils;
import ru.dante.scpfoundation.utils.parsing.DownloadAllArticlesInfo;
import ru.dante.scpfoundation.utils.parsing.DownloadArticle;
import ru.dante.scpfoundation.utils.parsing.DownloadObjects;

public class ServiceDownloadAll extends IntentService
{
    public static final String LOG = ServiceDownloadAll.class.getSimpleName();
    public static final String KEY_DOWNLOAD_TYPE = "KEY_DOWNLOAD_TYPE";
    public static final String KEY_POSITION = "KEY_POSITION";
    private static final String KEY_MAX = "KEY_MAX";

    private static final int NOTIFICATION_ID = 42;

    private static final String ACTION_DELETE = "ACTION_DELETE";
    private static final String ACTION_DOWNLOAD = "ACTION_DOWNLOAD";
    private static final String ACTION_DOWNLOAD_ARTICLE = "ACTION_DOWNLOAD_ARTICLE";
    private static final String ACTION_DOWNLOAD_ARTICLES_LIST = "ACTION_DOWNLOAD_ARTICLES_LIST";

    private static ServiceDownloadAll instance = null;
    private static boolean shouldStop = false;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ServiceDownloadAll(String name)
    {
        super(name);
    }

    public ServiceDownloadAll()
    {
        super(LOG);
    }

    public static boolean isRunning()
    {
        return instance != null;
    }

    public static void startDownloadWithType(Context ctx, DownloadTypes type)
    {
        Intent intent = new Intent(ctx, ServiceDownloadAll.class);
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_DOWNLOAD_TYPE, type.ordinal());
        intent.putExtras(bundle);
        intent.setAction(ACTION_DOWNLOAD);
        ctx.startService(intent);
    }

    public static void stopDownload(Context ctx)
    {
        Log.i(LOG, "stopDownload called");
        Intent intent = new Intent(ctx, ServiceDownloadAll.class);
        intent.setAction(ACTION_DELETE);
        ctx.startService(intent);
    }

    /**
     * starts service for download objects
     */
    private static void createIntentForArticleDownload(Context ctx, ArrayList<Article> articles, int position)
    {
        Intent intent = new Intent(ctx, ServiceDownloadAll.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Article.KEY_ARTICLE, articles);
        bundle.putInt(KEY_POSITION, position);
        intent.putExtras(bundle);
        intent.setAction(ACTION_DOWNLOAD_ARTICLE);
        ctx.startService(intent);
    }

    /**
     * creates intent and starts article download from list of all arts
     */
    private static void createIntentForArticleDownload(Context ctx, int position, int max)
    {
        Intent intent = new Intent(ctx, ServiceDownloadAll.class);
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_POSITION, position);
        bundle.putInt(KEY_MAX, max);
        intent.putExtras(bundle);
        intent.setAction(ACTION_DOWNLOAD_ARTICLE);
        ctx.startService(intent);
    }

    private static void createIntentForArticlesListDownload(Context ctx, int position, int max)
    {
        Intent intent = new Intent(ctx, ServiceDownloadAll.class);
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_POSITION, position);
        bundle.putInt(KEY_MAX, max);
        intent.putExtras(bundle);
        intent.setAction(ACTION_DOWNLOAD_ARTICLES_LIST);
        ctx.startService(intent);
    }

    private static PendingIntent getContentIntent(Context ctx)
    {
        return PendingIntent.getActivity(ctx,
                NOTIFICATION_ID,
                new Intent(ctx, ActivityMain.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDestroy()
    {
        Log.i(LOG, "onDestroy");
        instance = null;
        super.onDestroy();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent != null && intent.getAction() != null)
        {
            Log.i(LOG, "onHandleIntent with action: " + intent.getAction());
            Bundle args = intent.getExtras();
            switch (intent.getAction())
            {
                case ACTION_DELETE:
                    Log.e(LOG, "stop download");
                    shouldStop = true;
                    stopForeground(true);
                    return;
                case ACTION_DOWNLOAD_ARTICLE:
                    ArrayList<Article> objects = args.getParcelableArrayList(Article.KEY_ARTICLE);
                    int position = args.getInt(KEY_POSITION);
                    int max = args.getInt(KEY_MAX);
                    if (objects != null)
                    {
                        downloadArticle(objects, position);
                    }
                    else
                    {
                        SharedPreferences preferences = getSharedPreferences(getString(R.string.pref_arts_list), MODE_PRIVATE);
                        for (Map.Entry<String, ?> entry : preferences.getAll().entrySet())
                        {
                            String url = entry.getKey();
                            String title = entry.getValue().toString();
                            downloadArticle(url, title, position, max);
                            break;
                        }
                    }
                    break;
                case ACTION_DOWNLOAD_ARTICLES_LIST:
                    downloadArticlesList(args.getInt(KEY_POSITION), args.getInt(KEY_MAX));
                    break;
                case ACTION_DOWNLOAD:
                    shouldStop = false;
                    DownloadTypes types = DownloadTypes.values()[args.getInt(KEY_DOWNLOAD_TYPE)];
                    Log.i(LOG, "type is: " + types.name());
                    int contentTitle;
                    int contentText;
                    String url;
                    switch (types)
                    {
                        case Type1:
                            contentTitle = R.string.download_type_one;
                            contentText = R.string.download_type_one_list;
                            url = Const.Urls.OBJECTS_1;
                            break;
                        case Type2:
                            contentTitle = R.string.download_type_two;
                            contentText = R.string.download_type_two_list;
                            url = Const.Urls.OBJECTS_2;
                            break;
                        case Type3:
                            contentTitle = R.string.download_type_three;
                            contentText = R.string.download_type_three_list;
                            url = Const.Urls.OBJECTS_3;
                            break;
                        case TypeRu:
                            contentTitle = R.string.download_type_ru;
                            contentText = R.string.download_type_ru_list;
                            url = Const.Urls.OBJECTS_RU;
                            break;
                        case TypeAll:
                            contentTitle = R.string.download_type_all;
                            contentText = R.string.download_type_all_list;
                            url = Const.Urls.NEW_ARTICLES;
                            break;
                        default:
                            Log.i(LOG, "no type...");
                            return;
                    }
                    processStartNotification(contentTitle, contentText, url);
                    break;
            }
        }
    }

    private void processStartNotification(@StringRes int contentTitle, @StringRes int contentText, String url)
    {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(getString(contentTitle))
                .setAutoCancel(false)
                .setContentText(getString(contentText))
                .setSmallIcon(R.drawable.scp_icon);

        builder.setContentIntent(getContentIntent(this));

        startForeground(NOTIFICATION_ID, builder.build());

        switch (url)
        {
            case Const.Urls.OBJECTS_1:
            case Const.Urls.OBJECTS_2:
            case Const.Urls.OBJECTS_3:
            case Const.Urls.OBJECTS_RU:
                ArrayList<Article> objects = DownloadObjects.getAllArticles(url,this);
                if (objects != null)
                {
                    createIntentForArticleDownload(this, objects, 0);
                }
                else
                {
                    //notify about error and remove notif;
                    notifyErrorAndFinish();
                }
                break;
            case Const.Urls.NEW_ARTICLES:
                //download all
                Integer numOfPages = DownloadAllArticlesInfo.getNumOfPages();
                if (numOfPages != null)
                {
                    downloadArticlesList(1, numOfPages);
                }
                else
                {
                    //notify about error and remove notif;
                    Log.e(LOG, "numOfPages is NULL");
                    notifyErrorAndFinish();
                }
                break;
        }
    }

    private void downloadArticlesList(int position, int max)
    {
        Log.i(LOG, "downloadArticlesList with position: " + position);
        if (shouldStop)
        {
            Log.i(LOG, "shouldStop: " + shouldStop);
            stopForeground(true);
            shouldStop = false;
            return;
        }

        SharedPreferences preferences = getSharedPreferences(getString(R.string.pref_arts_list), MODE_PRIVATE);
        if (position >= max)
        {
            //start article download
            createIntentForArticleDownload(this, 0, preferences.getAll().size());
            return;
        }

        NotificationCompat.Builder builderArticlesList = new NotificationCompat.Builder(this);
        builderArticlesList.setContentTitle("Загружено " + String.valueOf(position * 30) + "/" + String.valueOf(max * 30))
                .setAutoCancel(false)
                .setContentText(getString(R.string.downlad_art_list))
                .setProgress(max, position, false)
                .setSmallIcon(R.drawable.scp_icon);

        builderArticlesList.setContentIntent(getContentIntent(this));
        startForeground(NOTIFICATION_ID, builderArticlesList.build());

        ArrayList<Article> articles = DownloadAllArticlesInfo.getArticlesByPage(position);
        if (articles != null)
        {
            Log.i(LOG, "downloadArticlesList size: " + articles.size());
            SharedPreferences.Editor editor = preferences.edit();
            for (Article a : articles)
            {
                //prevent total reloading of all lists
                if(preferences.contains(a.getURL()))
                {
                    //start article download
                    createIntentForArticleDownload(this, 0, preferences.getAll().size());
                    return;
                }
                editor.putString(a.getURL(), a.getTitle());
            }
            editor.apply();
            createIntentForArticlesListDownload(this, position + 1, max);
        }
        else
        {
            Log.i(LOG, "downloadArticlesList articles is NULL");
            //notify about error and remove notif;
            notifyErrorAndFinish();
        }
    }

    private void notifyErrorAndFinish()
    {
        shouldStop = true;
        NotificationCompat.Builder builderError = new NotificationCompat.Builder(this);
        builderError.setContentTitle(getString(R.string.download_error))
                .setAutoCancel(true)
                .setContentText(getString(R.string.download_list_error))
                .setSmallIcon(R.drawable.scp_icon);

        builderError.setContentIntent(getContentIntent(this));
        startForeground(NOTIFICATION_ID, builderError.build());
        sleep(5);
    }

    private void notifyDoneAndFinish()
    {
        NotificationCompat.Builder builderDone = new NotificationCompat.Builder(this);
        builderDone.setContentTitle(getString(R.string.download_done))
                .setAutoCancel(true)
                .setContentText(getString(R.string.download_done))
                .setSmallIcon(R.drawable.scp_icon);

        builderDone.setContentIntent(getContentIntent(this));
        startForeground(NOTIFICATION_ID, builderDone.build());
        sleep(5);
    }

    private void downloadArticle(String url, String title, int position, int max)
    {
        if (shouldStop)
        {
            stopForeground(true);
            shouldStop = false;
            return;
        }

        if (position >= max)
        {
            notifyDoneAndFinish();
            return;
        }

        NotificationCompat.Builder builderArticle = new NotificationCompat.Builder(this);
        builderArticle.setContentTitle("Загружено " + String.valueOf(position) + "/" + max)
                .setAutoCancel(false)
                .setContentText("Загружаю " + title)
                .setProgress(max, position, false)
                .setSmallIcon(R.drawable.scp_icon);

        builderArticle.setContentIntent(getContentIntent(this));
        startForeground(NOTIFICATION_ID, builderArticle.build());

        if (OfflineUtils.hasOfflineWithURL(this, url))
        {
            SharedPreferences preferences = getSharedPreferences(getString(R.string.pref_arts_list), MODE_PRIVATE);
            preferences.edit().remove(url).apply();
            createIntentForArticleDownload(this, position + 1, max);
            return;
        }

        Article object = DownloadArticle.getArticle(url);
        if (object != null)
        {
            OfflineUtils.updateOfflineOnDevice(this, object.getURL(), object.getTitle(), object.getArticlesText(), false);
            SharedPreferences preferences = getSharedPreferences(getString(R.string.pref_arts_list), MODE_PRIVATE);
            preferences.edit().remove(url).apply();
            createIntentForArticleDownload(this, position + 1, max);
        }
        else
        {
            notifyErrorAndFinish();
        }
    }

    private void downloadArticle(ArrayList<Article> objects, int position)
    {
        String url = objects.get(position).getURL();
        String title = objects.get(position).getTitle();
        int max = objects.size();

//        downloadArticle(url, title, position, max);

        if (shouldStop)
        {
            stopForeground(true);
            shouldStop = false;
            return;
        }

        if (position >= max)
        {
            notifyDoneAndFinish();
            return;
        }

        NotificationCompat.Builder builderArticle = new NotificationCompat.Builder(this);
        builderArticle.setContentTitle("Загружено " + String.valueOf(position) + "/" + max)
                .setAutoCancel(false)
                .setContentText("Загружаю " + title)
                .setProgress(max, position, false)
                .setSmallIcon(R.drawable.scp_icon);

        builderArticle.setContentIntent(getContentIntent(this));
        startForeground(NOTIFICATION_ID, builderArticle.build());

        if (OfflineUtils.hasOfflineWithURL(this, url))
        {
            createIntentForArticleDownload(this, objects, position + 1);
            return;
        }

        Article object = DownloadArticle.getArticle(url);
        if (object != null)
        {
            OfflineUtils.updateOfflineOnDevice(this, object.getURL(), object.getTitle(), object.getArticlesText(), false);
            createIntentForArticleDownload(this, objects, position + 1);
        }
        else
        {
            notifyErrorAndFinish();
        }
    }

    private void sleep(long seconds)
    {
        try
        {
            Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public enum DownloadTypes
    {
        Type1, Type2, Type3, TypeRu, TypeAll
    }
}