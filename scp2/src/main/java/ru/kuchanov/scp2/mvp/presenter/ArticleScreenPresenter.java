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
     */
    private List<String> mUrls;

    public ArticleScreenPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    public void onCreate() {
        Timber.d("onCreate");
        //TODO
    }

    @Override
    public void onDestroy() {
        //TODO
    }

    @Override
    public void onNavigationItemClicked(int id) {
        //TODO
    }

    @Override
    public void setArticlesUrls(List<String> urls) {
        mUrls = urls;
    }
}