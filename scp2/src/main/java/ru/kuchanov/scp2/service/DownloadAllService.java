package ru.kuchanov.scp2.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.NotificationCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;

import ru.kuchanov.scp2.Constants;
import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.api.ApiClient;
import ru.kuchanov.scp2.db.DbProviderFactory;
import ru.kuchanov.scp2.manager.MyPreferenceManager;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static ru.kuchanov.scp2.service.DownloadAllService.DownloadType.TYPE_1;
import static ru.kuchanov.scp2.service.DownloadAllService.DownloadType.TYPE_2;
import static ru.kuchanov.scp2.service.DownloadAllService.DownloadType.TYPE_3;
import static ru.kuchanov.scp2.service.DownloadAllService.DownloadType.TYPE_ALL;
import static ru.kuchanov.scp2.service.DownloadAllService.DownloadType.TYPE_RU;

/**
 * Created by mohax on 11.01.2017.
 * <p>
 * for scp_ru
 */
public class DownloadAllService extends Service {

    private static final int NOTIFICATION_ID = 42;
    private static final String EXTRA_DOWNLOAD_TYPE = "EXTRA_DOWNLOAD_TYPE";
    private static final String ACTION_STOP = "ACTION_STOP";
    private static final String ACTION_START = "ACTION_START";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({DownloadType.TYPE_1, DownloadType.TYPE_2, DownloadType.TYPE_3, DownloadType.TYPE_RU, DownloadType.TYPE_ALL})
    public @interface DownloadType {
        String TYPE_1 = "TYPE_1";
        String TYPE_2 = "TYPE_2";
        String TYPE_3 = "TYPE_3";
        String TYPE_RU = "TYPE_RU";
        String TYPE_ALL = "TYPE_ALL";
    }

    private static DownloadAllService instance = null;

    @Inject
    MyPreferenceManager mMyPreferenceManager;
    @Inject
    ApiClient mApiClient;
    @Inject
    DbProviderFactory mDbProviderFactory;

    private int curProgress;
    private int mMaxProgress;

    private CompositeSubscription mCompositeSubscription;

    public static boolean isRunning() {
        return instance != null;
    }

    public static void startDownloadWithType(Context ctx, @DownloadType String type) {
        Intent intent = new Intent(ctx, DownloadAllService.class);
        intent.setAction(ACTION_START);
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_DOWNLOAD_TYPE, type);
        intent.putExtras(bundle);
        ctx.startService(intent);
    }

    public static void stopDownload(Context ctx) {
        Timber.d("stopDownload called");
        Intent intent = new Intent(ctx, DownloadAllService.class);
        intent.setAction(ACTION_STOP);
        ctx.startService(intent);
    }
//
//    public DownloadAllService(String name) {
//        super(name);
//    }
//
//    public DownloadAllService() {
//        super(DownloadAllService.class.getSimpleName());
//    }

    @Override
    public void onDestroy() {
        Timber.d("onDestroy");
        instance = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Timber.d("onCreate");
        super.onCreate();
        instance = this;
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_STOP)) {
            if (mCompositeSubscription != null && !mCompositeSubscription.isUnsubscribed()) {
                mCompositeSubscription.unsubscribe();
            }
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
        @DownloadType
        String type = intent.getStringExtra(EXTRA_DOWNLOAD_TYPE);
        Timber.d("onStartCommand with type; %s", type);
        switch (type) {
            case TYPE_1:
                downloadObjects(Constants.Urls.OBJECTS_1);
                break;
            case TYPE_2:
                downloadObjects(Constants.Urls.OBJECTS_2);
                break;
            case TYPE_3:
                downloadObjects(Constants.Urls.OBJECTS_3);
                break;
            case TYPE_RU:
                downloadObjects(Constants.Urls.OBJECTS_RU);
                break;
            case TYPE_ALL:
                //TODO
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void downloadObjects(String link) {
        showNotificationDownloadObjects();
        //download list

        Subscription subscription = mApiClient.getObjectsArticles(link)
                .observeOn(Schedulers.io())
                .doOnNext(articles -> mMaxProgress = articles.size())
                .flatMap(Observable::from)
                .flatMap(article -> mApiClient.getArticle(article.url))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(article -> mDbProviderFactory.getDbProvider().saveArticle(article))
//                .flatMap(article -> Observable.merge())
//                .map(article -> new Pair<>(0, 1))
                .subscribe(
                        article -> {
                            curProgress++;
                            showNotificationDownloadProgress(getString(R.string.download_objects_title), curProgress, mMaxProgress);
                        },
                        error -> {
                            Timber.e(error, "error download objects");
                            //TODO
                        },
                        () -> {
                            Timber.d("onCompleted");
                            //TODO}
                        }
                );
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }

    private void showNotificationDownloadObjects() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentTitle(getString(R.string.download_objects_title))
                .setAutoCancel(false)
                .setContentText(getString(R.string.downlad_art_list))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_download_white_48dp))
                .setSmallIcon(R.drawable.ic_download_white_24dp);

        startForeground(NOTIFICATION_ID, builder.build());
    }

    private void showNotificationDownloadProgress(String title, int cur, int max) {
        NotificationCompat.Builder builderArticlesList = new NotificationCompat.Builder(this);
        String content = "Загружено " + cur + "/" + max;
        builderArticlesList.setContentTitle(title)
                .setAutoCancel(false)
                .setContentText(content)
                .setProgress(max, cur, false)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_download_white_48dp))
                .setSmallIcon(R.drawable.ic_download_white_24dp);

        startForeground(NOTIFICATION_ID, builderArticlesList.build());
    }

//    private void showNotificationDownloadProgress(int page, int max) {
//        NotificationCompat.Builder builderArticlesList = new NotificationCompat.Builder(this);
//        String title = "Загружено " +
//                String.valueOf(page * Constants.Api.NUM_OF_ARTICLES_ON_RECENT_PAGE) +
//                "/" + String.valueOf(max * 30);
//        builderArticlesList.setContentTitle(title)
//                .setAutoCancel(false)
//                .setContentText(getString(R.string.downlad_art_list))
//                .setProgress(max, page, false)
//                .setSmallIcon(R.drawable.ic_download_white_24dp);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                this,
//                NOTIFICATION_ID,
//                new Intent(this, MainActivity.class),
//                PendingIntent.FLAG_UPDATE_CURRENT
//        );
//
//        builderArticlesList.setContentIntent(pendingIntent);
//        startForeground(NOTIFICATION_ID, builderArticlesList.build());
//    }
}
