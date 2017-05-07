package ru.dante.scpfoundation.mvp.base;

import java.util.Collections;

import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.contract.DrawerMvp;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public abstract class BaseDrawerPresenter<V extends DrawerMvp.View>
        extends BaseActivityPresenter<V>
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
                            getView().onReceiveRandomUrl(url);
                        },
                        error -> {
                            getView().showProgressDialog(false);
                            getView().showError(error);
                        }
                );
    }

    @Override
    public void onNavigationItemClicked(int id) {
        Timber.d("onNavigationItemClicked: %s", id);
        //nothing to do
    }

    @Override
    public void onAvatarClicked() {
        Timber.d("onAvatarClicked");
        getView().showProgressDialog(R.string.progress_leaderboard);
        mApiClient.getLeaderboard()
//                .map(leaderBoardResponse -> {
//                    Collections.sort(leaderBoardResponse.users, (user1, user) -> user1.score - user.score);
//                    return leaderBoardResponse;
//                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        leaderBoardResponse -> {
                            Timber.d("getLeaderboard onNext: %s", leaderBoardResponse);
                            getView().dismissProgressDialog();
                            getView().showLeaderboard(leaderBoardResponse);
                        },
                        e -> {
                            Timber.e(e);
                            getView().dismissProgressDialog();
                            getView().showError(e);
                        }
                );
    }
}