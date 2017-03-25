package ru.dante.scpfoundation.mvp.base;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.api.error.ScpLoginException;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.User;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
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
    //TODO think if we really need to check user here or we can use one that we get from authInFirebase callback result
    private FirebaseAuth.AuthStateListener mAuthListener = mAuth -> {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            // User is signed in
            Timber.d("onAuthStateChanged:signed_in: %s", firebaseUser.getUid());
            if (TextUtils.isEmpty(firebaseUser.getEmail())) {
                //user firstly signin, so update him with vk data
                mApiClient.nameAndAvatarFromProviderObservable(Constants.Firebase.SocialProvider.VK)
                        .flatMap(nameAvatar -> mApiClient.updateFirebaseUsersNameAndAvatarObservable(nameAvatar.first, nameAvatar.second))
                        .flatMap(aVoid -> mApiClient.updateFirebaseUsersEmailObservable())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                result -> {
                                    Timber.d("result");
                                    //TODO create user object in firebase
                                },
                                error -> {
                                    Timber.e(error);
                                    logoutUser();
                                    getView().dismissProgressDialog();
                                    getView().showError(new ScpLoginException(
                                            MyApplication.getAppInstance()
                                                    .getString(R.string.error_login_firebase_connection,
                                                            error.getMessage())));
                                }
                        );
            } else {
                Timber.d("email not empty, check for user in firebase DB");
                //check if we create user object in remote db
                mApiClient.getUserFromFirebaseObservable().subscribe(userFromFirebase -> {
                            if (userFromFirebase == null) {
                                //create it
                                User userToWriteToDb = new User();
                                userToWriteToDb.uid = firebaseUser.getUid();
                                userToWriteToDb.fullName = firebaseUser.getDisplayName();
                                if (firebaseUser.getPhotoUrl() != null) {
                                    userToWriteToDb.avatar = firebaseUser.getPhotoUrl().toString();
                                }
                                userToWriteToDb.email = firebaseUser.getEmail();
                                userToWriteToDb.network = Constants.Firebase.SocialProvider.VK;
                                mApiClient.writeUserToFirebaseObservable(userToWriteToDb).subscribe(
                                        user -> {
                                            Timber.d("user write to firebase success");
                                            getView().showMessage(MyApplication.getAppInstance()
                                                    .getString(R.string.on_user_logined,
                                                            user.fullName));
                                            //TODO create observable for it
                                            mDbProviderFactory.getDbProvider().saveUser(user).subscribe(
                                                    result -> {
                                                        Timber.d("user saved");
                                                        getView().dismissProgressDialog();
                                                        getView().showMessage(MyApplication.getAppInstance()
                                                                .getString(R.string.on_user_logined,
                                                                        user.fullName));
                                                    },
                                                    error -> {
                                                        Timber.e(error, "error while save user to DB");
                                                        logoutUser();
                                                        getView().dismissProgressDialog();
                                                        getView().showError(new ScpLoginException(
                                                                MyApplication.getAppInstance()
                                                                        .getString(R.string.error_login_firebase_connection,
                                                                                error.getMessage())));
                                                    }
                                            );
                                        },
                                        error -> {
                                            Timber.e(error);
                                            logoutUser();
                                            getView().dismissProgressDialog();
                                            getView().showError(new ScpLoginException(
                                                    MyApplication.getAppInstance()
                                                            .getString(R.string.error_login_firebase_connection,
                                                                    error.getMessage())));
                                        }
                                );
                            } else {
                                //add it to realn DB
                                Timber.d("user found in firebase");
                                //TODO create observable for it
                                mDbProviderFactory.getDbProvider().saveUser(userFromFirebase).subscribe(
                                        result -> {
                                            Timber.d("user saved");
                                            getView().dismissProgressDialog();
                                            getView().showMessage(MyApplication.getAppInstance()
                                                    .getString(R.string.on_user_logined,
                                                            userFromFirebase.fullName));
                                        },
                                        error -> {
                                            Timber.e(error, "error while save user to DB");
                                            logoutUser();
                                            getView().dismissProgressDialog();
                                            getView().showError(new ScpLoginException(
                                                    MyApplication.getAppInstance()
                                                            .getString(R.string.error_login_firebase_connection,
                                                                    error.getMessage())));
                                        }
                                );
                            }
                        },
                        error -> {
                            Timber.e(error);
                            logoutUser();
                            getView().dismissProgressDialog();
                            getView().showError(new ScpLoginException(
                                    MyApplication.getAppInstance()
                                            .getString(R.string.error_login_firebase_connection,
                                                    error.getMessage())));
                        }
                );
            }
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
     * depend on social provider creates proper observable,
     * that emits Firebase user after successfully login to firebase
     * and prints it to logs;
     * if some error ocures dissmises progress dialog in activity,
     * shows message to user
     * and call {@link #logoutUser()}
     * <p>
     * !!! WARNING !!!
     * <p>
     * Do not use result of created Observable as firebase will call AuthStateChanedListener itself
     * <p>
     * !!! WARNING !!!
     * <p>
     *
     * @param provider login provider to usse to login to firebase
     */
    @Override
    public void startFirebaseLogin(@Constants.Firebase.SocialProvider String provider) {
        getView().showProgressDialog(R.string.login_in_progress_custom_token);
        mApiClient.getAuthInFirebaseWithSocialProviderObservable(provider).subscribe(
                //now onAuthStateChanggedListener will be called
                firebaseUser -> {
                    Timber.d("successfully login to firebase and gain user: %s", firebaseUser);
                    //TODO
                },
                error -> {
                    Timber.e(error);
                    logoutUser();
                    getView().dismissProgressDialog();
                    getView().showError(error);
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