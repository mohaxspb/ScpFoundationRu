package ru.kuchanov.scp2.ui.fragment;

import android.support.annotation.NonNull;

import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.mvp.contract.About;
import ru.kuchanov.scp2.ui.base.BaseFragment;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class AboutFragment extends BaseFragment<About.View, About.Presenter> implements About.View {

    public static final String TAG = AboutFragment.class.getSimpleName();

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    protected int getResId() {
        return R.layout.fragment_feed;
    }

    @NonNull
    @Override
    public About.Presenter createPresenter() {
        return mPresenter;
    }

    @Override
    public void showError(Throwable throwable) {

    }

    @Override
    protected void callInjections() {
        MyApplication.getAppComponent().inject(this);
    }
}