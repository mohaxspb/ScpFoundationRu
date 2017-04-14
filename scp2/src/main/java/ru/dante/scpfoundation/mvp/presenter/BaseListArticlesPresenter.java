package ru.dante.scpfoundation.mvp.presenter;

import android.util.Pair;

import java.util.List;

import io.realm.RealmResults;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProvider;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.base.BaseArticlesListMvp;
import ru.dante.scpfoundation.mvp.base.BasePresenter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
abstract class BaseListArticlesPresenter<V extends BaseArticlesListMvp.View>
        extends BasePresenter<V>
        implements BaseArticlesListMvp.Presenter<V> {

    protected RealmResults<Article> mData;

    BaseListArticlesPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    public List<Article> getData() {
        return mData;
    }

    protected abstract Observable<RealmResults<Article>> getDbObservable();

    protected abstract Observable<List<Article>> getApiObservable(int offset);

    protected abstract Observable<Pair<Integer, Integer>> getSaveToDbObservable(List<Article> data, int offset);

    DbProvider mDbProvider;

    @Override
    public void getDataFromDb() {
        Timber.d("getDataFromDb");

        getView().showCenterProgress(true);
        getView().enableSwipeRefresh(false);

        getDbObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        data -> {
                            //check if realm is closed and resubscribe, by calling getDataFromDb
                            if (!data.isValid()) {
                                Timber.e("data is not valid, so unsubscribe and restart observable");
                                mData = null;
                                getView().updateData(mData);
                                getDataFromDb();
                                return;
                            }
                            Timber.d("getDataFromDb data.size(): %s", data.size());
                            mData = data;
//                            getView().showCenterProgress(false);
                            if (mData.isEmpty()) {
                                getView().enableSwipeRefresh(true);
                            } else {
                                getView().showCenterProgress(false);
                                getView().updateData(mData);
                            }
                        },
                        e -> {
                            Timber.e(e);
                            getView().showCenterProgress(false);
                            getView().enableSwipeRefresh(true);
                            getView().showError(e);
                        }
                );

    }

    @Override
    public void getDataFromApi(int offset) {
        Timber.d("getDataFromApi with offset: %s", offset);
        if (mData != null && mData.isValid() && !mData.isEmpty()) {
            getView().showCenterProgress(false);
            if (offset != 0) {
                getView().enableSwipeRefresh(true);
                getView().showBottomProgress(true);
            } else {
                getView().enableSwipeRefresh(true);
                getView().showSwipeProgress(true);
            }
        } else {
            getView().showSwipeProgress(false);
            getView().showBottomProgress(false);
            getView().enableSwipeRefresh(false);

            getView().showCenterProgress(true);
        }

        if (mData != null && !mData.isValid()) {
            getDataFromDb();
        }

        getApiObservable(offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(apiDate -> getSaveToDbObservable(apiDate, offset))
                .subscribe(
                        data -> {
                            Timber.d("getDataFromApi load data size: %s and offset: %s", data.first, data.second);

                            getView().enableSwipeRefresh(true);
                            getView().showSwipeProgress(false);
                            getView().showBottomProgress(false);
                            getView().showCenterProgress(false);
                        }
                        , error -> {
                            Timber.e(error);
                            getView().showError(error);

                            getView().enableSwipeRefresh(true);
                            getView().showSwipeProgress(false);
                            getView().showBottomProgress(false);
                            getView().showCenterProgress(false);
                            //also we need to reset onScrollListener
                            //if there is data, because we can receive error
                            //while download with error
                            //in this case we do not call View#updateData,
                            //which calls resetOnScrollListener
                            if (mData != null && !mData.isEmpty()) {
                                getView().resetOnScrollListener();
                            }
                        }
                );
    }

    @Override
    public void toggleFavoriteState(Article article) {
        if (!article.isValid()) {
            return;
        }
        Timber.d("toggleFavoriteState: %s", article);
        mDbProviderFactory.getDbProvider().toggleFavorite(article.url)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(article1 -> mDbProviderFactory.getDbProvider().setArticleSynced(article1, false))
                .subscribe(getToggleFavoriteSubscriber());
    }

    @Override
    public void toggleReadState(Article article) {
        if (!article.isValid()) {
            return;
        }
        Timber.d("toggleReadState: %s", article);
        mDbProviderFactory.getDbProvider().toggleReaden(article.url)
                .flatMap(articleUrl -> mDbProviderFactory.getDbProvider().getUnmanagedArticleAsyncOnes(articleUrl))
                .flatMap(article1 -> mDbProviderFactory.getDbProvider().setArticleSynced(article1, false))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getToggleReadSubscriber());
    }

    //TODO think if we need to manage state of loading during confChanges
    @Override
    public void toggleOfflineState(Article article) {
        if (!article.isValid()) {
            return;
        }
        Timber.d("toggleOfflineState: %s", article.url);
        if (article.text == null) {
            mApiClient.getArticle(article.url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(apiData -> mDbProviderFactory.getDbProvider().saveArticle(apiData))
                    .subscribe(getDownloadArticleSubscriber());
        } else {
            mDbProviderFactory.getDbProvider().deleteArticlesText(article.url)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getDeleteArticlesTextSubscriber());
        }
    }

    @Override
    public void toggleOfflineState(String url) {
        Timber.d("toggleOfflineState: %s", url);
        Article article = new Article();
        article.url = url;
        toggleOfflineState(article);
    }

    @Override
    public Subscriber<Article> getToggleFavoriteSubscriber() {
        return new Subscriber<Article>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "error toggle favs state...");
            }

            @Override
            public void onNext(Article article) {
                Timber.d("favs state now is: %s", article.isInFavorite != Article.ORDER_NONE);
                updateArticleInFirebase(article, true);
//                updateUserScoreFromAction(1);
            }
        };
    }

    @Override
    public Subscriber<Article> getToggleReadSubscriber() {
        return new Subscriber<Article>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "error toggle read state...");
            }

            @Override
            public void onNext(Article article) {
                Timber.d("read state now is: %s", article.isInFavorite != Article.ORDER_NONE);
                updateArticleInFirebase(article, false);
            }
        };
    }

    @Override
    public Subscriber<String> getDeleteArticlesTextSubscriber() {
        return new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "error delete text...");
            }

            @Override
            public void onNext(String stringBooleanPair) {
                Timber.d("deleted");
            }
        };
    }

    @Override
    public Subscriber<Article> getDownloadArticleSubscriber() {
        return new Subscriber<Article>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "error download article");
                getView().showError(e);
            }

            @Override
            public void onNext(Article article) {
                Timber.d("getDownloadArticleSubscriber onNext article: %s", article.url);
            }
        };
    }
}