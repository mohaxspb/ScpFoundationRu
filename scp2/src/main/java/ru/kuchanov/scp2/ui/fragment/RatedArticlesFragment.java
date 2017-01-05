package ru.kuchanov.scp2.ui.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import ru.kuchanov.scp2.Constants;
import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.db.model.Article;
import ru.kuchanov.scp2.mvp.contract.RatedArticles;
import ru.kuchanov.scp2.ui.adapter.RecyclerAdapterListArticles;
import ru.kuchanov.scp2.ui.base.BaseListFragment;
import ru.kuchanov.scp2.ui.util.EndlessRecyclerViewScrollListener;
import timber.log.Timber;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class RatedArticlesFragment extends BaseListFragment<RatedArticles.View, RatedArticles.Presenter> implements RatedArticles.View {

    public static final String TAG = RatedArticlesFragment.class.getSimpleName();

    private RecyclerAdapterListArticles mAdapter;

    public static RatedArticlesFragment newInstance() {
        return new RatedArticlesFragment();
    }

    @Override
    protected int getResId() {
        return R.layout.fragment_feed;
    }

    @NonNull
    @Override
    public RatedArticles.Presenter createPresenter() {
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

        mAdapter = new RecyclerAdapterListArticles();
        mAdapter.setArticleClickListener(new RecyclerAdapterListArticles.ArticleClickListener() {
            @Override
            public void onArticleClicked(Article article) {
                //TODO
//                Intent intent = new Intent(getActivity(), ActivityArticles.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("title", articles.get(position).getTitle());
//                bundle.putString("url", articles.get(position).getURL());
//                intent.putExtras(bundle);
//                ctx.startActivity(intent);
            }

            @Override
            public void toggleReadenState(Article article) {
                //TODO
            }

            @Override
            public void toggleFavoriteState(Article article) {
                //TODO
            }

            @Override
            public void onDownloadClicked(Article article) {
                //TODO start download
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        if (mPresenter.getData() != null) {
            mAdapter.setData(mPresenter.getData());
        } else {
            mPresenter.getDataFromDb();
            mPresenter.getDataFromApi(Constants.Api.ZERO_OFFSET);
        }

        resetOnScrollListener();

        assert mSwipeRefreshLayout != null;
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            Timber.d("onRefresh");
            mPresenter.getDataFromApi(Constants.Api.ZERO_OFFSET);
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

    private void resetOnScrollListener() {
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
}