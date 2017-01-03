package ru.kuchanov.scp2.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.kuchanov.scp2.R;
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

    @BindView(R.id.root)
    protected View root;

    protected abstract int getResId();

    protected abstract void callInjections();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        callInjections();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getResId(), container, false);
    }

    @Override
    public void onViewCreated(android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, view);
        mPresenter.onCreate();
    }

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
}