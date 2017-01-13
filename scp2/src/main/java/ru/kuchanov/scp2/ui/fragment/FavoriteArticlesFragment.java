package ru.kuchanov.scp2.ui.fragment;

import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.mvp.contract.FavoriteArticlesMvp;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class FavoriteArticlesFragment
        extends BaseListArticlesWithSearchFragment<FavoriteArticlesMvp.View, FavoriteArticlesMvp.Presenter>
        implements FavoriteArticlesMvp.View {

    public static final String TAG = FavoriteArticlesFragment.class.getSimpleName();

    public static FavoriteArticlesFragment newInstance() {
        return new FavoriteArticlesFragment();
    }

    @Override
    protected void callInjections() {
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    protected void initAdapter() {
        super.initAdapter();
        mAdapter.setShouldShowPopupOnFavoriteClick(true);
    }

    @Override
    protected void getDataFromApi() {
        //FIXME now we do not use any server to store favs
    }

    @Override
    public void resetOnScrollListener() {
        //FIXME now we do not have paging for favs
    }

    @Override
    protected boolean isSwipeRefreshEnabled() {
        //FIXME as we do not have api for it, we do not need to update list
        return false;
    }
}