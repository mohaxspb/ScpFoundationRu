package ru.dante.scpfoundation.mvp.base;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.User;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static ru.dante.scpfoundation.Constants.Firebase.SocialProvider.VK;

/**
 * Created by mohax on 23.03.2017.
 * <p>
 * for scp_ru
 */
abstract class BaseActivityPresenter<V extends BaseActivityMvp.View>
        extends BasePresenter<V>
        implements BaseActivityMvp.Presenter<V> {

    private User mUser;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener = mAuth -> {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            // User is signed in
            Timber.d("onAuthStateChanged:signed_in: %s", firebaseUser.getUid());
            if (TextUtils.isEmpty(firebaseUser.getEmail())) {
                //user firstly signin, so update him with vk data
                //TODO switch by provider
                updateFirebaseUserProfileDataFromProvider(Constants.Firebase.SocialProvider.VK);
            } else {
                Timber.d("email not empty, check for user in firebase DB");
                //TODO check if we create user object in remote db
                getUserFromFirebaseObservable().subscribe(
                        userFromFirebase -> {
                            if (userFromFirebase == null) {
                                //create it
                                User userToWriteToDb = new User();
                                userToWriteToDb.fullName = firebaseUser.getDisplayName();
                                if (firebaseUser.getPhotoUrl() != null) {
                                    userToWriteToDb.avatar = firebaseUser.getPhotoUrl().toString();
                                }
                                userToWriteToDb.email = firebaseUser.getEmail();
                                userToWriteToDb.network = Constants.Firebase.SocialProvider.VK;
                                writeUserToFirebase(userToWriteToDb)
                                        .subscribe(
                                                user -> {
                                                    Timber.d("user write to firebase success");
                                                },
                                                error -> {
                                                    //TODO
                                                    Timber.e(error);
                                                }
                                        );
                            } else {
                                //add it to realn DB
                                Timber.d("user from fire base");
                                onUserLogined(userFromFirebase);
                            }
                        },
                        error -> {
                            //tODO
                            Timber.e(error);
                        }
                );
            }
        } else {
            // User is signed out
            Timber.d("onAuthStateChanged:signed_out");
        }
    };

    BaseActivityPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);

        getUserFromDb();
    }

    @Override
    public void getUserFromDb(){
        mDbProviderFactory.getDbProvider().getUserAsync().subscribe(
                user -> {
                    mUser = user;
                    getView().updateUser(mUser);
                },
                error -> Timber.e(error, "error while get user from DB")
        );
    }

    @Override
    public Observable<User> getUserFromFirebaseObservable() {
        return Observable.create(subscriber -> {
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            if (firebaseUser != null) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                firebaseDatabase.getReference()
                        .child(Constants.Firebase.Refs.USERS)
                        .child(firebaseUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User userFromFireBase = dataSnapshot.getValue(User.class);
                                subscriber.onNext(userFromFireBase);
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Timber.e(databaseError.toException(), "onCancelled");
                                subscriber.onError(databaseError.toException());
                            }
                        });
            } else {
                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        });
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

    @Override
    public void startFirebaseLogin() {
        Observable.<String>create(subscriber -> {
            OkHttpClient client = new OkHttpClient();
//            String url = "http://192.168.43.56:8080/scp-ru/MyServlet";
            String url = "http://192.168.0.93:8080/scp-ru/MyServlet"; //home
            String params = "?provider=vk&token=" +
                    VKAccessToken.currentToken().accessToken +
                    "&email=" + VKAccessToken.currentToken().email +
                    "&id=" + VKAccessToken.currentToken().userId;
            Request request = new Request.Builder()
//                    .url("http://37.143.14.68:8080/scp-ru-1/MyServlet?provider=vk&token=" + VKAccessToken.currentToken().accessToken)
//                    .url("http://192.168.0.93:8080/scp-ru/MyServlet?provider=vk&token=" + VKAccessToken.currentToken().accessToken)
                    .url(url + params)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    subscriber.onError(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    subscriber.onNext(response.body().string());
                    subscriber.onCompleted();
                }
            });
        })
                .flatMap(response -> TextUtils.isEmpty(response) ? Observable.error(new IllegalArgumentException("empty token")) : Observable.just(response))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        token -> {
                            Timber.d("token: %s", token);
                            authWithCustomToken(token);
                        },
                        error -> getView().showError(error)
                );
    }

    @Override
    public void logoutUser() {
        VKSdk.logout();
        //TODO add other networks
        mAuth.signOut();
        onUserLogined(null);
    }

    @Override
    public void authWithCustomToken(String token) {
        mAuth.signInWithCustomToken(token).addOnCompleteListener(task -> {
            Timber.d("signInWithCustomToken:onComplete: %s", task.isSuccessful());

            // If sign in fails, display a message to the user. If sign in succeeds
            // the auth state listener will be notified and logic to handle the
            // signed in user can be handled in the listener.
            if (!task.isSuccessful()) {
                Timber.e(task.getException(), "signInWithCustomToken");
                getView().showError(new Throwable("error auth in Firebase with custom token"));
            } else {
                Timber.d("signInWithCustomToken task.getResult(): %s", task.getResult().getUser().getUid());
            }
        });
    }

    //TODO return observable
    @Override
    public void updateFirebaseUserProfileDataFromProvider(@Constants.Firebase.SocialProvider String provider) {
        Observable<Pair<String, String>> nameAvatarObservable;

        switch (provider) {
            case VK:
                nameAvatarObservable = mApiClient.getUserDataFromVk()
                        .flatMap(vkApiUser -> {
                            String displayName = vkApiUser.first_name + " " + vkApiUser.last_name;
                            String avatarUrl = vkApiUser.photo_200;
                            return Observable.just(new Pair<>(displayName, avatarUrl));
                        });
                break;
            default:
                throw new RuntimeException("unexpected provider");
        }

        nameAvatarObservable.flatMap(nameAvatar -> Observable.create(subscriber -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(nameAvatar.first)
                        .setPhotoUri(Uri.parse(nameAvatar.second))
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Timber.d("User profile updated name and photo.");
                                if (VKAccessToken.currentToken() != null) {
                                    user.updateEmail(VKAccessToken.currentToken().email).addOnCompleteListener(task1 -> {
                                        if (task.isSuccessful()) {
                                            Timber.d("User profile updated email.");
                                            subscriber.onNext(null);
                                            subscriber.onCompleted();
                                        } else {
                                            Timber.e("error while update user email");
                                            subscriber.onError(task.getException());
                                        }
                                    });
                                }
                            } else {
                                Timber.e("error while update user name and photo");
                                subscriber.onError(task.getException());
                            }
                        });
            } else {
                Timber.e("user is null while try to update!");
                subscriber.onError(new IllegalStateException("Firebase user is null while try to update its profile"));
            }
        }))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            Timber.d("result");
                            //TODO create user object in firebase
                        },
                        error -> {
                            Timber.e(error);
                            getView().showError(error);
                            logoutUser();
                        }
                );
    }

    @Override
    public Observable<User> writeUserToFirebase(User user) {
        return Observable.create(subscriber -> {
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            if (firebaseUser != null) {
                FirebaseDatabase.getInstance()
                        .getReference(Constants.Firebase.Refs.USERS)
                        .child(firebaseUser.getUid())
                        .setValue(user, (databaseError, databaseReference) -> {
                            if (databaseError == null) {
                                //success
                                Timber.d("user created");
                                subscriber.onNext(user);
                                subscriber.onCompleted();
                            } else {
                                subscriber.onError(databaseError.toException());
                            }
                        });
            } else {
                subscriber.onError(new IllegalStateException("firebase user is null"));
            }
        });
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