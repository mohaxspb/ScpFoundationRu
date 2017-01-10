package ru.kuchanov.scp2.mvp.presenter;

import android.util.Pair;

import java.util.List;

import io.realm.RealmResults;
import ru.kuchanov.scp2.api.ApiClient;
import ru.kuchanov.scp2.db.DbProviderFactory;
import ru.kuchanov.scp2.db.model.Article;
import ru.kuchanov.scp2.manager.MyPreferenceManager;
import ru.kuchanov.scp2.mvp.base.BaseArticlesListMvp;
import ru.kuchanov.scp2.mvp.base.BasePresenter;
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
public abstract class BaseListArticlesPresenter<V extends BaseArticlesListMvp.View>
        extends BasePresenter<V>
        implements BaseArticlesListMvp.Presenter<V> {

    protected RealmResults<Article> mData;

    public BaseListArticlesPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    public void onCreate() {
        Timber.d("onCreate");
    }

    @Override
    public void onDestroy() {
        Timber.d("onDestroy");
    }

    @Override
    public List<Article> getData() {
        return mData;
    }

    protected abstract Observable<RealmResults<Article>> getDbObservable();

    protected abstract Observable<List<Article>> getApiObservable(int offset);

    protected abstract Observable<Pair<Integer, Integer>> getSaveToDbObservable(List<Article> data, int offset);

    @Override
    public void getDataFromDb() {
        Timber.d("getDataFromDb");

        getView().showCenterProgress(true);
        getView().enableSwipeRefresh(false);

        getDbObservable()
                .subscribe(
                        data -> {
                            Timber.d("getDataFromDb data.size(): %s", data.size());
                            mData = data;
                            getView().updateData(mData);
//                            getView().showCenterProgress(false);
                            if (mData.isEmpty()) {
                                getView().enableSwipeRefresh(true);
                            } else {
                                getView().showCenterProgress(false);
                            }
                        },
                        error -> {
                            getView().showCenterProgress(false);
                            getView().enableSwipeRefresh(true);
                            getView().showError(error);
                        });
    }

    @Override
    public void getDataFromApi(int offset) {
        Timber.d("getDataFromApi with offset: %s", offset);
        if (mData != null && mData.isLoaded() && mData.isValid() && !mData.isEmpty()) {
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
                        });
    }

    @Override
    public void toggleFavoriteState(String url) {
        Timber.d("toggleFavoriteState: %s", url);
        mDbProviderFactory.getDbProvider().toggleFavorite(url)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getToggleFavoriteSubscriber());
    }

    @Override
    public void toggleReadenState(String url) {
        Timber.d("toggleReadenState: %s", url);
        mDbProviderFactory.getDbProvider().toggleReaden(url)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getToggleReadenSubscriber());
    }

    //TODO think if we need to manage state of loading during confChanges
    @Override
    public void toggleOfflineState(Article article) {
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
    public Subscriber<Pair<String, Long>> getToggleFavoriteSubscriber() {
        return new Subscriber<Pair<String, Long>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "error toggle favs state...");
            }

            @Override
            public void onNext(Pair<String, Long> stringBooleanPair) {
                Timber.d("favs state now is: %s", stringBooleanPair.second);
            }
        };
    }

    @Override
    public Subscriber<Pair<String, Boolean>> getToggleReadenSubscriber() {
        return new Subscriber<Pair<String, Boolean>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "error toggle readen state...");
            }

            @Override
            public void onNext(Pair<String, Boolean> stringBooleanPair) {
                Timber.d("read state now is: %s", stringBooleanPair.second);
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
                Timber.d("toggleOfflineState article: %s", article.url);
            }
        };
    }
}