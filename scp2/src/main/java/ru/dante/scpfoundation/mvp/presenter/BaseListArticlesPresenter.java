package ru.dante.scpfoundation.mvp.presenter;

import android.util.Pair;

import java.util.List;

import io.realm.RealmResults;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.base.BaseArticlesListMvp;
import ru.dante.scpfoundation.mvp.base.BasePresenter;
import rx.Observable;
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
                            //also we need to reset onScrollListener
                            //if there is data, because we can receive error
                            //while download with error
                            //in this case we do not call View#updateData,
                            //which calls resetOnScrollListener
                            if (mData != null && !mData.isEmpty()) {
                                getView().resetOnScrollListener();
                            }
                        });
    }

    @Override
    public void toggleFavoriteState(String url) {
        Timber.d("toggleFavoriteState: %s", url);
        mDbProviderFactory.getDbProvider().toggleFavorite(url)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        stringLongPair -> {
                            Timber.d("favs state now is: %s", stringLongPair.second);
                            //TODO test
                            syncFavorite(stringLongPair.first, stringLongPair.second != Article.ORDER_NONE);
                        },
                        e -> Timber.e(e, "error toggle favs state...")
                );
    }

    @Override
    public void toggleReadenState(String url) {
        Timber.d("toggleReadenState: %s", url);
        mDbProviderFactory.getDbProvider().toggleReaden(url)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        stringBooleanPair -> Timber.d("read state now is: %s", stringBooleanPair.second),
                        e -> Timber.e(e, "error toggle readen state...")
                );
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
                    .subscribe(
                            article1 -> Timber.d("toggleOfflineState article: %s", article.url),
                            e -> {
                                Timber.e(e, "error download article");
                                getView().showError(e);
                            }
                    );
        } else {
            mDbProviderFactory.getDbProvider().deleteArticlesText(article.url)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            s -> Timber.d("articles text deleted"),
                            e -> Timber.e(e, "error delete articles text...")
                    );
        }
    }

    @Override
    public void toggleOfflineState(String url) {
        Timber.d("toggleOfflineState: %s", url);
        Article article = new Article();
        article.url = url;
        toggleOfflineState(article);
    }
}