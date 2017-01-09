package ru.kuchanov.scp2.mvp.presenter;

import android.util.Pair;

import java.util.List;

import io.realm.RealmResults;
import io.realm.Sort;
import ru.kuchanov.scp2.Constants;
import ru.kuchanov.scp2.api.ApiClient;
import ru.kuchanov.scp2.db.DbProviderFactory;
import ru.kuchanov.scp2.db.model.Article;
import ru.kuchanov.scp2.manager.MyPreferenceManager;
import ru.kuchanov.scp2.mvp.base.BaseArticlesListMvp;
import rx.Observable;
import timber.log.Timber;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public abstract class BaseObjectsArticlesPresenter<V extends BaseArticlesListMvp.View>
        extends BaseListArticlesPresenter<V>
        implements BaseArticlesListMvp.Presenter<V> {

    private boolean isAlreadyTriedToLoadInitialData;

    protected abstract String getObjectsInDbFieldName();

    protected abstract String getObjectsLink();

    public BaseObjectsArticlesPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    protected Observable<RealmResults<Article>> getDbObservable() {
        return mDbProviderFactory.getDbProvider()
                .getObjectsArticlesSortedAsync(getObjectsInDbFieldName(), Sort.ASCENDING)
                //onNext check if data is empty and we do not tryed to update it
                .doOnNext(data -> {
                    if (!isAlreadyTriedToLoadInitialData && data.isEmpty()) {
                        isAlreadyTriedToLoadInitialData = true;
                        Timber.d("we do not try to load data from api and data is empty... So load from api");
                        getDataFromApi(Constants.Api.ZERO_OFFSET);
                    } else {
                        getView().showCenterProgress(false);
                        getView().enableSwipeRefresh(true);
                    }
                });
    }

    @Override
    protected Observable<List<Article>> getApiObservable(int offset) {
        return mApiClient.getObjectsArticles(getObjectsLink());
    }

    @Override
    protected Observable<Pair<Integer, Integer>> getSaveToDbObservable(List<Article> data, int offset) {
        return mDbProviderFactory.getDbProvider().saveObjectsArticlesList(data, getObjectsInDbFieldName());
    }
}