package ru.dante.scpfoundation.mvp.base;

import android.os.Bundle;
import android.support.v4.util.Pair;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProvider;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.db.model.User;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.monetization.model.VkGroupToJoin;
import ru.dante.scpfoundation.mvp.contract.LoginActions;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
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
        //empty implementation
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

        @ScoreAction
        String action = article.isInReaden ? ScoreAction.READ :
                article.isInFavorite != Article.ORDER_NONE ? ScoreAction.FAVORITE : ScoreAction.NONE;

        int totalScoreToAdd = getTotalScoreToAddFromAction(action);

        //update score for articles, that is not in firebase, than write/update them
        mApiClient
                .getArticleFromFirebase(article)
                .flatMap(articleInFirebase -> articleInFirebase == null ?
                        mApiClient.incrementScoreInFirebaseObservable(totalScoreToAdd)
                                //score will be added to firebase user object
                                .flatMap(newTotalScore -> Observable.just(article))
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
    public void syncData(boolean showResultMessage) {
        Timber.d("syncData showResultMessage: %s", showResultMessage);
        //get unsynced articles from DB
        //write them to firebase
        //mark them as synced
        DbProvider dbProvider = mDbProviderFactory.getDbProvider();
        dbProvider.getUnsyncedArticlesManaged()
                .doOnNext(articles -> Timber.d("articles: %s", articles))
                .flatMap(articles -> articles.isEmpty() ? Observable.just(0) :
                        mApiClient.writeArticlesToFirebase(articles)
                                .flatMap(writeArticles -> mDbProviderFactory.getDbProvider().setArticlesSynced(writeArticles, true)))
                //also increment user score from unsynced score
                .flatMap(updatedArticles -> {
                    int unsyncedScore = mMyPreferencesManager.getNumOfUnsyncedScore();
                    if (unsyncedScore == 0) {
                        if (updatedArticles == 0) {
                            //no need to update something
                            return Observable.just(new Pair<>(updatedArticles, unsyncedScore));
                        } else {
                            //getScore from firebase and update it in Realm
                            return mApiClient.getUserScoreFromFirebase()
                                    .flatMap(firebaseUserScore -> mDbProviderFactory.getDbProvider().updateUserScore(firebaseUserScore))
                                    .flatMap(totalScore -> Observable.just(new Pair<>(updatedArticles, unsyncedScore)));
                        }
                    } else {
                        return mApiClient.incrementScoreInFirebaseObservable(unsyncedScore)
                                //update score in realm
                                .flatMap(newTotalScore -> mDbProviderFactory.getDbProvider().updateUserScore(newTotalScore))
                                .flatMap(newTotalScore -> Observable.just(new Pair<>(updatedArticles, unsyncedScore)));
                    }
                })
                //TODO add unsynced score for vkGroups and apps
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        data -> {
                            Timber.d("articles saved to firebase: %s", data);
                            if (showResultMessage) {
                                if (data.first == 0 && data.second == 0) {
                                    //TODO add plurals support
                                    getView().showMessage(R.string.all_data_already_synced);
                                } else {
                                    getView().showMessage(MyApplication.getAppInstance()
                                            .getString(R.string.all_data_sync_success, data.first, data.second));
                                }
                            }
                            dbProvider.close();
                            //we should set zero as unsynced score only in onSuccess callback,
                            //to not loose some score from broken connection
                            //reset unsynced score as we already sync it
                            mMyPreferencesManager.setNumOfUnsyncedScore(0);
                        },
                        e -> {
                            Timber.e(e);
                            if (showResultMessage) {
                                getView().showMessage(R.string.error_while_all_data_sync);
                            }
                            dbProvider.close();
                        }
                );
    }

    public void joinVkGroup(String id){
        Timber.d("joinVkGroup: %s", id);
        mApiClient
                .joinVkGroup(id)
                .subscribe(
                        result -> {
                            if (result) {
                                Timber.d("Successful group join");
                                mMyPreferenceManager.setVkGroupJoined(((VkGroupToJoin) data1).id);
                                mMyPreferenceManager.applyAwardVkGroupJoined();

                                long numOfMillis = FirebaseRemoteConfig.getInstance()
                                        .getLong(Constants.Firebase.RemoteConfigKeys.FREE_VK_GROUPS_JOIN_REWARD);
                                long hours = numOfMillis / 1000 / 60 / 60;

                                showNotificationSimple(getActivity(), getString(R.string.ads_reward_gained, hours), getString(R.string.thanks_for_supporting_us));

                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "group" + ((VkGroupToJoin) data1).id);
                                FirebaseAnalytics.getInstance(getActivity()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                            } else {
                                Timber.e("error group join");
                            }
                        },
                        error -> {
                            Timber.e(error, "error while join group");
                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                );

        int totalScoreToAdd = getTotalScoreToAddFromAction(ScoreAction.VK_GROUP);

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

            //increment unsynced score to sync it later
            mMyPreferencesManager.addUnsyncedVkGroup(id);
            return;
        }

        //increment scoreInFirebase
        mApiClient
                .isUserJoinedVkGroup(id)
                .flatMap(isUserJoinedVkGroup -> isUserJoinedVkGroup ?
                        Observable.empty() :
                        mApiClient.incrementScoreInFirebaseObservable(totalScoreToAdd)
                                .flatMap(newTotalScore -> mApiClient.addJoinedVkGroup(id).flatMap(aVoid -> Observable.just(newTotalScore)))
                )
                .subscribe(
                        newTotalScore -> Timber.d("new total score is: %s", newTotalScore),
                        e -> {
                            Timber.e(e, "error while increment userCore from action");
                            getView().showError(e);
                            //increment unsynced score to sync it later
                            mMyPreferencesManager.addUnsyncedScore(totalScoreToAdd);
                        }
                );
    }

    /**
     * check if user logged in,
     * calculate final score to add value from modificators,
     * if user do not have subscription we increment unsynced score
     * if user has subscription we increment score in firebase
     */
    @Override
    public void updateUserScoreForVkGroup(String id) {
        Timber.d("updateUserScore: %s", id);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Timber.d("user unlogined, do nothing");
            return;
        }

        int totalScoreToAdd = getTotalScoreToAddFromAction(ScoreAction.VK_GROUP);

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

            //increment unsynced score to sync it later
            mMyPreferencesManager.addUnsyncedVkGroup(id);
            return;
        }

        //increment scoreInFirebase
        mApiClient
                .isUserJoinedVkGroup(id)
                .flatMap(isUserJoinedVkGroup -> isUserJoinedVkGroup ?
                        Observable.empty() :
                        mApiClient.incrementScoreInFirebaseObservable(totalScoreToAdd)
                                .flatMap(newTotalScore -> mApiClient.addJoinedVkGroup(id).flatMap(aVoid -> Observable.just(newTotalScore)))
                )
                .subscribe(
                        newTotalScore -> Timber.d("new total score is: %s", newTotalScore),
                        e -> {
                            Timber.e(e, "error while increment userCore from action");
                            getView().showError(e);
                            //increment unsynced score to sync it later
                            mMyPreferencesManager.addUnsyncedScore(totalScoreToAdd);
                        }
                );
    }

    private int getTotalScoreToAddFromAction(@ScoreAction String action) {
        long score;

        //switch by action to get initial score value
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
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
            case ScoreAction.REWARDED_VIDEO:
                score = remoteConfig.getLong(Constants.Firebase.RemoteConfigKeys.SCORE_ACTION_REWARDED_VIDEO);
                break;
            case ScoreAction.VK_GROUP:
                score = remoteConfig.getLong(Constants.Firebase.RemoteConfigKeys.SCORE_ACTION_VK_GROUP);
                break;
            case ScoreAction.OUR_APP:
                score = remoteConfig.getLong(Constants.Firebase.RemoteConfigKeys.SCORE_ACTION_OUR_APP);
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
        return (int) (score * subscriptionModificator * vkGroupAppModificator);
    }
}