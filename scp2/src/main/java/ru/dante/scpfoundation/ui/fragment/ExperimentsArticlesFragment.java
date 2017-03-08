package ru.dante.scpfoundation.ui.fragment;

import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.mvp.contract.ExperimentsArticles;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class ExperimentsArticlesFragment
        extends BaseListArticlesWithSearchFragment<ExperimentsArticles.View, ExperimentsArticles.Presenter>
        implements ExperimentsArticles.View {

    public static final String TAG = ExperimentsArticlesFragment.class.getSimpleName();

    public static ExperimentsArticlesFragment newInstance() {
        return new ExperimentsArticlesFragment();
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