package ru.dante.scpfoundation.mvp.base;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import java.util.List;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.db.model.User;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.contract.LoginActions;
import rx.Observable;
import timber.log.Timber;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for scp_ru
 */
public abstract class BasePresenter<V extends BaseMvp.View>
        extends MvpNullObjectBasePresenter<V>
        implements BaseMvp.Presenter<V> {

    MyPreferenceManager mMyPreferencesManager;
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
    public void updateArticleInFirebase(Article article, boolean showResultMessage) {
        Timber.d("updateArticleInFirebase: %s", article.url);
        if (!mMyPreferencesManager.isHasSubscription()) {
            long curNumOfAttempts = mMyPreferencesManager.getNumOfAttemptsToAutoSync();
            long maxNumOfAttempts = FirebaseRemoteConfig.getInstance()
                    .getLong(Constants.Firebase.RemoteConfigKeys.NUM_OF_SYNC_ATTEMPTS_BEFORE_CALL_TO_ACTION);

            Timber.d("does not have subscription, so no auto sync: %s/%s", curNumOfAttempts, maxNumOfAttempts);

            if (curNumOfAttempts >= maxNumOfAttempts) {
                //show call to action
                mMyPreferencesManager.setNumOfAttemptsToAutoSync(0);
                getView().showSnackBarWithAction(Constants.Firebase.CallToActionReason.ENABLE_AUTO_SYNC);
            } else {
                mMyPreferencesManager.setNumOfAttemptsToAutoSync(curNumOfAttempts + 1);
            }
            return;
        }

        //TODO check if user has subscription and if not just mark article as need to sync score
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

        long score = 1;

        @ScoreAction
        String action = article.isInReaden ? ScoreAction.READ :
                article.isInFavorite != Article.ORDER_NONE ? ScoreAction.FAVORITE : ScoreAction.NONE;

        //switch by action to get initial score value
        switch (action) {
            case ScoreAction.FAVORITE:
                score = remoteConfig.getLong(Constants.Firebase.RemoteConfigKeys.SCORE_ACTION_FAVORITE);
                break;
            case ScoreAction.READ:
                score = remoteConfig.getLong(Constants.Firebase.RemoteConfigKeys.SCORE_ACTION_READ);
                break;
            case ScoreAction.INTERSTITIAL_SHOWN:
                score = remoteConfig.getLong(Constants.Firebase.RemoteConfigKeys.SCORE_ACTION_INTERSTITIAL_SHOWN);
                break;
            case ScoreAction.VK_GROUP:
                score = remoteConfig.getLong(Constants.Firebase.RemoteConfigKeys.SCORE_ACTION_VK_GROUP);
                break;
            case ScoreAction.OUR_APP:
                score = remoteConfig.getLong(Constants.Firebase.RemoteConfigKeys.SCORE_ACTION_OUR_APP);
                break;
            case ScoreAction.REWARDED_VIDEO:
                score = remoteConfig.getLong(Constants.Firebase.RemoteConfigKeys.SCORE_ACTION_REWARDED_VIDEO);
                break;
            case ScoreAction.NONE:
                score = remoteConfig.getLong(Constants.Firebase.RemoteConfigKeys.SCORE_ACTION_NONE);
                break;
            default:
                throw new RuntimeException("unexpected score action");
        }

        double subscriptionModificator = remoteConfig.getDouble(Constants.Firebase.RemoteConfigKeys.SCORE_MULTIPLIER_SUBSCRIPTION);
        double vkGroupAppModificator = remoteConfig.getDouble(Constants.Firebase.RemoteConfigKeys.SCORE_MULTIPLIER_VK_GROUP_APP);

        boolean hasSubscriptionModificator = mMyPreferencesManager.isHasSubscription();
        boolean hasVkGroupAppModificator = mMyPreferencesManager.isVkGroupAppJoined();

        subscriptionModificator = hasSubscriptionModificator ? subscriptionModificator : 1;
        vkGroupAppModificator = hasVkGroupAppModificator ? vkGroupAppModificator : 1;
        //check if user has subs and joined vk group to add multilplier
        int totalScoreToAdd = (int) (score * subscriptionModificator * vkGroupAppModificator);

        mApiClient.getArticleFromFirebase(article)
                .flatMap(articleInFirebase -> articleInFirebase == null ?
                        mApiClient.updateScoreInFirebaseObservable(totalScoreToAdd)
                                //score will be added to firebase user object
                                .flatMap(addedScore -> Observable.just(article))
                        : Observable.just(article))
                .flatMap(article1 -> mApiClient.writeArticleToFirebase(article1))
                .flatMap(article1 -> mDbProviderFactory.getDbProvider().setArticleSynced(article1, true))
                .subscribe(
                        article1 -> {
                            Timber.d("sync article onComplete: %s", article1.url);
                            //show only for favorites
                            if (showResultMessage) {
                                getView().showMessage(R.string.sync_fav_success);
                            }
                        },
                        e -> {
                            Timber.e(e);
                            if (showResultMessage) {
                                getView().showError(new Throwable(MyApplication.getAppInstance().getString(R.string.error_while_sync)));
                            }
                        }
                );
    }

    @Override
    public void syncArticles(boolean showResultMessage) {
        Timber.d("syncArticles showResultMessage: %s", showResultMessage);
        //get unsynced articles from DB
        //write them to firebase
        //mark them as synced
        //TODO use managed ones
        Observable.<List<Article>>create(subscriber -> mDbProviderFactory.getDbProvider().getUnsynedArticlesUnmanaged()
                .subscribe(
                        data -> {
                            subscriber.onNext(data);
                            subscriber.onCompleted();
                        },
                        subscriber::onError
                ))
                .flatMap(articles -> articles.isEmpty() ? Observable.just(articles) :
                        mApiClient.writeArticlesToFirebase(articles)
                                .flatMap(writeArticles -> mDbProviderFactory.getDbProvider().setArticlesSynced(writeArticles, true)))
                .subscribe(
                        data -> {
                            Timber.d("articles saved to firebase: %s", data);
                            if (showResultMessage) {
                                if (data.isEmpty()) {
                                    getView().showMessage(R.string.all_data_already_synced);
                                } else {
                                    getView().showMessage(R.string.all_data_sync_success);
                                }
                            }
                        },
                        e -> {
                            Timber.e(e);
                            if (showResultMessage) {
                                getView().showMessage(R.string.error_while_all_data_sync);
                            }
                        }
                );
    }

    /**
     * check if user loged in,
     * calculate final score to add value from modificators,
     * //TODO check if this article was not rewarded before (may be we can mark article as requested rewar and try to add this core later, when user manually sync data)
     * write score to realm,
     * write it to firebase if user has subscription
     */
    @Override
    public void updateUserScoreFromAction(@ScoreAction String action) {
//        Timber.d("updateUserScore: %s", action);
//
//        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
//            Timber.d("user unlogined, do nothing");
//            return;
//        }

//        mDbProviderFactory.getDbProvider().incrementUserScore(totalScoreToAdd)
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .observeOn(Schedulers.io())
//                //TODO test
//                .flatMap(user ->/* mMyPreferencesManager.isHasSubscription()*/true ? mApiClient.updateScoreInFirebaseObservable(user.score) : Observable.empty())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        newTotalScore -> Timber.d("score updated in firebase"),
//                        Timber::e,
//                        () -> Timber.d("onCompleted")
//                );
    }
}