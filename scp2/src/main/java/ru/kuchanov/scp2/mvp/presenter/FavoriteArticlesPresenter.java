package ru.kuchanov.scp2.mvp.presenter;

import android.util.Pair;

import java.util.List;

import io.realm.RealmResults;
import io.realm.Sort;
import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.api.ApiClient;
import ru.kuchanov.scp2.db.DbProviderFactory;
import ru.kuchanov.scp2.db.model.Article;
import ru.kuchanov.scp2.manager.MyPreferenceManager;
import ru.kuchanov.scp2.mvp.contract.FavoriteArticles;
import rx.Observable;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class FavoriteArticlesPresenter
        extends BaseListArticlesPresenter<FavoriteArticles.View>
        implements FavoriteArticles.Presenter {

    public FavoriteArticlesPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    protected Observable<RealmResults<Article>> getDbObservable() {
        return mDbProviderFactory.getDbProvider().getFavoriteArticlesSortedAsync(Article.FIELD_IS_IN_FAVORITE, Sort.DESCENDING);
    }

    @Override
    protected Observable<List<Article>> getApiObservable(int offset) {
        throw new IllegalStateException(MyApplication.getAppInstance().getString(R.string.not_implemented));
    }

    @Override
    protected Observable<Pair<Integer, Integer>> getSaveToDbObservable(List<Article> data, int offset) {
        throw new IllegalStateException(MyApplication.getAppInstance().getString(R.string.not_implemented));
    }
}