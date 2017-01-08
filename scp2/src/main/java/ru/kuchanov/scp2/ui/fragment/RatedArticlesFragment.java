package ru.kuchanov.scp2.ui.fragment;

import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.mvp.contract.RatedArticles;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class RatedArticlesFragment
        extends BaseArticlesListFragment<RatedArticles.View, RatedArticles.Presenter>
        implements RatedArticles.View {

    public static final String TAG = RatedArticlesFragment.class.getSimpleName();

    public static RatedArticlesFragment newInstance() {
        return new RatedArticlesFragment();
    }

    @Override
    protected void callInjections() {
        MyApplication.getAppComponent().inject(this);
    }
}