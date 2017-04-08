package ru.dante.scpfoundation.mvp.presenter;

import android.util.Pair;

import java.util.List;

import io.realm.RealmResults;
import io.realm.Sort;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.contract.OfflineArticles;
import rx.Observable;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class OfflineArticlesPresenter
        extends BaseListArticlesPresenter<OfflineArticles.View>
        implements OfflineArticles.Presenter {

    public OfflineArticlesPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    protected Observable<RealmResults<Article>> getDbObservable() {
        return mDbProviderFactory.getDbProvider().getOfflineArticlesSortedAsync(Article.FIELD_LOCAL_UPDATE_TIME_STAMP, Sort.DESCENDING);
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