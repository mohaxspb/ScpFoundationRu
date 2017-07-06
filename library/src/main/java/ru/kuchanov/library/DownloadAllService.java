package ru.kuchanov.library;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by mohax on 11.01.2017.
 * <p>
 * for scp_ru
 */
public abstract class DownloadAllService<T extends ArticleModel> extends Service {

    private static final int NOTIFICATION_ID = 42;

    private static final int DELAY_BEFORE_HIDE_NOTIFICATION = 5;

    public static final int RANGE_NONE = Integer.MIN_VALUE;
    private static final String EXTRA_DOWNLOAD_TYPE = "EXTRA_DOWNLOAD_TYPE";

    private static final String EXTRA_RANGE_START = "EXTRA_RANGE_START";
    private static final String EXTRA_RANGE_END = "EXTRA_RANGE_END";
    private static final String ACTION_STOP = "ACTION_STOP";

    private static final String ACTION_START = "ACTION_START";

    protected static DownloadAllService instance = null;

    private int rangeStart;
    private int rangeEnd;

    private int mCurProgress;
    private int mMaxProgress;
    private int mNumOfErrors;

    private CompositeSubscription mCompositeSubscription;

    public static boolean isRunning() {
        return instance != null;
    }

    public static void startDownloadWithType(
            Context ctx,
            DownloadEntry type,
            int rangeStart,
            int rangeEnd,
            Class clazz
    ) {
        Intent intent = new Intent(ctx, clazz);
        intent.setAction(ACTION_START);
        intent.putExtra(EXTRA_DOWNLOAD_TYPE, type);
        intent.putExtra(EXTRA_RANGE_START, rangeStart);
        intent.putExtra(EXTRA_RANGE_END, rangeEnd);
        ctx.startService(intent);
    }

    public static void stopDownload(Context ctx, Class clazz) {
        Timber.d("stopDownload called");
        Intent intent = new Intent(ctx, clazz);
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
    }

    private void stopDownloadAndRemoveNotif() {
        Timber.d("stopDownloadAndRemoveNotif");
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
        Timber.d("onStartCommand: %s, %s, %s", intent, false, startId);
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            stopDownloadAndRemoveNotif();
            return super.onStartCommand(intent, flags, startId);
        }
        if (intent.getAction().equals(ACTION_STOP)) {
            stopDownloadAndRemoveNotif();
            return super.onStartCommand(intent, flags, startId);
        }

        //check for not being RANGE_NONE and use while download
        rangeStart = intent.getIntExtra(EXTRA_RANGE_START, RANGE_NONE);
        rangeEnd = intent.getIntExtra(EXTRA_RANGE_END, RANGE_NONE);
        Timber.d("rangeStart/rangeEnd: %s/%s", rangeStart, rangeEnd);

        DownloadEntry type = (DownloadEntry) intent.getSerializableExtra(EXTRA_DOWNLOAD_TYPE);
        download(type);

        return super.onStartCommand(intent, flags, startId);
    }

    protected abstract void download(DownloadEntry type);

    public abstract ApiClientModel<T> getApiClient();

    protected abstract int getNumOfArticlesOnRecentPage();

    protected abstract DbProviderModel<T> getDbProviderModel();

    protected void downloadAll() {
        Timber.d("downloadAll");
        showNotificationDownloadList();
        //download list
        Subscription subscription = getApiClient().getRecentArticlesPageCountObservable()
                .doOnError(e -> {
                    Timber.e(e);
                    showNotificationSimple(
                            getString(R.string.error_notification_title),
                            getString(R.string.error_notification_recent_list_download_content)
                    );
                })
                .onExceptionResumeNext(Observable.<Integer>empty().delay(DELAY_BEFORE_HIDE_NOTIFICATION, TimeUnit.SECONDS))
                //if we have limit we must not load all lists of articles
                .map(pageCount -> (rangeStart != RANGE_NONE && rangeEnd != RANGE_NONE)
                        ? (int) Math.ceil((double) rangeEnd / getNumOfArticlesOnRecentPage()) : pageCount)
                .doOnNext(pageCount -> mMaxProgress = pageCount)
                //FIX ME for test do not load all arts lists
//                .doOnNext(pageCount -> mMaxProgress = 2)
                .flatMap(integer -> Observable.range(1, mMaxProgress))
                .flatMap(integer -> getApiClient().getRecentArticlesForPage(integer)
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
//                //FIX ME test value
//                .flatMap(list -> Observable.just(list.subList(0, mMaxProgress)))
                .flatMap(this::downloadAndSaveArticles)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                        article -> {
                            Timber.d("download complete");
                            stopDownloadAndRemoveNotif();
                        }
                );
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }

    protected void downloadObjects(DownloadEntry type) {
        Timber.d("downloadObjects: %s", type);
        showNotificationDownloadList();
        //download lists
        Observable<List<T>> articlesObservable;

        if (type.resId == R.string.type_archive) {
            articlesObservable = getApiClient().getMaterialsArchiveArticles();
        } else if (type.resId == R.string.type_jokes) {
            articlesObservable = getApiClient().getMaterialsJokesArticles();
        } else if (type.resId == R.string.type_1
                || type.resId == R.string.type_2
                || type.resId == R.string.type_3
                || type.resId == R.string.type_4
                || type.resId == R.string.type_ru) {
            articlesObservable = getApiClient().getObjectsArticles(type.url);
        } else {
            articlesObservable = getApiClient().getMaterialsArticles(type.url);
        }

        //just for test use just n elements
//        final int testMaxProgress = 8;
        Subscription subscription = articlesObservable
                .doOnError(throwable -> showNotificationSimple(
                        getString(R.string.error_notification_title),
                        getString(R.string.error_notification_objects_list_download_content)
                ))
                .onExceptionResumeNext(Observable.<List<T>>empty().delay(DELAY_BEFORE_HIDE_NOTIFICATION, TimeUnit.SECONDS))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(articles -> getDbProviderModel()
                        .<Pair<Integer, Integer>>saveObjectsArticlesList(articles, type.dbField)
                        .flatMap(integerIntegerPair -> Observable.just(articles)))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .flatMap(this::downloadAndSaveArticles)
                .subscribe(
                        article -> {
                            Timber.d("download complete");
                            stopDownloadAndRemoveNotif();
                        }
                );
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }

    private Observable<List<T>> downloadAndSaveArticles(List<T> articlesToDwonload) {
        return Observable.just(articlesToDwonload)
                .map(limitArticles)
                .map(articles -> {
                    List<T> articlesToDownload = new ArrayList<>();
                    DbProviderModel<T> dbProvider = getDbProviderModel();
                    for (T article : articles) {
                        T articleInDb = dbProvider.getUnmanagedArticleSync(article.getUrl());
                        if (articleInDb == null || articleInDb.getText() == null) {
                            articlesToDownload.add(article);
                        } else {
                            mCurProgress++;
                            Timber.d("already downloaded: %s", article.getUrl());
                            Timber.d("mCurProgress %s, mMaxProgress: %s", mCurProgress, mMaxProgress);
                        }
                    }
                    dbProvider.close();
                    return articlesToDownload;
                })
                .flatMap(articles -> {
                    DbProviderModel<T> dbProvider = getDbProviderModel();
                    for (int i = 0; i < articles.size(); i++) {
                        T articleToDownload = articles.get(i);
                        try {
                            T articleDownloaded = getApiClient().getArticleFromApi(articleToDownload.getUrl());
                            if (articleDownloaded != null) {
                                dbProvider.saveArticleSync(articleDownloaded, false);
                                Timber.d("downloaded: %s", articleDownloaded.getUrl());
                                mCurProgress++;
                                Timber.d("mCurProgress %s, mMaxProgress: %s", mCurProgress, mMaxProgress);
                                showNotificationDownloadProgress(getString(R.string.download_objects_title),
                                        mCurProgress, mMaxProgress, mNumOfErrors);
                            } else {
                                mNumOfErrors++;
                                mCurProgress++;
                                showNotificationDownloadProgress(
                                        getString(R.string.download_objects_title),
                                        mCurProgress, mMaxProgress, mNumOfErrors
                                );
                            }
                        } catch (Exception | ScpParseException e) {
                            Timber.e(e);
                            mNumOfErrors++;
                            mCurProgress++;
                            showNotificationDownloadProgress(
                                    getString(R.string.download_objects_title),
                                    mCurProgress, mMaxProgress, mNumOfErrors
                            );
                        }
                    }
                    dbProvider.close();
                    return Observable.just(articles);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnError(e -> {
                    Timber.e(e);
                    showNotificationSimple(
                            getString(R.string.error_notification_title),
                            getString(R.string.error_notification_download_failed, e.getMessage()));
                })
                .onErrorResumeNext(Observable.<List<T>>just(Collections.EMPTY_LIST).delay(DELAY_BEFORE_HIDE_NOTIFICATION, TimeUnit.SECONDS))
                .doOnNext(articles -> showNotificationSimple(
                        getString(R.string.download_complete_title),
                        getString(R.string.download_complete_title_content,
                                mCurProgress - mNumOfErrors, mMaxProgress, mNumOfErrors)
                ))
                .flatMap(articles -> Observable.just(articles).delay(DELAY_BEFORE_HIDE_NOTIFICATION, TimeUnit.SECONDS));
    }

    private Func1<List<T>, List<T>> limitArticles = articles -> {
        mCurProgress = 0;
        if (rangeStart == RANGE_NONE && rangeEnd == RANGE_NONE) {
            mMaxProgress = articles.size();
        } else {
            mMaxProgress = rangeEnd - rangeStart;
            articles = articles.subList(rangeStart, rangeEnd);
        }
        return articles;
    };

    private void showNotificationDownloadList() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentTitle(getString(R.string.download_objects_title))
                .setAutoCancel(false)
                .setContentText(getString(R.string.downlad_art_list))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
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
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_download_white_24dp);

        startForeground(NOTIFICATION_ID, builderArticlesList.build());
    }

    private void showNotificationSimple(String title, String content) {
        NotificationCompat.Builder builderArticlesList = new NotificationCompat.Builder(this);
        builderArticlesList
                .setContentTitle(title)
                .setContentText(content)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.ic_bug_report_white_24dp);

        startForeground(NOTIFICATION_ID, builderArticlesList.build());
    }
}