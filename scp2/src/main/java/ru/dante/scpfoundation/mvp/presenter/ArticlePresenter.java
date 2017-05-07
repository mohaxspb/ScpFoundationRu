package ru.dante.scpfoundation.mvp.presenter;

import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.base.BasePresenter;
import ru.dante.scpfoundation.mvp.contract.ArticleMvp;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class ArticlePresenter
        extends BasePresenter<ArticleMvp.View>
        implements ArticleMvp.Presenter {

    /**
     * used as Article obj id
     */
    private String mArticleUrl;
    private Article mData;

    public ArticlePresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    public void setArticleId(String url) {
        Timber.d("setArticleId: %s", url);
        mArticleUrl = url;
    }

    @Override
    public Article getData() {
        return mData;
    }

    @Override
    public void getDataFromDb() {
        Timber.d("getDataFromDb");

        getView().showCenterProgress(true);
        getView().enableSwipeRefresh(false);

        mDbProviderFactory.getDbProvider().getUnmanagedArticleAsync(mArticleUrl).subscribe(
                data -> {
                    Timber.d("getDataFromDb data: %s", data);
                    mData = data;
                    if (mData == null) {
                        getDataFromApi();
                    } else if (mData.text == null) {
                        getView().showData(mData);
                        getDataFromApi();
                    } else {
                        getView().showData(mData);
                        getView().showCenterProgress(false);
                        getView().enableSwipeRefresh(true);
                    }
                },
                error -> {
                    getView().showCenterProgress(false);
                    getView().enableSwipeRefresh(true);
                    getView().showError(error);
                }
        );
    }

    @Override
    public void getDataFromApi() {
        Timber.d("getDataFromApi: %s", mArticleUrl);
        mApiClient.getArticle(mArticleUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(apiData -> mDbProviderFactory.getDbProvider().saveArticle(apiData))
                .subscribe(
                        data -> {
                            Timber.d("getDataFromApi onNext");

                            getView().showCenterProgress(false);
                            getView().enableSwipeRefresh(true);
                            getView().showSwipeProgress(false);
                        },
                        error -> {
                            Timber.e(error);
                            getView().showError(error);

                            getView().showCenterProgress(false);
                            getView().enableSwipeRefresh(true);
                            getView().showSwipeProgress(false);
                        }
                );
    }

    @Override
    public void setArticleIsReaden(String url) {
        Timber.d("setArticleIsReaden url: %s", url);
        mDbProviderFactory.getDbProvider()
                .toggleReaden(url)
                .flatMap(articleUrl -> mDbProviderFactory.getDbProvider().getUnmanagedArticleAsyncOnes(articleUrl))
                .flatMap(article1 -> mDbProviderFactory.getDbProvider().setArticleSynced(article1, false))
                .subscribe(
                        article -> {
                            Timber.d("read state now is: %s", article.isInReaden);
                            updateArticleInFirebase(article, false);
                        },
                        Timber::e
                );
    }
}