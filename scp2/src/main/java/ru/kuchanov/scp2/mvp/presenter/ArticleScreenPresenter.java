package ru.kuchanov.scp2.mvp.presenter;

import java.util.List;

import ru.kuchanov.scp2.api.ApiClient;
import ru.kuchanov.scp2.db.DbProviderFactory;
import ru.kuchanov.scp2.manager.MyPreferenceManager;
import ru.kuchanov.scp2.mvp.base.BasePresenter;
import ru.kuchanov.scp2.mvp.contract.ArticleScreenMvp;
import timber.log.Timber;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class ArticleScreenPresenter extends BasePresenter<ArticleScreenMvp.View> implements ArticleScreenMvp.Presenter {

    /**
     * urls (aka IDs) of arts
     * TODO may be we do not need it...
     */
    private List<String> mUrls;

    public ArticleScreenPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    public void onCreate() {
//        Timber.d("onCreate");
        //nothing to do
    }

    @Override
    public void onDestroy() {
        //nothing to do
    }

    @Override
    public void onNavigationItemClicked(int id) {
        //nothing to do
    }

    @Override
    public void setArticlesUrls(List<String> urls) {
        mUrls = urls;
    }

    @Override
    public void toggleFavorite(String url) {
        Timber.d("toggleFavorite url: %s", url);
        mDbProviderFactory.getDbProvider().toggleFavorite(url)
                .subscribe(
                        resultState -> Timber.d("fav state now is: %b", resultState),
                        Timber::e
                );
    }
}