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
import ru.kuchanov.scp2.mvp.contract.Objects1Articles;
import rx.Observable;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class Objects1ArticlesPresenter
        extends BaseListArticlesPresenter<Objects1Articles.View>
        implements Objects1Articles.Presenter {

    private static final String sObjectsLink = Constants.Urls.OBJECTS_1;
    private static final String sObjectsInDbFieldName = Article.FIELD_IS_IN_OBJECTS_1;

    public Objects1ArticlesPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    protected Observable<RealmResults<Article>> getDbObservable() {
        return mDbProviderFactory.getDbProvider().getObjectsArticlesSortedAsync(sObjectsInDbFieldName, Sort.ASCENDING);
    }

    @Override
    protected Observable<List<Article>> getApiObservable(int offset) {
        return mApiClient.getObjectsArticles(sObjectsLink);
    }

    @Override
    protected Observable<Pair<Integer, Integer>> getSaveToDbObservable(List<Article> data, int offset) {
        return mDbProviderFactory.getDbProvider().saveObjectsArticlesList(data, sObjectsInDbFieldName);
    }
}