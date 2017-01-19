package ru.dante.scpfoundation.mvp.presenter;

import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.base.BasePresenter;
import ru.dante.scpfoundation.mvp.base.DrawerMvp;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public abstract class BaseDrawerPresenter<V extends DrawerMvp.View>
        extends BasePresenter<V>
        implements DrawerMvp.Presenter<V> {

    public BaseDrawerPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    public void getRandomArticleUrl() {
        getView().showProgressDialog(true);
        Timber.d("getRandomArticle");
        mApiClient.getRandomUrl()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        url -> {
                            getView().showProgressDialog(false);
                            getView().startArticleActivity(url);
                        },
                        error -> {
                            getView().showProgressDialog(false);
                            getView().showError(error);
                        }
                );
    }
}