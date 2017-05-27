package ru.dante.scpfoundation.ui.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.db.model.ArticleTag;
import ru.dante.scpfoundation.mvp.contract.ObjectsRuArticles;
import ru.dante.scpfoundation.mvp.contract.TagsSearchMvp;
import ru.dante.scpfoundation.ui.base.BaseFragment;
import ru.dante.scpfoundation.ui.dialog.PopupSettingsFragment;
import ru.dante.scpfoundation.ui.view.TagView;
import ru.dante.scpfoundation.util.DimensionUtils;
import timber.log.Timber;

import static ru.dante.scpfoundation.ui.activity.TagSearchActivity.EXTRA_TAGS;

/**
 * Created by mohax on 25.05.2017.
 * <p>
 * for ScpFoundationRu
 */
public class TagsSearchFragment
        extends BaseFragment<TagsSearchMvp.View, TagsSearchMvp.Presenter>
        implements TagsSearchMvp.View {

    public static final String TAG = TagsSearchFragment.class.getSimpleName();

    @BindView(R.id.tagsSearch)
    FlowLayout mSearchTagsContainer;
    @BindView(R.id.tagsAll)
    FlowLayout mAllTagsContainer;
    @BindView(R.id.searchFAB)
    FloatingActionButton mSearchFab;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.listContainer)
    View mListContainer;

    private ObjectAnimator fabAnimator;

    private List<ArticleTag> mTags = new ArrayList<>();
    private BottomSheetBehavior<View> mBottomSheetListBehavior;

    public static TagsSearchFragment newInstance(List<String> tags) {
        TagsSearchFragment fragment = new TagsSearchFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(EXTRA_TAGS, (ArrayList<String>) tags);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tags_search;
    }

    @Override
    protected void callInjections() {
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    protected void initViews() {
        if (mPresenter.getTags() == null) {
            mPresenter.getTagsFromDb();
        } else {
            showAllTags(mPresenter.getTags());
        }

        if (getUserVisibleHint()) {
            if (getActivity() instanceof ArticleFragment.ToolbarStateSetter) {
                ((ArticleFragment.ToolbarStateSetter) getActivity()).setTitle(getString(R.string.tags_search));
            }
        }

        mSearchTagsContainer.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View view, View view1) {
                mSearchFab.show();
            }

            @Override
            public void onChildViewRemoved(View view, View view1) {
                if (mSearchTagsContainer.getChildCount() == 0) {
                    mSearchFab.hide();
                }
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.zbs_color_red);
        mSwipeRefreshLayout.setOnRefreshListener(() -> mPresenter.getTagsFromApi());
        mSwipeRefreshLayout.setProgressViewEndTarget(false, DimensionUtils.getActionBarHeight(getActivity()));

        mSearchFab.hide();

        mBottomSheetListBehavior = BottomSheetBehavior.from(mListContainer);
        mBottomSheetListBehavior.setPeekHeight(DimensionUtils.dpToPx(286));
        mBottomSheetListBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetListBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                Timber.d("state %s", newState);
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                Timber.d("slideOffSet %s", slideOffset);
//                if (mToolbarBackground != null) {
//                    mToolbarBackground.setBackgroundResource(slideOffset == 1.0f ? android.R.color.black : android.R.color.transparent);
//                }
            }
        });

        if (getChildFragmentManager().findFragmentById(R.id.listContainer) == null) {
            getChildFragmentManager().beginTransaction()
                    .add(R.id.listContainer, ObjectsRuArticlesFragment.newInstance(), ObjectsRuArticlesFragment.TAG)
                    .commit();
        }
    }

    @Override
    public void showAllTags(List<ArticleTag> data) {
        Timber.d("showAllTags: %s", data.size());
        mAllTagsContainer.removeAllViews();
        for (ArticleTag tag : data) {
//            Timber.d("add tag: %s", tag.title);
            TagView tagView = new TagView(getActivity());
            tagView.setTag(tag);

            tagView.setOnTagClickListener(mAllTagsClickListener);

            mAllTagsContainer.addView(tagView);
        }
    }

    @Override
    public void showSearchResults(List<Article> data) {
        Timber.d("showSearchResults: %s", data);

//        PopupSettingsFragment.newInstance().show(getChildFragmentManager(), PopupSettingsFragment.TAG);
        mBottomSheetListBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @OnClick(R.id.searchFAB)
    void onSearchFabClick() {
        Timber.d("onSearchFabClick");
        mPresenter.searchByTags(mTags);
    }

    @Override
    public void showSwipeProgress(boolean show) {
        if (!mSwipeRefreshLayout.isRefreshing() && !show) {
            return;
        }
        mSwipeRefreshLayout.setRefreshing(show);
    }

    @Override
    public void enableSwipeRefresh(boolean enable) {
        mSwipeRefreshLayout.setEnabled(enable);
    }

    @Override
    public void showProgress(boolean show) {
        Timber.d("showProgress: %s", show);

        mSearchFab.setImageResource(show ? R.drawable.ic_autorenew : R.drawable.ic_search);
        mSearchFab.setColorFilter(Color.WHITE);

        if (show) {
            if (fabAnimator == null) {
                fabAnimator = ObjectAnimator.ofFloat(mSearchFab, "rotation", 0, 360);
                fabAnimator.setDuration(1000);
                fabAnimator.setRepeatCount(ValueAnimator.INFINITE);
            } else {
                fabAnimator.removeAllListeners();
                fabAnimator.end();
                fabAnimator.cancel();
            }
            fabAnimator.start();
        } else {
            fabAnimator.removeAllListeners();
            fabAnimator.end();
            fabAnimator.cancel();
            mSearchFab.setRotation(0);
        }
    }

    private TagView.OnTagClickListener mAllTagsClickListener = new TagView.OnTagClickListener() {
        @Override
        public void onTagClicked(TagView view, ArticleTag tag) {
            Timber.d("mAllTagsClickListener: %s", tag);
            mAllTagsContainer.removeView(view);
            mSearchTagsContainer.addView(view);

            mTags.add(tag);

            view.setActionImage(TagView.Action.REMOVE);

            view.setOnTagClickListener(mSearchTagsClickListener);
        }
    };

    private TagView.OnTagClickListener mSearchTagsClickListener = new TagView.OnTagClickListener() {
        @Override
        public void onTagClicked(TagView view, ArticleTag tag) {
            Timber.d("mSearchTagsClickListener: %s", tag);
            mSearchTagsContainer.removeView(view);
            mAllTagsContainer.addView(view);

            mTags.remove(tag);

            view.setActionImage(TagView.Action.ADD);

            view.setOnTagClickListener(mAllTagsClickListener);
        }
    };
}
