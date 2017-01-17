package ru.dante.scpfoundation.mvp.base;

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
    }

    interface Presenter<V extends View> extends BaseMvp.Presenter<V> {
        void onNavigationItemClicked(int id);

        void getRandomArticleUrl();
    }
}