package ru.dante.scpfoundation.mvp.base;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.db.model.User;
import rx.Observable;

/**
 * Created by mohax on 23.03.2017.
 * <p>
 * for scp_ru
 */
public interface BaseActivityMvp {

    interface View extends BaseMvp.View {

        void startLogin(@Constants.Firebase.SocialProvider String provider);

        void updateUser(User user);
    }

    interface Presenter<V extends View> extends BaseMvp.Presenter<V> {

        void getUserFromDb();

        Observable<User> getUserFromFirebaseObservable();

        void onUserLogined(User user);

        User getUser();

        void startFirebaseLogin();

        void logoutUser();

        void authWithCustomToken(String token);

        void updateFirebaseUserProfileDataFromProvider(@Constants.Firebase.SocialProvider String provider);

        Observable<User> writeUserToFirebase(User user);

        void onActivityStarted();

        void onActivityStopped();
    }
}