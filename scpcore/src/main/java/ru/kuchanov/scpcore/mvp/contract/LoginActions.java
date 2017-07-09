package ru.kuchanov.scpcore.mvp.contract;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.db.model.User;

/**
 * Created by mohax on 24.03.2017.
 * <p>
 * for scp_ru
 */
public interface LoginActions {

    interface View {

        void startLogin(Constants.Firebase.SocialProvider provider);

        void updateUser(User user);

        void showNeedReloginPopup();

        void showLoginProvidersPopup();
    }

    interface Presenter {

        void startFirebaseLogin(Constants.Firebase.SocialProvider provider, String id);

        void logoutUser();
    }
}