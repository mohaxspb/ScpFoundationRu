package ru.dante.scpfoundation.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Pair;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProvider;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static ru.dante.scpfoundation.service.DownloadAllService.DownloadType.TYPE_1;
import static ru.dante.scpfoundation.service.DownloadAllService.DownloadType.TYPE_2;
import static ru.dante.scpfoundation.service.DownloadAllService.DownloadType.TYPE_3;
import static ru.dante.scpfoundation.service.DownloadAllService.DownloadType.TYPE_ALL;
import static ru.dante.scpfoundation.service.DownloadAllService.DownloadType.TYPE_ARCHIVE;
import static ru.dante.scpfoundation.service.DownloadAllService.DownloadType.TYPE_EXPERIMETS;
import static ru.dante.scpfoundation.service.DownloadAllService.DownloadType.TYPE_INCIDENTS;
import static ru.dante.scpfoundation.service.DownloadAllService.DownloadType.TYPE_INTERVIEWS;
import static ru.dante.scpfoundation.service.DownloadAllService.DownloadType.TYPE_JOKES;
import static ru.dante.scpfoundation.service.DownloadAllService.DownloadType.TYPE_OTHER;
import static ru.dante.scpfoundation.service.DownloadAllService.DownloadType.TYPE_RU;

/**
 * Created by mohax on 11.01.2017.
 * <p>
 * for scp_ru
 */
public class DownloadAllService extends Service {

    private static final int NOTIFICATION_ID = 42;

    public static final int RANGE_NONE = Integer.MIN_VALUE;

    private static final String EXTRA_DOWNLOAD_TYPE = "EXTRA_DOWNLOAD_TYPE";
    private static final String EXTRA_RANGE_START = "EXTRA_RANGE_START";
    private static final String EXTRA_RANGE_END = "EXTRA_RANGE_END";

    private static final String ACTION_STOP = "ACTION_STOP";
    private static final String ACTION_START = "ACTION_START";

    private int rangeStart;
    private int rangeEnd;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            DownloadType.TYPE_1, DownloadType.TYPE_2, DownloadType.TYPE_3, DownloadType.TYPE_RU,
            DownloadType.TYPE_EXPERIMETS, TYPE_OTHER, DownloadType.TYPE_INCIDENTS,
            DownloadType.TYPE_INTERVIEWS, DownloadType.TYPE_ARCHIVE, DownloadType.TYPE_JOKES,
            DownloadType.TYPE_ALL
    })
    public @interface DownloadType {
        String TYPE_1 = "TYPE_1";
        String TYPE_2 = "TYPE_2";
        String TYPE_3 = "TYPE_3";
        String TYPE_RU = "TYPE_RU";

        String TYPE_EXPERIMETS = "TYPE_Experiments";
        String TYPE_OTHER = "TYPE_Other";
        String TYPE_INCIDENTS = "TYPE_Incidents";
        String TYPE_INTERVIEWS = "TYPE_Interviews";
        String TYPE_ARCHIVE = "TYPE_Archive";
        String TYPE_JOKES = "TYPE_Jokes";

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

    public static void startDownloadWithType(
            Context ctx,
            @DownloadType String type,
            int rangeStart,
            int rangeEnd
    ) {
        Intent intent = new Intent(ctx, DownloadAllService.class);
        intent.setAction(ACTION_START);
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_DOWNLOAD_TYPE, type);
        intent.putExtras(bundle);
        intent.putExtra(EXTRA_RANGE_START, rangeStart);
        intent.putExtra(EXTRA_RANGE_END, rangeEnd);
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
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            stopDownloadAndRemoveNotif();
            return super.onStartCommand(intent, flags, startId);
        }
        if (intent.getAction().equals(ACTION_STOP)) {
            stopDownloadAndRemoveNotif();
            return super.onStartCommand(intent, flags, startId);
        }

        //TODO check for not being RANGE_NONE and use while download
        rangeStart = intent.getIntExtra(EXTRA_RANGE_START, RANGE_NONE);
        rangeEnd = intent.getIntExtra(EXTRA_RANGE_END, RANGE_NONE);
        Timber.d("rangeStart/rangeEnd: %s/%s", rangeStart, rangeEnd);

        @DownloadType
        String type = intent.getStringExtra(EXTRA_DOWNLOAD_TYPE);
        Timber.d("onStartCommand with type; %s", type);
        switch (type) {
            case TYPE_1:
                downloadObjects(Constants.Urls.OBJECTS_1, Article.FIELD_IS_IN_OBJECTS_1);
                break;
            case TYPE_2:
                downloadObjects(Constants.Urls.OBJECTS_2, Article.FIELD_IS_IN_OBJECTS_2);
                break;
            case TYPE_3:
                downloadObjects(Constants.Urls.OBJECTS_3, Article.FIELD_IS_IN_OBJECTS_3);
                break;
            case TYPE_RU:
                downloadObjects(Constants.Urls.OBJECTS_RU, Article.FIELD_IS_IN_OBJECTS_RU);
                break;
            case TYPE_EXPERIMETS:
                downloadObjects(Constants.Urls.PROTOCOLS, Article.FIELD_IS_IN_EXPERIMETS);
                break;
            case TYPE_OTHER:
                downloadObjects(Constants.Urls.OTHERS, Article.FIELD_IS_IN_OTHER);
                break;
            case TYPE_INCIDENTS:
                downloadObjects(Constants.Urls.INCEDENTS, Article.FIELD_IS_IN_INCIDENTS);
                break;
            case TYPE_INTERVIEWS:
                downloadObjects(Constants.Urls.INTERVIEWS, Article.FIELD_IS_IN_INTERVIEWS);
                break;
            case TYPE_ARCHIVE:
                downloadObjects(Constants.Urls.ARCHIVE, Article.FIELD_IS_IN_ARCHIVE);
                break;
            case TYPE_JOKES:
                downloadObjects(Constants.Urls.JOKES, Article.FIELD_IS_IN_JOKES);
                break;
            case TYPE_ALL:
                downloadAll();
                break;
            default:
                throw new IllegalArgumentException("unexpected type");
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
                    //test value
//                    mMaxProgress = 3;
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
//                //test value
//                .flatMap(list -> Observable.just(list.subList(0, mMaxProgress)))
                .map(limitArticles)
                //TODO refactor it - from here code is equal to objects one
                .map(articles -> {
                    List<Article> articlesToDownload = new ArrayList<>();
                    DbProvider dbProvider = mDbProviderFactory.getDbProvider();
                    for (Article article : articles) {
                        Article articleInDb = dbProvider.getUnmanagedArticleSync(article.url);
                        if (articleInDb == null || articleInDb.text == null) {
                            articlesToDownload.add(article);
                        } else {
                            mCurProgress++;
                            Timber.d("already downloaded: %s", article.url);
                            Timber.d("mCurProgress %s, mMaxProgress: %s", mCurProgress, mMaxProgress);
//                            showNotificationDownloadProgress(getString(R.string.download_recent_title),
//                                    mCurProgress, mMaxProgress, mNumOfErrors);
                        }
                    }
                    dbProvider.close();
                    return articlesToDownload;
                })
                .flatMap(Observable::from)
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
                .flatMap(article -> mDbProviderFactory.getDbProvider().saveArticleSync(article))
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
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                        article -> showNotificationDownloadProgress(getString(R.string.download_objects_title),
                                mCurProgress, mMaxProgress, mNumOfErrors),
                        e -> {
                            Timber.e(e, "error download objects");
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

    private void downloadObjects(String link, String dbField) {
        showNotificationDownloadList();
        //download lists
        Observable<List<Article>> articlesObservable;
        switch (link) {
            case Constants.Urls.ARCHIVE:
                articlesObservable = mApiClient.getMaterialsArchiveArticles();
                break;
            case Constants.Urls.JOKES:
                articlesObservable = mApiClient.getMaterialsJokesArticles();
                break;
            case Constants.Urls.OBJECTS_1:
            case Constants.Urls.OBJECTS_2:
            case Constants.Urls.OBJECTS_3:
            case Constants.Urls.OBJECTS_RU:
                articlesObservable = mApiClient.getObjectsArticles(link);
                break;
            default:
                articlesObservable = mApiClient.getMaterialsArticles(link);
                break;
        }

        //just for test use just n elements
//        final int testMaxProgress = 8;
        Subscription subscription = articlesObservable
                .map(limitArticles)
                // just for test use just n elements
//                .doOnNext(articles -> mMaxProgress = testMaxProgress)
                .doOnError(throwable -> showNotificationSimple(
                        getString(R.string.error_notification_title),
                        getString(R.string.error_notification_objects_list_download_content)
                ))
                .onExceptionResumeNext(Observable.<List<Article>>empty().delay(5, TimeUnit.SECONDS))
                //just for test use just n elements
//                .map(list -> list.subList(0, testMaxProgress))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(articles -> mDbProviderFactory.getDbProvider()
                        .<Pair<Integer, Integer>>saveObjectsArticlesList(articles, dbField)
                        .flatMap(integerIntegerPair -> Observable.just(articles)))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(articles -> {
                    List<Article> articlesToDownload = new ArrayList<>();
                    DbProvider dbProvider = mDbProviderFactory.getDbProvider();
                    for (Article article : articles) {
                        Article articleInDb = dbProvider.getUnmanagedArticleSync(article.url);
                        if (articleInDb == null || articleInDb.text == null) {
                            articlesToDownload.add(article);
                        } else {
                            mCurProgress++;
                            Timber.d("already downloaded: %s", article.url);
                            Timber.d("mCurProgress %s, mMaxProgress: %s", mCurProgress, mMaxProgress);
//                            showNotificationDownloadProgress(getString(R.string.download_objects_title), mCurProgress, mMaxProgress, mNumOfErrors);
                        }
                    }
                    dbProvider.close();
                    return articlesToDownload;
                })
                .flatMap(Observable::from)
                //try to load article
                //on error increase counters and resume query, emiting onComplete to article observable
                .flatMap(article -> mApiClient.getArticle(article.url).onErrorResumeNext(throwable -> {
                    Timber.e(throwable, "error while load article: %s", article.url);
                    mNumOfErrors++;
                    mCurProgress++;
                    showNotificationDownloadProgress(
                            getString(R.string.download_objects_title),
                            mCurProgress, mMaxProgress, mNumOfErrors
                    );
                    return Observable.empty();
                }))
                .flatMap(article -> mDbProviderFactory.getDbProvider().saveArticleSync(article))
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
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                        article -> showNotificationDownloadProgress(getString(R.string.download_objects_title),
                                mCurProgress, mMaxProgress, mNumOfErrors),
                        e -> {
                            Timber.e(e, "error download objects");
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

    private Func1<List<Article>, List<Article>> limitArticles = articles -> {
//        mCurProgress = 0;
//        mMaxProgress = articles.size();
//        //if not have subscription
//        //check if we have limit in downloads
//        //if so - set maxProgress to limit
//        //and use sub string of articles
//
//        if (!mMyPreferenceManager.isHasSubscription()) {
//            FirebaseRemoteConfig config = FirebaseRemoteConfig.getInstance();
//            if (!config.getBoolean(Constants.Firebase.RemoteConfigKeys.DOWNLOAD_ALL_ENABLED_FOR_FREE)) {
//                long limit = config.getLong(Constants.Firebase.RemoteConfigKeys.DOWNLOAD_FREE_ARTICLES_LIMIT);
//                mMaxProgress = (int) limit;
//
//                if (articles.size() > mMaxProgress) {
//                    articles = articles.subList(mCurProgress, mMaxProgress);
//                }
//            }
//        }
//        return articles;

//        mCurProgress = rangeStart;
//        mMaxProgress = rangeEnd;
        mCurProgress = 0;
        mMaxProgress = rangeEnd - rangeStart;

        articles = articles.subList(mCurProgress, mMaxProgress);
        return articles;
    };

    private void showNotificationDownloadList() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentTitle(getString(R.string.download_objects_title))
                .setAutoCancel(false)
                .setContentText(getString(R.string.downlad_art_list))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
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
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setSmallIcon(R.drawable.ic_download_white_24dp);

        startForeground(NOTIFICATION_ID, builderArticlesList.build());
    }

    private void showNotificationSimple(String title, String content) {
        NotificationCompat.Builder builderArticlesList = new NotificationCompat.Builder(this);
        builderArticlesList
                .setContentTitle(title)
                .setContentText(content)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setSmallIcon(R.drawable.ic_bug_report_white_24dp);

        startForeground(NOTIFICATION_ID, builderArticlesList.build());
    }
}