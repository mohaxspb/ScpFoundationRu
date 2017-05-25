package ru.dante.scpfoundation.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.db.model.ArticleTag;
import ru.dante.scpfoundation.mvp.contract.TagsSearchMvp;
import ru.dante.scpfoundation.ui.base.BaseFragment;
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

    private List<ArticleTag> mTags;

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
//        TODO

        mPresenter.onCreate();
    }

    @Override
    public void showAllTags(List<ArticleTag> data) {
        Timber.d("showAllTags: %s", data);
        mAllTagsContainer.removeAllViews();
        for (ArticleTag tag : data) {
            Timber.d("add tag: %s", tag.title);
            View tagView = LayoutInflater.from(getActivity()).inflate(R.layout.card_tag, mAllTagsContainer, false);
            TextView tagTitle = ButterKnife.findById(tagView, R.id.title);
//            ImageView tagAction = ButterKnife.findById(tagView, R.id.action);
//
            tagTitle.setText(tag.title);
            tagView.setOnClickListener(mAllTagsClickListener);

            mAllTagsContainer.addView(tagView);
        }
    }

    private View.OnClickListener mAllTagsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mAllTagsContainer.removeView(view);
            mSearchTagsContainer.addView(view);

            ImageView tagAction = ButterKnife.findById(view, R.id.action);
            tagAction.setImageResource(R.drawable.ic_clear);
            view.setOnClickListener(mSearchTagsClickListener);
        }
    };

    private View.OnClickListener mSearchTagsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mSearchTagsContainer.removeView(view);
            mAllTagsContainer.addView(view);

            ImageView tagAction = ButterKnife.findById(view, R.id.action);
            tagAction.setImageResource(R.drawable.ic_add);
            view.setOnClickListener(mAllTagsClickListener);
        }
    };
}
