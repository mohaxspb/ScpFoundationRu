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
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import ru.kuchanov.scp2.Constants;
import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.api.ApiClient;
import ru.kuchanov.scp2.db.DbProvider;
import ru.kuchanov.scp2.db.DbProviderFactory;
import ru.kuchanov.scp2.db.model.Article;
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

    private int mCurProgress;
    private int mMaxProgress;
    private int mNumOfErrors;

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

    private void stopDownloadAndRemoveNotif() {
        mCurProgress = 0;
        mMaxProgress = 0;
        mNumOfErrors = 0;
        if (mCompositeSubscription != null && !mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_STOP)) {
            stopDownloadAndRemoveNotif();
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
                downloadAll();
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void downloadAll() {
        Timber.d("downloadAll");
        showNotificationDownloadList();
        //download list
        Subscription subscription = mApiClient.getRecentArticlesPageCount()
                .doOnNext(pageCount -> {
                    mMaxProgress = pageCount;
                    //FIXME test value
                    mMaxProgress = 3;
                })
                .doOnError(throwable -> showNotificationSimple(
                        getString(R.string.error_notification_title),
                        getString(R.string.error_notification_recent_list_download_content)
                ))
                .onExceptionResumeNext(Observable.<Integer>empty().delay(5, TimeUnit.SECONDS))
                .flatMap(integer -> Observable.range(1, mMaxProgress))
                .flatMap(integer -> mApiClient.getRecentArticlesForPage(integer)
                        .doOnNext(list -> {
                            mCurProgress = integer;
                            showNotificationDownloadProgress(getString(R.string.notification_recent_list_title),
                                    mCurProgress, mMaxProgress, mNumOfErrors);
                        })
                        .flatMap(Observable::from)
                        .doOnError(throwable -> {
                            mCurProgress = integer;
                            mNumOfErrors++;
                            showNotificationDownloadProgress(getString(R.string.notification_recent_list_title),
                                    mCurProgress, mMaxProgress, mNumOfErrors);
                        })
                        .onExceptionResumeNext(Observable.empty()))
                .toList()
                .doOnNext(pageCount -> {
                    mCurProgress = 0;
                    mMaxProgress = pageCount.size();
                    //FIXME test value
                    mMaxProgress = 30;
                })
                //FIXME test value
                .flatMap(list -> Observable.just(list.subList(0, mMaxProgress)))
                .flatMap(Observable::from)
                //TODO refactor it - from here code is equal to objects one
                .filter(article -> {
                    DbProvider dbProvider = mDbProviderFactory.getDbProvider();
                    Article articleInDb = dbProvider.getUnmanagedArticleSync(article.url);
                    dbProvider.close();
                    if (articleInDb == null || articleInDb.text == null) {
                        return true;
                    } else {
                        mCurProgress++;
                        Timber.d("already downloaded: %s", article.url);
                        Timber.d("mCurProgress %s, mMaxProgress: %s", mCurProgress, mMaxProgress);
                        showNotificationDownloadProgress(getString(R.string.download_recent_title),
                                mCurProgress, mMaxProgress, mNumOfErrors);
                        return false;
                    }
                })
                //try to load article
                //on error increase counters and resume query, emiting onComplete to article observable
                .flatMap(article -> mApiClient.getArticle(article.url)
                        .onErrorResumeNext(throwable -> {
                            Timber.e(throwable, "error while load article: %s", article.url);
                            mNumOfErrors++;
                            mCurProgress++;
                            showNotificationDownloadProgress(getString(R.string.download_recent_title),
                                    mCurProgress, mMaxProgress, mNumOfErrors
                            );
                            return Observable.empty();
                        })
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(article -> mDbProviderFactory.getDbProvider().saveArticle(article))
                //TODO we can show notif here as we do it for articles lists
                .flatMap(article -> {
                    mCurProgress++;
                    Timber.d("mCurProgress %s, mMaxProgress: %s", mCurProgress, mMaxProgress);
                    if (mCurProgress == mMaxProgress) {
                        showNotificationSimple(
                                getString(R.string.download_complete_title),
                                getString(R.string.download_complete_title_content,
                                        mCurProgress - mNumOfErrors, mMaxProgress, mNumOfErrors)
                        );
                        return Observable.just(article).delay(5, TimeUnit.SECONDS);
                    } else {
                        return Observable.just(article);
                    }
                })
                .subscribe(
                        article -> showNotificationDownloadProgress(getString(R.string.download_objects_title),
                                mCurProgress, mMaxProgress, mNumOfErrors),
                        error -> {
                            Timber.e(error, "error download objects");
                            stopDownloadAndRemoveNotif();
                        },
                        () -> {
                            Timber.d("onCompleted");
                            stopDownloadAndRemoveNotif();
                        }
                );
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }

    private void downloadObjects(String link) {
        showNotificationDownloadList();
        //download list

        //just for test use just n elements
//        final int testMaxProgress = 8;

        Subscription subscription = mApiClient.getObjectsArticles(link)
                .doOnNext(articles -> mMaxProgress = articles.size())
                // just for test use just n elements
//                .doOnNext(articles -> mMaxProgress = testMaxProgress)
                .doOnError(throwable -> showNotificationSimple(
                        getString(R.string.error_notification_title),
                        getString(R.string.error_notification_objects_list_download_content)
                ))
                .onExceptionResumeNext(Observable.<List<Article>>empty().delay(5, TimeUnit.SECONDS))
                //just for test use just n elements
//                .map(list -> list.subList(0, testMaxProgress))
                .flatMap(Observable::from)
                .filter(article -> {
                    DbProvider dbProvider = mDbProviderFactory.getDbProvider();
                    Article articleInDb = dbProvider.getUnmanagedArticleSync(article.url);
                    dbProvider.close();
                    if (articleInDb == null || articleInDb.text == null) {
                        return true;
                    } else {
                        mCurProgress++;
                        Timber.d("already downloaded: %s", article.url);
                        Timber.d("mCurProgress %s, mMaxProgress: %s", mCurProgress, mMaxProgress);
                        showNotificationDownloadProgress(getString(R.string.download_objects_title), mCurProgress, mMaxProgress, mNumOfErrors);
                        return false;
                    }
                })
                //try to load article
                //on error increase counters and resume query, emiting onComplete to article observable
                .flatMap(article -> mApiClient.getArticle(article.url)
                        .onErrorResumeNext(throwable -> {
                            Timber.e(throwable, "error while load article: %s", article.url);
                            mNumOfErrors++;
                            mCurProgress++;
                            showNotificationDownloadProgress(
                                    getString(R.string.download_objects_title),
                                    mCurProgress, mMaxProgress, mNumOfErrors
                            );
                            return Observable.empty();
                        })
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(article -> mDbProviderFactory.getDbProvider().saveArticle(article))
                .flatMap(article -> {
                    Timber.d("downloaded: %s", article.url);
                    mCurProgress++;
                    Timber.d("mCurProgress %s, mMaxProgress: %s", mCurProgress, mMaxProgress);
                    if (mCurProgress == mMaxProgress) {
                        showNotificationSimple(
                                getString(R.string.download_complete_title),
                                getString(R.string.download_complete_title_content,
                                        mCurProgress - mNumOfErrors, mMaxProgress, mNumOfErrors)
                        );
                        return Observable.just(article).delay(5, TimeUnit.SECONDS);
                    } else {
                        return Observable.just(article);
                    }
                })
                .subscribe(
                        article -> showNotificationDownloadProgress(getString(R.string.download_objects_title),
                                mCurProgress, mMaxProgress, mNumOfErrors),
                        error -> {
                            Timber.e(error, "error download objects");
                            stopDownloadAndRemoveNotif();
                        },
                        () -> {
                            Timber.d("onCompleted");
                            stopDownloadAndRemoveNotif();
                        }
                );
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }

    private void showNotificationDownloadList() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentTitle(getString(R.string.download_objects_title))
                .setAutoCancel(false)
                .setContentText(getString(R.string.downlad_art_list))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_download_white_48dp))
                .setSmallIcon(R.drawable.ic_download_white_24dp);

        startForeground(NOTIFICATION_ID, builder.build());
    }

    private void showNotificationDownloadProgress(String title, int cur, int max, int errorsCount) {
        NotificationCompat.Builder builderArticlesList = new NotificationCompat.Builder(this);
        String content = getString(R.string.download_progress_content, cur, max, errorsCount);
        builderArticlesList.setContentTitle(title)
                .setAutoCancel(false)
                .setContentText(content)
                .setProgress(max, cur, false)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_download_white_48dp))
                .setSmallIcon(R.drawable.ic_download_white_24dp);

        startForeground(NOTIFICATION_ID, builderArticlesList.build());
    }

    private void showNotificationSimple(String title, String content) {
        NotificationCompat.Builder builderArticlesList = new NotificationCompat.Builder(this);
        builderArticlesList
                .setContentTitle(title)
                .setContentText(content)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_bug_report_white_48dp))
                .setSmallIcon(R.drawable.ic_bug_report_white_24dp);

        startForeground(NOTIFICATION_ID, builderArticlesList.build());
    }
}