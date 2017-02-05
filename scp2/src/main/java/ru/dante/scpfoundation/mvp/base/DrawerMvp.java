package ru.dante.scpfoundation.mvp.base;

import ru.dante.scpfoundation.db.model.User;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for scp_ru
 */
public interface DrawerMvp {
    interface View extends BaseMvp.View {
        /**
         *
         * @return true if need to show selected
         */
        boolean onNavigationItemClicked(int id);

        void startArticleActivity(String url);

        void showProgressDialog(boolean show);

        void onGetUserFromDB(User user);
    }

    interface Presenter<V extends View> extends BaseMvp.Presenter<V> {
        void onNavigationItemClicked(int id);

        void getRandomArticleUrl();
    }
}