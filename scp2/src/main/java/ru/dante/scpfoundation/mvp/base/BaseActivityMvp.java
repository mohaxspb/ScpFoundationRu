package ru.dante.scpfoundation.mvp.base;

import ru.dante.scpfoundation.mvp.contract.LoginActions;

/**
 * Created by mohax on 23.03.2017.
 * <p>
 * for scp_ru
 */
public interface BaseActivityMvp {

    interface View extends BaseMvp.View, LoginActions.View {

    }

    interface Presenter<V extends View> extends BaseMvp.Presenter<V>, LoginActions.Presenter {

        void onActivityStarted();

        void onActivityStopped();

        void deleteAllData();
    }
}