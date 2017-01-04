package ru.kuchanov.scp2.mvp.presenter;

import java.util.List;

import io.realm.Sort;
import ru.kuchanov.scp2.Constants;
import ru.kuchanov.scp2.api.ApiClient;
import ru.kuchanov.scp2.db.DbProviderFactory;
import ru.kuchanov.scp2.db.model.Article;
import ru.kuchanov.scp2.manager.MyPreferenceManager;
import ru.kuchanov.scp2.mvp.base.BasePresenter;
import ru.kuchanov.scp2.mvp.contract.RecentArticles;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class RecentArticlesPresenter extends BasePresenter<RecentArticles.View> implements RecentArticles.Presenter {

    private List<Article> mData;

    public RecentArticlesPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    public void onCreate() {
        Timber.d("onCreate");
        getDataFromDb();
        getDataFromApi(Constants.Api.ZERO_OFFSET);
    }

    @Override
    public List<Article> getData() {
        return mData;
    }

    @Override
    public void getDataFromDb() {
        Timber.d("getDataFromDb");
        mDbProviderFactory.getDbProvider().getRecentArticlesSortedAsync(Article.FIELD_IS_IN_RECENT, Sort.ASCENDING)
                .subscribe(
                        data -> {
                            Timber.d("getDataFromDb data: %s", data);
                            mData = data;
                            getView().updateData(mData);
                        },
                        error -> {
                            getView().showError(error);
                        });
    }

    @Override
    public void getDataFromApi(int offset) {
        Timber.d("getDataFromApi");
        mApiClient.getRecentArticles(offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(apiData -> mDbProviderFactory.getDbProvider().saveRecentArticlesList(apiData, offset))
                .subscribe(
                        data -> {
                            Timber.d("getDataFromApi load data size: %s and offset: %s", data.first, data.second);
                        }
                        , error -> {
                            Timber.e(error);
                            getView().showError(error);
                        });
    }
}