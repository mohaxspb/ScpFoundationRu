package ru.kuchanov.scp2.ui.base;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;

import ru.kuchanov.scp2.mvp.base.BaseMvpView;

/**
 * Created by mohax on 31.12.2016.
 * <p>
 * for scp_ru
 */
public abstract class BaseActivity<V extends BaseMvpView, P extends MvpPresenter<V>> extends MvpActivity<V, P> implements BaseMvpView {

}
