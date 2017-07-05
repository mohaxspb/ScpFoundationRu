package ru.dante.scpfoundation.service;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.kuchanov.library.ApiClientModel;
import ru.kuchanov.library.DbProviderModel;
import ru.kuchanov.library.DownloadAllService;
import ru.kuchanov.library.DownloadEntry;
import timber.log.Timber;

/**
 * Created by mohax on 01.07.2017.
 * <p>
 * for ScpFoundationRu
 */
public class DownloadAllServiceTest extends DownloadAllService<Article> {

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
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    public ApiClientModel<Article> getApiClient() {
        return mApiClient;
    }

    @Override
    protected void download(DownloadEntry type) {
        switch (type.resId) {
            case R.string.type_all:
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

    @Override
    protected Article getArticleFromApi(String id) throws Exception, ru.kuchanov.library.ScpParseException {
        return mApiClient.getArticleFromApi(id);
    }
}