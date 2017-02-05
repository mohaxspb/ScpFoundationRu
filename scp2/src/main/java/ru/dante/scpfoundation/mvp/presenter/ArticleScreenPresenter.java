package ru.dante.scpfoundation.mvp.presenter;

import java.util.List;

import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.base.BasePresenter;
import ru.dante.scpfoundation.mvp.contract.ArticleScreenMvp;
import timber.log.Timber;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class ArticleScreenPresenter
        extends BaseDrawerPresenter<ArticleScreenMvp.View>
        implements ArticleScreenMvp.Presenter {

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

    @Override
    protected void onReceiveUserFromDb() {
        super.onReceiveUserFromDb();
        getView().onGetUserFromDB(mUser);
    }
}