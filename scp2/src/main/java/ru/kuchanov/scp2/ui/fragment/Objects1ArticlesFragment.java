package ru.kuchanov.scp2.ui.fragment;

import android.support.design.widget.Snackbar;
import android.view.MenuItem;

import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.mvp.contract.Objects1Articles;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class Objects1ArticlesFragment
        extends BaseListArticlesWithSearchFragment<Objects1Articles.View, Objects1Articles.Presenter>
        implements Objects1Articles.View {

    public static final String TAG = Objects1ArticlesFragment.class.getSimpleName();

    public static Objects1ArticlesFragment newInstance() {
        return new Objects1ArticlesFragment();
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