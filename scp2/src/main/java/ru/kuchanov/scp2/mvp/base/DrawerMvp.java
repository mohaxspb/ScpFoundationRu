package ru.kuchanov.scp2.mvp.base;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for scp_ru
 */
public interface DrawerMvp {
    interface View extends BaseMvp.View {
        void onNavigationItemClicked(int id);
    }

    interface Presenter<V extends View> extends BaseMvp.Presenter<V> {
        void onNavigationItemClicked(int id);
    }
}