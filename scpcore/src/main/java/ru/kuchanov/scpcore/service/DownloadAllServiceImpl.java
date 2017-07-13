package ru.kuchanov.scpcore.service;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import ru.kuchanov.scpcore.Constants;
import ru.kuchanov.scpcore.BaseApplication;
import ru.kuchanov.scpcore.R;
import ru.kuchanov.scpcore.R2;
import ru.kuchanov.scpcore.api.ApiClient;
import ru.kuchanov.scpcore.db.DbProviderFactory;
import ru.kuchanov.scpcore.db.model.Article;
import ru.kuchanov.scpcore.manager.MyPreferenceManager;
import ru.kuchanov.scp.downloads.ApiClientModel;
import ru.kuchanov.scp.downloads.DbProviderModel;
import ru.kuchanov.scp.downloads.DownloadAllService;
import ru.kuchanov.scp.downloads.DownloadEntry;
import timber.log.Timber;

/**
 * Created by mohax on 01.07.2017.
 * <p>
 * for ScpFoundationRu
 */
public class DownloadAllServiceImpl extends DownloadAllService<Article> {

    @Inject
    MyPreferenceManager mMyPreferenceManager;
    @Inject
    ApiClient mApiClient;
    @Inject
    DbProviderFactory mDbProviderFactory;

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
        BaseApplication.getAppComponent().inject(this);
    }

    @Override
    public ApiClientModel<Article> getApiClient() {
        return mApiClient;
    }

    @Override
    protected void download(DownloadEntry type) {
        switch (type.resId) {
            case R2.string.type_all:
                downloadAll();
                break;
            default:
                downloadObjects(type);
                break;
        }
    }

    @Override
    protected int getNumOfArticlesOnRecentPage() {
        return Constants.Api.NUM_OF_ARTICLES_ON_RECENT_PAGE;
    }

    @Override
    protected DbProviderModel<Article> getDbProviderModel() {
        return mDbProviderFactory.getDbProvider();
    }
}