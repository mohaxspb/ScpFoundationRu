package ru.kuchanov.scp2.ui.fragment;

import android.support.annotation.NonNull;

import java.util.List;

import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.db.model.Article;
import ru.kuchanov.scp2.mvp.contract.RecentArticles;
import ru.kuchanov.scp2.ui.base.BaseListFragment;
import timber.log.Timber;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class RecentArticlesFragment extends BaseListFragment<Article, RecentArticles.View, RecentArticles.Presenter> implements RecentArticles.View {

    public static final String TAG = RecentArticlesFragment.class.getSimpleName();

    public static RecentArticlesFragment newInstance() {
        return new RecentArticlesFragment();
    }

    @Override
    protected int getResId() {
        return R.layout.fragment_feed;
    }

    @NonNull
    @Override
    public RecentArticles.Presenter createPresenter() {
        return mPresenter;
    }

    @Override
    protected void callInjections() {
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    public void showError(Throwable throwable) {

    }

    @Override
    public void updateData(List<Article> data) {
        //TODO
        Timber.d("updateData: %s", data);
    }
}