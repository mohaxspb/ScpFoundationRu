package ru.dante.scpfoundation.mvp.base;

import android.support.annotation.StringRes;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

import ru.dante.scpfoundation.db.model.User;
import ru.dante.scpfoundation.mvp.contract.DataSyncActions;

/**
 * Created by mohax on 14.01.2017.
 * <p>
 * for scp_ru
 */
public interface BaseMvp {

    interface View extends MvpView {

        void showMessage(String message);

        void showMessage(@StringRes int message);

        void showError(Throwable throwable);

        void showProgressDialog(String title);

        void showProgressDialog(@StringRes int title);

        void dismissProgressDialog();
    }

    interface Presenter<V extends MvpView> extends MvpPresenter<V>, DataSyncActions {
        void onCreate();

        void getUserFromDb();

        User getUser();

        void onUserChanged(User user);
    }
}