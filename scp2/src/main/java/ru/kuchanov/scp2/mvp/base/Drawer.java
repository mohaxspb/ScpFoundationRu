package ru.kuchanov.scp2.mvp.base;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for scp_ru
 */
public interface Drawer {
    interface View extends BaseMvpView {
        void onNavigationItemClicked(int id);
    }

    interface Presenter<V extends View> extends BaseDataPresenter<V> {
        void onNavigationItemClicked(int id);
    }
}