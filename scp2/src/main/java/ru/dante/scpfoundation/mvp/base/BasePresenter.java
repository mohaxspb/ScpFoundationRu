package ru.dante.scpfoundation.mvp.base;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.User;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import timber.log.Timber;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for scp_ru
 */
public abstract class BasePresenter<V extends BaseMvp.View>
        extends MvpNullObjectBasePresenter<V>
        implements BaseMvp.Presenter<V> {

    protected MyPreferenceManager mMyPreferencesManager;
    protected DbProviderFactory mDbProviderFactory;
    protected ApiClient mApiClient;

    protected User mUser;

    public BasePresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        mMyPreferencesManager = myPreferencesManager;
        mDbProviderFactory = dbProviderFactory;
        mApiClient = apiClient;

        mDbProviderFactory.getDbProvider().getUserAsync()
                .subscribe(
                        user -> {
                            mUser = user;
                            onReceiveUserFromDb();
                        },
                        error -> Timber.e(error, "error while get user from DB")
                );
    }

    /**
     * override it to do something on getting user from DB
     * i.e. update drawers header via calling View methods
     */
    protected void onReceiveUserFromDb() {
        //empty implementation
    }

    @Override
    public void onUserLogined(User user) {
        Timber.d("onUserLogined: %s", user);

        if (user != null) {
            mDbProviderFactory.getDbProvider().saveUser(user)
                    .subscribe(
                            result -> Timber.d("user saved"),
                            error -> Timber.e(error, "error while save user to DB")
                    );
        } else {
            mDbProviderFactory.getDbProvider().deleteUser()
                    .subscribe(
                            result -> Timber.d("user deleted"),
                            error -> Timber.e(error, "error while delete user from DB")
                    );
        }
    }

    @Override
    public User getUser() {
        return mUser;
    }
}