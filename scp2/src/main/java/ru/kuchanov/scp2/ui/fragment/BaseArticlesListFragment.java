package ru.kuchanov.scp2.ui.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.kuchanov.scp2.Constants;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.db.model.Article;
import ru.kuchanov.scp2.mvp.base.BaseArticlesListMvp;
import ru.kuchanov.scp2.mvp.base.BaseListMvp;
import ru.kuchanov.scp2.ui.activity.ArticleActivity;
import ru.kuchanov.scp2.ui.adapter.RecyclerAdapterListArticles;
import ru.kuchanov.scp2.ui.base.BaseListFragment;
import ru.kuchanov.scp2.ui.util.EndlessRecyclerViewScrollListener;
import timber.log.Timber;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public abstract class BaseArticlesListFragment<V extends BaseArticlesListMvp.View, P extends BaseArticlesListMvp.Presenter<V>>
        extends BaseListFragment<V, P>
        implements BaseListMvp.View {

    protected RecyclerAdapterListArticles mAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_feed;
    }

    @NonNull
    @Override
    public P createPresenter() {
        return mPresenter;
    }

    @Override
    protected void initViews() {
        Timber.d("initViews");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        initAdapter();

        mRecyclerView.setAdapter(mAdapter);

        if (mPresenter.getData() != null) {
            mAdapter.setData(mPresenter.getData());
        } else {
            getDataFromDb();
            //TODO add settings to update list on launch
            if (shouldUpdateThisListOnLaunch()) {
                getDataFromApi();
            }
        }

        resetOnScrollListener();

        initSwipeRefresh();
    }

    /**
     * override it if you do not want to update this list on first launch
     * @return true by default
     */
    protected boolean shouldUpdateThisListOnLaunch() {
        return true;
    }

    @Override
    protected boolean isSwipeRefreshEnabled() {
        return true;
    }

    private void initSwipeRefresh() {
        if (mSwipeRefreshLayout != null) {
            if (isSwipeRefreshEnabled()) {
                mSwipeRefreshLayout.setColorSchemeResources(R.color.zbs_color_red);
                mSwipeRefreshLayout.setOnRefreshListener(() -> {
                    Timber.d("onRefresh");
                    mPresenter.getDataFromApi(Constants.Api.ZERO_OFFSET);
                });
            } else {
                mSwipeRefreshLayout.setEnabled(false);
            }
        }
    }

    /**
     * override it to add something
     */
    protected void initAdapter() {
        mAdapter = new RecyclerAdapterListArticles();
        mAdapter.setArticleClickListener(new RecyclerAdapterListArticles.ArticleClickListener() {
            @Override
            public void onArticleClicked(Article article, int position) {
                ArticleActivity.startActivity(getActivity(), (ArrayList<String>) Article.getListOfUrls(mPresenter.getData()), position);
            }

            @Override
            public void toggleReadenState(Article article) {
                mPresenter.toggleReadenState(article.url);
            }

            @Override
            public void toggleFavoriteState(Article article) {
                mPresenter.toggleFavoriteState(article.url);
            }

            @Override
            public void onOfflineClicked(Article article) {
                mPresenter.toggleOfflineState(article);
            }
        });
    }

    @Override
    public void updateData(List<Article> data) {
        Timber.d("updateData size: %s", data.size());
        if (!isAdded()) {
            return;
        }
        mAdapter.setData(data);
        resetOnScrollListener();
    }

    protected void getDataFromDb() {
        mPresenter.getDataFromDb();
    }

    protected void getDataFromApi() {
        mPresenter.getDataFromApi(Constants.Api.ZERO_OFFSET);
    }

    /**
     * override it to change or disable endless scroolling behavior
     */
    protected void resetOnScrollListener() {
        mRecyclerView.clearOnScrollListeners();
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Timber.d("onLoadMode with page: %s, and offset: %s", page, view.getAdapter().getItemCount());
                showBottomProgress(true);
                mPresenter.getDataFromApi(mAdapter.getItemCount());
            }
        });
    }

    @Override
    protected void onTextSizeUiChanged() {
        if (!isAdded()) {
            return;
        }
        mAdapter.notifyDataSetChanged();
    }
}