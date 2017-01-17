package ru.dante.scpfoundation.mvp.base;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for scp_ru
 */
public abstract class BasePresenter<V extends BaseMvp.View>
        extends MvpNullObjectBasePresenter<V> {

    protected MyPreferenceManager mMyPreferencesManager;
    protected DbProviderFactory mDbProviderFactory;
    protected ApiClient mApiClient;

    public BasePresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        mMyPreferencesManager = myPreferencesManager;
        mDbProviderFactory = dbProviderFactory;
        mApiClient = apiClient;
    }
}