package ru.dante.scpfoundation.mvp.presenter;

import android.text.TextUtils;

import com.vk.sdk.VKAccessToken;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.base.BasePresenter;
import ru.dante.scpfoundation.mvp.base.DrawerMvp;
import rx.Observable;
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