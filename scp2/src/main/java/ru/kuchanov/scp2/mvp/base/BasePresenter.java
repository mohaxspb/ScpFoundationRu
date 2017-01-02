package ru.kuchanov.scp2.mvp.base;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

import ru.kuchanov.scp2.api.ApiClient;
import ru.kuchanov.scp2.db.DbProviderFactory;
import ru.kuchanov.scp2.manager.MyPreferenceManager;

/**
 * Created by y.kuchanov on 21.12.16.
 *
 * for scp_ru
 */
public abstract class BasePresenter<V extends MvpView> extends MvpNullObjectBasePresenter<V> {

    protected static final int ZERO_OFFSET = 0;

    protected MyPreferenceManager mMyPreferencesManager;
    protected DbProviderFactory mDbProviderFactory;
    protected ApiClient mApiClient;

    public BasePresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        mMyPreferencesManager = myPreferencesManager;
        mDbProviderFactory = dbProviderFactory;
        mApiClient = apiClient;
    }
}