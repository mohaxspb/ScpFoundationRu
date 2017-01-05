package ru.kuchanov.scp2.mvp.presenter;

import ru.kuchanov.scp2.api.ApiClient;
import ru.kuchanov.scp2.db.DbProviderFactory;
import ru.kuchanov.scp2.db.model.Article;
import ru.kuchanov.scp2.manager.MyPreferenceManager;
import ru.kuchanov.scp2.mvp.base.BasePresenter;
import ru.kuchanov.scp2.mvp.contract.ArticleMvp;
import timber.log.Timber;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class ArticlePresenter extends BasePresenter<ArticleMvp.View> implements ArticleMvp.Presenter {

    /**
     * used as Article obj id
     */
    private String mArticleUrl;
    private Article mData;

    public ArticlePresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
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
    public void setArticleId(String url) {
        Timber.d("setArticleId: %s", url);
        mArticleUrl = url;
    }

    @Override
    public Article getData() {
        //TODO
        return mData;
    }

    @Override
    public void getDataFromDb() {
        //TODO

    }

    @Override
    public void getDataFromApi() {
        //TODO

    }
}