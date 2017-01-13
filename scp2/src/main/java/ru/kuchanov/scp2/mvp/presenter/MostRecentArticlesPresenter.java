package ru.kuchanov.scp2.mvp.presenter;

import android.util.Pair;

import java.util.List;

import io.realm.RealmResults;
import io.realm.Sort;
import ru.kuchanov.scp2.api.ApiClient;
import ru.kuchanov.scp2.db.DbProviderFactory;
import ru.kuchanov.scp2.db.model.Article;
import ru.kuchanov.scp2.manager.MyPreferenceManager;
import ru.kuchanov.scp2.mvp.contract.RecentArticlesMvp;
import rx.Observable;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class MostRecentArticlesPresenter extends BaseListArticlesPresenter<RecentArticlesMvp.View> implements RecentArticlesMvp.Presenter {

    public MostRecentArticlesPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    protected Observable<RealmResults<Article>> getDbObservable() {
        return mDbProviderFactory.getDbProvider().getRecentArticlesSortedAsync(Article.FIELD_IS_IN_RECENT, Sort.ASCENDING);
    }

    @Override
    protected Observable<List<Article>> getApiObservable(int offset) {
        return mApiClient.getRecentArticlesForOffset(offset);
    }

    @Override
    protected Observable<Pair<Integer, Integer>> getSaveToDbObservable(List<Article> data, int offset) {
        return mDbProviderFactory.getDbProvider().saveRecentArticlesList(data, offset);
    }
}