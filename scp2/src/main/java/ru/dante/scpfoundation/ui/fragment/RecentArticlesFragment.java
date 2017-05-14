package ru.dante.scpfoundation.ui.fragment;

import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.mvp.contract.RecentArticlesMvp;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class RecentArticlesFragment
        extends BaseArticlesListFragment<RecentArticlesMvp.View, RecentArticlesMvp.Presenter>
        implements RecentArticlesMvp.View {

    public static final String TAG = RecentArticlesFragment.class.getSimpleName();

    public static RecentArticlesFragment newInstance() {
        return new RecentArticlesFragment();
    }

    @Override
    protected void callInjections() {
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    protected boolean isSwipeRefreshEnabled() {
        return true;
    }

    @Override
    protected boolean isHasOptionsMenu() {
        return false;
    }
}