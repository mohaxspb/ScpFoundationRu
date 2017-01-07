package ru.kuchanov.scp2.mvp.contract;

import ru.kuchanov.scp2.mvp.base.DrawerMvp;

/**
 * Created by y.kuchanov on 21.12.16.
 *
 * for scp_ru
 */
public interface MainMvp extends DrawerMvp {
    interface View extends DrawerMvp.View {
        void setToolbarTitleByDrawerItemId(int id);
    }

    interface Presenter extends DrawerMvp.Presenter<View> {
    }
}