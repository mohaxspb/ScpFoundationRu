package ru.dante.scpfoundation.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.mvp.base.BaseArticlesListMvp;
import ru.dante.scpfoundation.mvp.base.BaseListMvp;
import ru.dante.scpfoundation.ui.activity.ArticleActivity;
import ru.dante.scpfoundation.ui.adapter.RecyclerAdapterListArticles;
import ru.dante.scpfoundation.ui.base.BaseListFragment;
import ru.dante.scpfoundation.ui.util.EndlessRecyclerViewScrollListener;
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

    @SuppressWarnings("unchecked")
    @Override
    protected RecyclerAdapterListArticles getAdapter() {
        if (mAdapter == null) {
            mAdapter = new RecyclerAdapterListArticles();
        }
        return mAdapter;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_list;
    }

    @Override
    protected void initViews() {
        super.initViews();
        Timber.d("initViews");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        initAdapter();

        mRecyclerView.setAdapter(getAdapter());

        if (mPresenter.getData() != null) {
            getAdapter().setData(mPresenter.getData());
        } else {
            mPresenter.getDataFromDb();
            //TODO add settings to update list on launch
            if (shouldUpdateThisListOnLaunch()) {
                mPresenter.getDataFromApi(Constants.Api.ZERO_OFFSET);
            }
        }

        resetOnScrollListener();

        initSwipeRefresh();
    }

    @Override
    protected boolean isHasOptionsMenu() {
        return true;
    }

    @Override
    protected int getMenuResId() {
        return R.menu.menu_articles_list;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItemSort:
                final List<RecyclerAdapterListArticles.SortType> sortTypes = Arrays.asList(RecyclerAdapterListArticles.SortType.values());
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.dialog_sort_title)
                        .items(sortTypes)
                        .alwaysCallSingleChoiceCallback()
                        .itemsCallbackSingleChoice(RecyclerAdapterListArticles.SortType.valueOf(getAdapter().getSortType().name()).ordinal(), (dialog, itemView, which, text) -> {
                            Timber.d("sortBy: %s", text);
                            getAdapter().sortByType(sortTypes.get(which));
                            dialog.dismiss();
                            return true;
                        })
                        .positiveText(R.string.close)
                        .onPositive((dialog, which) -> dialog.dismiss())
                        .build()
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * override it if you do not want to update this list on first launch
     *
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
        getAdapter().setArticleClickListener(new RecyclerAdapterListArticles.ArticleClickListener() {
            @Override
            public void onArticleClicked(Article article, int position) {
                Timber.d("onArticleClicked: %s/%s", article.title, position);
                ArticleActivity.startActivity(getActivity(), (ArrayList<String>) Article.getListOfUrls(mPresenter.getData()), position);
            }

            @Override
            public void toggleReadenState(Article article) {
                Timber.d("toggleReadenState: %s", article.title);
                mPresenter.toggleReadState(article);
            }

            @Override
            public void toggleFavoriteState(Article article) {
                Timber.d("toggleFavoriteState: %s", article.title);
                mPresenter.toggleFavoriteState(article);
            }

            @Override
            public void onOfflineClicked(Article article) {
                Timber.d("onOfflineClicked: %s", article.title);
                mPresenter.toggleOfflineState(article);
            }
        });
        getAdapter().setHasStableIds(true);
        getAdapter().setShouldShowPopupOnFavoriteClick(isShouldShowPopupOnFavoriteClick());
    }

    protected boolean isShouldShowPopupOnFavoriteClick() {
        return false;
    }

    @Override
    public void updateData(List<Article> data) {
        Timber.d("updateData size: %s", data == null ? "data is null" : data.size());
        if (!isAdded()) {
            return;
        }
        getAdapter().setData(data);
        resetOnScrollListener();
    }

    /**
     * override it to change or disable endless scrolling behavior
     */
    @Override
    public void resetOnScrollListener() {
        mRecyclerView.clearOnScrollListeners();
        if (mAdapter.getItemCount() < Constants.Api.NUM_OF_ARTICLES_ON_SEARCH_PAGE) {
            //so there is to less arts to be able to load from bottom
            //this can be if we receive few search results
            //si we just no need to set scrollListener
            return;
        }
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Timber.d("onLoadMode with page: %s, and offset: %s", page, view.getAdapter().getItemCount());
                showBottomProgress(true);
                mPresenter.getDataFromApi(getAdapter().getItemCount());
            }
        });

        // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
        mRecyclerView.addOnScrollListener(mVerticalRecyclerViewFastScroller.getOnScrollListener());
    }

    @Override
    protected void onTextSizeUiChanged() {
        if (!isAdded()) {
            return;
        }
        getAdapter().notifyDataSetChanged();
    }
}