package ru.dante.scpfoundation.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import org.parceler.Parcels;

import java.util.List;

import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.db.model.ArticleTag;
import ru.dante.scpfoundation.mvp.contract.TagsSearchResultsArticlesMvp;
import timber.log.Timber;

import static ru.dante.scpfoundation.ui.activity.TagSearchActivity.EXTRA_TAGS;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class TagsSearchResultsArticlesFragment
        extends BaseListArticlesWithSearchFragment<TagsSearchResultsArticlesMvp.View, TagsSearchResultsArticlesMvp.Presenter>
        implements TagsSearchResultsArticlesMvp.View {

    public static final String TAG = TagsSearchResultsArticlesFragment.class.getSimpleName();
    private static final String EXTRA_ARTICLES = "EXTRA_ARTICLES";

    public static TagsSearchResultsArticlesFragment newInstance(List<Article> articles, List<ArticleTag> tags) {
        TagsSearchResultsArticlesFragment fragment = new TagsSearchResultsArticlesFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_TAGS, Parcels.wrap(tags));
        args.putParcelable(EXTRA_ARTICLES, Parcels.wrap(articles));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Timber.d("onViewCreated");
        List<Article> articles = Parcels.unwrap(getArguments().getParcelable(EXTRA_ARTICLES));
        List<ArticleTag> tags = Parcels.unwrap(getArguments().getParcelable(EXTRA_TAGS));
        Timber.d("articles: %s", articles);
        Timber.d("tags: %s", tags);
        mPresenter.setQueryTags(tags);
        mPresenter.setSearchData(articles);

        updateData(articles);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void initViews() {
        super.initViews();

        if (getUserVisibleHint()) {
            if (getActivity() instanceof ArticleFragment.ToolbarStateSetter) {
                ((ArticleFragment.ToolbarStateSetter) getActivity()).setTitle(getString(R.string.tags_search_results));
            }
        }
    }

    @Override
    public void enableSwipeRefresh(boolean enable) {
        //do not need to diasble it
    }

    @Override
    protected void callInjections() {
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    public void resetOnScrollListener() {
        //we do not have paging
    }

    @Override
    protected boolean shouldUpdateThisListOnLaunch() {
        return false;
    }
}