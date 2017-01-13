package ru.kuchanov.scp2.ui.fragment;

import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.mvp.contract.ObjectsRuArticles;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class ObjectsRuArticlesFragment
        extends BaseListArticlesWithSearchFragment<ObjectsRuArticles.View, ObjectsRuArticles.Presenter>
        implements ObjectsRuArticles.View {

    public static final String TAG = ObjectsRuArticlesFragment.class.getSimpleName();

    public static ObjectsRuArticlesFragment newInstance() {
        return new ObjectsRuArticlesFragment();
    }

    @Override
    protected void callInjections() {
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    public void resetOnScrollListener() {
        //FIXME now we do not have paging for favs
    }

    @Override
    protected boolean shouldUpdateThisListOnLaunch() {
        return false;
    }
}