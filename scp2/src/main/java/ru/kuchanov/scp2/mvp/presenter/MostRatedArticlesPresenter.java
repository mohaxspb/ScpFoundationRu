package ru.kuchanov.scp2.mvp.presenter;

import android.util.Pair;

import java.util.List;

import io.realm.RealmResults;
import io.realm.Sort;
import ru.kuchanov.scp2.api.ApiClient;
import ru.kuchanov.scp2.db.DbProviderFactory;
import ru.kuchanov.scp2.db.model.Article;
import ru.kuchanov.scp2.manager.MyPreferenceManager;
import ru.kuchanov.scp2.mvp.contract.RatedArticlesMvp;
import rx.Observable;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class MostRatedArticlesPresenter extends BaseListArticlesPresenter<RatedArticlesMvp.View> implements RatedArticlesMvp.Presenter {

    public MostRatedArticlesPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    protected Observable<RealmResults<Article>> getDbObservable() {
        return mDbProviderFactory.getDbProvider().getRatedArticlesSortedAsync(Article.FIELD_IS_IN_MOST_RATED, Sort.ASCENDING);
    }

    @Override
    protected Observable<List<Article>> getApiObservable(int offset) {
        return mApiClient.getRatedArticles(offset);
    }

    @Override
    protected Observable<Pair<Integer, Integer>> getSaveToDbObservable(List<Article> data, int offset) {
        return mDbProviderFactory.getDbProvider().saveRatedArticlesList(data, offset);
    }
}