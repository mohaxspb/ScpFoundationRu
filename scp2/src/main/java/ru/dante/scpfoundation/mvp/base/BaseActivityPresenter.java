package ru.dante.scpfoundation.mvp.base;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.vk.sdk.VKSdk;

import java.util.ArrayList;
import java.util.Map;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.api.error.ScpLoginException;
import ru.dante.scpfoundation.api.model.firebase.ArticleInFirebase;
import ru.dante.scpfoundation.api.model.firebase.FirebaseObjectUser;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.SocialProviderModel;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Created by mohax on 23.03.2017.
 * <p>
 * for scp_ru
 */
abstract class BaseActivityPresenter<V extends BaseActivityMvp.View>
        extends BasePresenter<V>
        implements BaseActivityMvp.Presenter<V> {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener = mAuth -> {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            // User is signed in
            Timber.d("onAuthStateChanged:signed_in: %s", firebaseUser.getUid());
            listenToChangesInFirebase(mMyPreferencesManager.isHasSubscription());
        } else {
            // User is signed out
            Timber.d("onAuthStateChanged: signed_out");

            listenToChangesInFirebase(false);

            if (VKSdk.isLoggedIn()) {
                //this can be after update to 1.1.x.x+ (have vk login and do not have firebase one)
                //so we can show popup with info and let user relogin
                if (mMyPreferencesManager.isTimeToShowNeedReloginPopup()) {
                    mMyPreferencesManager.setLastTimeNeedReloginPopupShown(System.currentTimeMillis());
                    getView().showNeedReloginPopup();
                }
            }
        }
    };

    private DatabaseReference mFirebaseArticlesRef;
    private DatabaseReference mFirebaseScoreRef;

    private ValueEventListener articlesChangeListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Timber.d("articles in user changed!");
            GenericTypeIndicator<Map<String, ArticleInFirebase>> t = new GenericTypeIndicator<Map<String, ArticleInFirebase>>() {
            };
            Map<String, ArticleInFirebase> map = dataSnapshot.getValue(t);

            if (map != null) {
                mDbProviderFactory.getDbProvider()
                        .saveArticlesFromFirebase(new ArrayList<>(map.values()))
                        .subscribe(
                                result -> Timber.d("articles in realm updated!"),
                                Timber::e
                        );
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.e(databaseError.toException());
        }
    };

    private ValueEventListener scoreChangeListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Timber.d("score in user changed!");
            Integer score = dataSnapshot.getValue(Integer.class);

            if (score != null) {
                mDbProviderFactory.getDbProvider()
                        .updateUserScore(score)
                        .subscribe(
                                result -> Timber.d("score in realm updated!"),
                                Timber::e
                        );
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Timber.e(databaseError.toException());
        }
    };

    BaseActivityPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);

        getUserFromDb();
    }

    /**
     * @param provider login provider to use to login to firebase
     */
    @Override
    public void startFirebaseLogin(Constants.Firebase.SocialProvider provider) {
        getView().showProgressDialog(R.string.login_in_progress_custom_token);
        mApiClient.getAuthInFirebaseWithSocialProviderObservable(provider)
                .flatMap(firebaseUser -> {
                    if (TextUtils.isEmpty(firebaseUser.getEmail())) {
                        return Observable.create(subscriber -> mApiClient.nameAndAvatarFromProviderObservable(provider)
                                .flatMap(nameAvatar -> mApiClient.updateFirebaseUsersNameAndAvatarObservable(nameAvatar.first, nameAvatar.second))
                                .flatMap(aVoid -> mApiClient.updateFirebaseUsersEmailObservable())
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        result -> {
                                            Timber.d("result");
                                            subscriber.onNext(FirebaseAuth.getInstance().getCurrentUser());
                                            subscriber.onCompleted();
                                        },
                                        Observable::error
                                ));
                    } else {
                        return Observable.just(firebaseUser);
                    }
                })
                .doOnNext(firebaseUser -> Timber.d(
                        "firebaseUser: %s, %s, %s, %s",
                        firebaseUser.getUid(),
                        firebaseUser.getEmail(),
                        firebaseUser.getPhotoUrl(),
                        firebaseUser.getDisplayName()
                ))
                .flatMap(firebaseUser -> mApiClient.getUserObjectFromFirebaseObservable())
                .flatMap(userObjectInFirebase -> {
                    if (userObjectInFirebase == null) {
                        Timber.d("there is no User object in firebase database, so create new one");
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (firebaseUser == null) {
                            return Observable.error(new ScpLoginException(
                                    MyApplication.getAppInstance()
                                            .getString(R.string.error_login_firebase_connection,
                                                    "firebase user is null")));
                        }
                        FirebaseObjectUser userToWriteToDb = new FirebaseObjectUser();
                        userToWriteToDb.uid = firebaseUser.getUid();
                        userToWriteToDb.fullName = firebaseUser.getDisplayName();
                        if (firebaseUser.getPhotoUrl() != null) {
                            userToWriteToDb.avatar = firebaseUser.getPhotoUrl().toString();
                        }
                        userToWriteToDb.email = firebaseUser.getEmail();
                        userToWriteToDb.socialProviders = new ArrayList<>();
                        userToWriteToDb.socialProviders.add(SocialProviderModel.getSocialProviderModelForProvider(provider));
                        //userToWriteToDb.socialProviders.put(provider.name(), SocialProviderModel.getSocialProviderModelForProvider(provider));
                        return mApiClient.writeUserToFirebaseObservable(userToWriteToDb);
                    } else {
                        return Observable.just(userObjectInFirebase);
                    }
                })
                //save user articles to realm
                .flatMap(userObjectInFirebase -> userObjectInFirebase.articles == null ?
                        Observable.just(userObjectInFirebase) :
                        mDbProviderFactory.getDbProvider()
                                .saveArticlesFromFirebase(new ArrayList<>(userObjectInFirebase.articles.values()))
                                .flatMap(articles -> Observable.just(userObjectInFirebase))
                )
                //save user to realm
                .flatMap(userObjectInFirebase -> mDbProviderFactory.getDbProvider().saveUser(userObjectInFirebase.toRealmUser()))
                .subscribe(
                        userInRealm -> {
                            Timber.d("user saved");
                            getView().dismissProgressDialog();
                            getView().showMessage(MyApplication.getAppInstance()
                                    .getString(R.string.on_user_logined,
                                            userInRealm.fullName));
                        },
                        e -> {
                            Timber.e(e, "error while save user to DB");
                            logoutUser();
                            getView().dismissProgressDialog();
                            getView().showError(new ScpLoginException(
                                    MyApplication.getAppInstance()
                                            .getString(R.string.error_login_firebase_connection,
                                                    e.getMessage())));
                        }
                );
    }

    @Override
    public void logoutUser() {
        Timber.d("logoutUser");
        mDbProviderFactory.getDbProvider().logout().subscribe(
                result -> Timber.d("logout successful"),
                error -> Timber.e(error, "error while logout user")
        );
    }

    @Override
    public void onActivityStarted() {
        mAuth.addAuthStateListener(mAuthListener);

        listenToChangesInFirebase(mMyPreferencesManager.isHasSubscription());
    }

    @Override
    public void onActivityStopped() {
        mAuth.removeAuthStateListener(mAuthListener);

        listenToChangesInFirebase(false);
    }

    private void listenToChangesInFirebase(boolean listen) {
        Timber.d("listenToChangesInFirebase: %s", listen);
        if (listen) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null && !TextUtils.isEmpty(firebaseUser.getUid())) {
                if (mFirebaseArticlesRef != null) {
                    mFirebaseArticlesRef.removeEventListener(articlesChangeListener);
                }
                mFirebaseArticlesRef = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.Firebase.Refs.USERS)
                        .child(firebaseUser.getUid())
                        .child(Constants.Firebase.Refs.ARTICLES);

                mFirebaseArticlesRef.addValueEventListener(articlesChangeListener);

                if (mFirebaseScoreRef != null) {
                    mFirebaseScoreRef.removeEventListener(scoreChangeListener);
                }
                mFirebaseScoreRef = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.Firebase.Refs.USERS)
                        .child(firebaseUser.getUid())
                        .child(Constants.Firebase.Refs.SCORE);

                mFirebaseScoreRef.addValueEventListener(scoreChangeListener);
            } else {
                if (mFirebaseArticlesRef != null) {
                    mFirebaseArticlesRef.removeEventListener(articlesChangeListener);
                }
                if (mFirebaseScoreRef != null) {
                    mFirebaseScoreRef.removeEventListener(scoreChangeListener);
                }
            }
        } else {
            if (mFirebaseArticlesRef != null) {
                mFirebaseArticlesRef.removeEventListener(articlesChangeListener);
            }
            if (mFirebaseScoreRef != null) {
                mFirebaseScoreRef.removeEventListener(scoreChangeListener);
            }
        }
    }

    @Override
    public void reactOnCrackEvent() {
        Timber.d("reactOnCrackEvent");
        mApiClient.setCrackedInFirebase()
                .onErrorResumeNext(e -> Observable.just(null))
                .flatMap(aVoid -> mDbProviderFactory.getDbProvider().updateUserScore(0))
                .subscribe(
                        newTotalScore -> {
                            Timber.d("reactOnCrackEvent onNext");
                            getView().dismissProgressDialog();
                        },
                        e -> {
                            Timber.e(e, "reactOnCrackEvent onError");
                            getView().dismissProgressDialog();
                        }
                );

    }
}