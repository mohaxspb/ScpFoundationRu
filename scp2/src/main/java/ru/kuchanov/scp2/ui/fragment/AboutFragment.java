package ru.kuchanov.scp2.ui.fragment;

import android.support.v4.app.Fragment;

import ru.kuchanov.scp2.R;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class AboutFragment extends Fragment/* extends BaseFragment<About.View, About.Presenter> implements About.View */ {

    public static final String TAG = AboutFragment.class.getSimpleName();

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }
/*
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

    @Override
    public void showSwipeProgress(boolean show) {

    }

    @Override
    public void showCenterProgress(boolean show) {

    }

    @Override
    public void showBottomProgress(boolean show) {

    }

    @Override
    public void enableSwipeRefresh(boolean enable) {

    }

    @Override
    public void updateData(List<Article> data) {

    }*/
}