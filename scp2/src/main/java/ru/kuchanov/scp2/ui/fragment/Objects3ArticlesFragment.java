package ru.kuchanov.scp2.ui.fragment;

import android.support.design.widget.Snackbar;
import android.view.MenuItem;

import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.mvp.contract.Objects1Articles;
import ru.kuchanov.scp2.mvp.contract.Objects3Articles;
import ru.kuchanov.scp2.mvp.contract.OfflineArticles;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class Objects3ArticlesFragment
        extends BaseListArticlesWithSearchFragment<Objects3Articles.View, Objects3Articles.Presenter>
        implements Objects3Articles.View {

    public static final String TAG = Objects3ArticlesFragment.class.getSimpleName();

    public static Objects3ArticlesFragment newInstance() {
        return new Objects3ArticlesFragment();
    }

    @Override
    protected void callInjections() {
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    protected void resetOnScrollListener() {
        //FIXME now we do not have paging for favs
    }

    @Override
    protected boolean shouldUpdateThisListOnLaunch() {
        return false;
    }
}