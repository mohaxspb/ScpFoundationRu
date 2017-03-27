package ru.dante.scpfoundation.mvp.base;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.db.model.User;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.contract.LoginActions;
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

    private User mUser;

    public BasePresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        mMyPreferencesManager = myPreferencesManager;
        mDbProviderFactory = dbProviderFactory;
        mApiClient = apiClient;

        getUserFromDb();
    }

    @Override
    public void onCreate() {
        Timber.d("onCreate");
    }

    @Override
    public void getUserFromDb() {
        mDbProviderFactory.getDbProvider().getUserAsync().subscribe(
                user -> {
                    mUser = user;
                    if (getView() instanceof LoginActions.View) {
                        ((LoginActions.View) getView()).updateUser(mUser);
                    }
                    onUserChanged(mUser);
                },
                error -> Timber.e(error, "error while get user from DB")
        );
    }

    @Override
    public void onUserChanged(User user) {
        //empty implemetation
    }

    @Override
    public User getUser() {
        return mUser;
    }

    @Override
    public void updateArticleInFirebase(Article article) {
        Timber.d("updateArticleInFirebase: %s", article.url);
        mApiClient.writeArticleToFirebase(article).subscribe(
                article1 -> {
                    Timber.d("sync fav onComplete: %s", MyApplication.getAppInstance().getString(R.string.sync_fav_success));
                    getView().showMessage(R.string.sync_fav_success);
                },
                e -> {
                    Timber.e(e);
                    getView().showError(new Throwable(MyApplication.getAppInstance().getString(R.string.error_while_sync_fav)));
                }
        );
    }
}