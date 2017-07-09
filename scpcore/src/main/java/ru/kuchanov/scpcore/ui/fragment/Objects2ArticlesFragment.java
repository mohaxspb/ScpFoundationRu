package ru.kuchanov.scpcore.ui.fragment;

import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.mvp.contract.Objects2Articles;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class Objects2ArticlesFragment
        extends BaseListArticlesWithSearchFragment<Objects2Articles.View, Objects2Articles.Presenter>
        implements Objects2Articles.View {

    public static final String TAG = Objects2ArticlesFragment.class.getSimpleName();

    public static Objects2ArticlesFragment newInstance() {
        return new Objects2ArticlesFragment();
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