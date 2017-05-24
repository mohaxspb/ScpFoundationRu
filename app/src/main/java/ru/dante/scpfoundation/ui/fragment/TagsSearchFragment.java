package ru.dante.scpfoundation.ui.fragment;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.db.model.ArticleTag;
import ru.dante.scpfoundation.mvp.contract.TagsSearchMvp;
import ru.dante.scpfoundation.ui.base.BaseFragment;

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
}
