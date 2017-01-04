package ru.kuchanov.scp2.mvp.base;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by y.kuchanov on 21.12.16.
 *
 * for scp_ru
 */
public interface BaseDataPresenter<V extends MvpView> extends MvpPresenter<V> {
    void onCreate();

    void onDestroy();
}