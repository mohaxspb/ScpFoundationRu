package ru.kuchanov.scp2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.BindView;
import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.db.model.Article;
import ru.kuchanov.scp2.mvp.contract.ArticleMvp;
import ru.kuchanov.scp2.ui.adapter.RecyclerAdapterArticle;
import ru.kuchanov.scp2.ui.base.BaseFragment;
import timber.log.Timber;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class ArticleFragment extends BaseFragment<ArticleMvp.View, ArticleMvp.Presenter> implements ArticleMvp.View {

    public static final String TAG = ArticleFragment.class.getSimpleName();

    public static final String EXTRA_URL = "EXTRA_URL";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";

    @BindView(R.id.progressCenter)
    protected ProgressBar mProgressBarCenter;
    @BindView(R.id.swipeRefresh)
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recyclerView)
    protected RecyclerView mRecyclerView;

    private RecyclerAdapterArticle mAdapter;

    public static ArticleFragment newInstance(String url, String title) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_URL, url);
        args.putString(EXTRA_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getResId() {
        return R.layout.fragment_article;
    }

    @NonNull
    @Override
    public ArticleMvp.Presenter createPresenter() {
        return mPresenter;
    }

    @Override
    protected void callInjections() {
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    protected void initViews() {
        Timber.d("initViews");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new RecyclerAdapterArticle();

        mRecyclerView.setAdapter(mAdapter);

        if (mPresenter.getData() != null) {
            mAdapter.setData(mPresenter.getData());
        } else {
            mPresenter.getDataFromDb();
        }

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            Timber.d("onRefresh");
            mPresenter.getDataFromApi();
        });
    }

    @Override
    public void enableSwipeRefresh(boolean enable) {
        if (!isAdded() || mSwipeRefreshLayout == null) {
            return;
        }
        mSwipeRefreshLayout.setEnabled(enable);
    }

    @Override
    public void showSwipeProgress(boolean show) {
        if (!isAdded()) {
            return;
        }
        if (!mSwipeRefreshLayout.isRefreshing() && !show) {
            return;
        }
//        mSwipeRefreshLayout.setProgressViewEndTarget(false, getActionBarHeight());
        mSwipeRefreshLayout.setRefreshing(show);
    }

    @Override
    public void showCenterProgress(boolean show) {
        if (!isAdded() || mProgressBarCenter == null) {
            return;
        }
        mProgressBarCenter.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showData(Article article) {
        Timber.d("showData: %s", article);
        if (!isAdded()) {
            return;
        }
        mAdapter.setData(article);
    }
}