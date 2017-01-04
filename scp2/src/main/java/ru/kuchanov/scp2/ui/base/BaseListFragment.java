package ru.kuchanov.scp2.ui.base;

import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import butterknife.BindView;
import io.realm.RealmObject;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.mvp.base.BaseListMvp;
import ru.kuchanov.scp2.util.DimensionUtils;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public abstract class BaseListFragment<V extends BaseListMvp.View, P extends BaseListMvp.Presenter<V>>
        extends BaseFragment<V, P> implements BaseListMvp.View {

    @BindView(R.id.root)
    protected View root;
    @Nullable
    @BindView(R.id.progressCenter)
    protected ProgressBar mProgressBarCenter;
    @Nullable
    @BindView(R.id.swipeRefresh)
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recyclerView)
    protected RecyclerView mRecyclerView;

    @Override
    public void showSwipeProgress(boolean show) {
        if (!isAdded() || mSwipeRefreshLayout == null) {
            return;
        }
        if (!mSwipeRefreshLayout.isRefreshing() && !show) {
            return;
        }
        mSwipeRefreshLayout.setProgressViewEndTarget(false, getActionBarHeight());
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
    public void showBottomProgress(boolean show) {
        if (!isAdded() || mSwipeRefreshLayout == null) {
            return;
        }
        if (!mSwipeRefreshLayout.isRefreshing() && !show) {
            return;
        }

        int screenHeight = DimensionUtils.getScreenHeight();
        mSwipeRefreshLayout.setProgressViewEndTarget(false, screenHeight - getActionBarHeight() * 2);

        mSwipeRefreshLayout.setRefreshing(show);
    }

    @Override
    public void enableSwipeRefresh(boolean enable) {
        if (!isAdded() || mSwipeRefreshLayout == null) {
            return;
        }
        mSwipeRefreshLayout.setEnabled(enable);
    }
}