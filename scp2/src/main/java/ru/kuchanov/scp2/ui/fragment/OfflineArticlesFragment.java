package ru.kuchanov.scp2.ui.fragment;

import android.support.design.widget.Snackbar;
import android.view.MenuItem;

import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.mvp.contract.OfflineArticles;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class OfflineArticlesFragment
        extends BaseListArticlesWithSearchFragment<OfflineArticles.View, OfflineArticles.Presenter>
        implements OfflineArticles.View {

    public static final String TAG = OfflineArticlesFragment.class.getSimpleName();

    public static OfflineArticlesFragment newInstance() {
        return new OfflineArticlesFragment();
    }

    @Override
    protected void callInjections() {
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    protected boolean isHasOptionsMenu() {
        return true;
    }

    @Override
    protected int getMenuResId() {
        return R.menu.menu_offline;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItemDownloadAll:
                //TODO
                Snackbar.make(root, R.string.not_implemented_yet, Snackbar.LENGTH_SHORT).show();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void getDataFromApi() {
        //FIXME now we do not use any server to store favs
    }

    @Override
    protected void resetOnScrollListener() {
        //FIXME now we do not have paging for favs
    }

    @Override
    protected boolean isSwipeRefreshEnabled() {
        //FIXME as we do not have api for it, we do not need to update list
        return false;
    }
}