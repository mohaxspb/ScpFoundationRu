package ru.dante.scpfoundation.mvp.base;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vk.sdk.VKAccessToken;

import io.realm.RealmList;
import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.api.error.ScpLoginException;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.SocialProviderModel;
import ru.dante.scpfoundation.db.model.User;
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
        } else {
            // User is signed out
            Timber.d("onAuthStateChanged: signed_out");
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
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (firebaseUser == null) {
                            return Observable.error(new ScpLoginException(
                                    MyApplication.getAppInstance()
                                            .getString(R.string.error_login_firebase_connection,
                                                    "firebase user is null")));
                        }
                        User userToWriteToDb = new User();
                        userToWriteToDb.uid = firebaseUser.getUid();
                        userToWriteToDb.fullName = firebaseUser.getDisplayName();
                        if (firebaseUser.getPhotoUrl() != null) {
                            userToWriteToDb.avatar = firebaseUser.getPhotoUrl().toString();
                        }
                        userToWriteToDb.email = firebaseUser.getEmail();
                        userToWriteToDb.socialProviders = new RealmList<>();
                        userToWriteToDb.socialProviders.add(new SocialProviderModel(
                                Constants.Firebase.SocialProvider.VK.name(),
                                VKAccessToken.currentToken().userId)
                        );
                        return mApiClient.writeUserToFirebaseObservable(userToWriteToDb);
                    } else {
                        return Observable.just(userObjectInFirebase);
                    }
                })
                .flatMap(userObjectInFirebase -> mDbProviderFactory.getDbProvider().saveUser(userObjectInFirebase))
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
        mDbProviderFactory.getDbProvider().logout().subscribe(
                result -> Timber.d("logout successful"),
                error -> Timber.e(error, "error while logout user")
        );
    }

    @Override
    public void onActivityStarted() {
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onActivityStopped() {
        mAuth.removeAuthStateListener(mAuthListener);
    }
}