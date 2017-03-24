package ru.dante.scpfoundation.mvp.contract;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.db.model.User;
import rx.Observable;

/**
 * Created by mohax on 24.03.2017.
 * <p>
 * for scp_ru
 */
public interface LoginActions {

    interface View {

        void startLogin(@Constants.Firebase.SocialProvider String provider);

        void updateUser(User user);
    }

    interface Presenter {

        Observable<User> getUserFromFirebaseObservable();

        void startFirebaseLogin();

        void logoutUser();

        void authWithCustomToken(String token);

        void updateFirebaseUserProfileDataFromProvider(@Constants.Firebase.SocialProvider String provider);

        Observable<User> writeUserToFirebase(User user);
    }
}