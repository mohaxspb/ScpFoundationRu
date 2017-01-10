package ru.kuchanov.scp2.ui.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.api.error.ScpParseException;
import ru.kuchanov.scp2.mvp.base.BaseDataPresenter;
import ru.kuchanov.scp2.mvp.base.BaseMvpView;
import timber.log.Timber;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public abstract class BaseFragment<V extends BaseMvpView, P extends BaseDataPresenter<V>> extends MvpFragment<V, P> implements BaseMvpView {

    protected Unbinder mUnbinder;

    @Inject
    protected P mPresenter;

    @NonNull
    @Override
    public P createPresenter() {
        return mPresenter;
    }

    @BindView(R.id.root)
    protected View root;

    protected abstract int getLayoutResId();

    protected abstract void callInjections();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        callInjections();
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(isHasOptionsMenu());
    }

    /**
     *  override it to enable menu for fragemnt
     * @return if fragemnt has options menu
     */
    protected boolean isHasOptionsMenu() {
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(getMenuResId(), menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * override it to add menu when add fragment
     * @return menu res id to add to activities menu
     */
    protected int getMenuResId() {
        return 0;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.d("onCreateView");
        return inflater.inflate(getLayoutResId(), container, false);
    }

    @Override
    public void onViewCreated(android.view.View view, @Nullable Bundle savedInstanceState) {
        Timber.d("onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);
        mPresenter.onCreate();
        initViews();
    }

    /**
     * called
     */
    protected abstract void initViews();

    @Override
    public void onDestroyView() {
        Timber.d("onDestroyView");
        super.onDestroyView();
        mUnbinder.unbind();
    }

    protected int getActionBarHeight() {
        TypedValue tv = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        } else {
            return 0;
        }
    }

    @Override
    public void showError(Throwable throwable) {
        Timber.e(throwable, "showError");
        if (!isAdded()) {
            return;
        }
        String message = throwable.getMessage();
        if (throwable instanceof IOException) {
            message = getString(R.string.error_connection);
        } else if (throwable instanceof ScpParseException) {
            message = getString(R.string.error_parse);
        }
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show();
    }
}