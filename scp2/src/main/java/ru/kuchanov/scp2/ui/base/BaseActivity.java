package ru.kuchanov.scp2.ui.base;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;

import javax.inject.Inject;

import butterknife.BindView;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.mvp.base.BaseMvpView;
import ru.kuchanov.scp2.mvp.base.BasePresenter;

/**
 * Created by mohax on 31.12.2016.
 * <p>
 * for scp_ru
 */
public abstract class BaseActivity<V extends BaseMvpView, P extends MvpPresenter<V>> extends MvpActivity<V, P> implements BaseMvpView {

    @BindView(R.id.root)
    protected View root;

    @Inject
    protected P mPresenter;

    @Override
    public void showError(Throwable throwable) {
        //TODO switch errors types
        Snackbar.make(root, throwable.getMessage(), Snackbar.LENGTH_SHORT);
    }
}