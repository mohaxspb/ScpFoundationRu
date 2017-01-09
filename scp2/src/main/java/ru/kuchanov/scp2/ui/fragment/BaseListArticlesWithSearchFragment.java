package ru.kuchanov.scp2.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.mvp.base.BaseArticlesListMvp;
import ru.kuchanov.scp2.ui.adapter.RecyclerAdapterListArticlesWithSearch;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public abstract class BaseListArticlesWithSearchFragment<V extends BaseArticlesListMvp.View, P extends BaseArticlesListMvp.Presenter<V>>
        extends BaseArticlesListFragment<V, P> {

    private static final String EXTRA_SEARCH_QUERY = "EXTRA_SEARCH_QUERY";

    @BindView(R.id.searchFAB)
    protected FloatingActionButton mSearchFAB;

    protected RecyclerAdapterListArticlesWithSearch mAdapter;

    private String mSearchQuery = "";
    private MenuItem menuItem;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_list_with_search;
    }

    @Override
    protected boolean isHasOptionsMenu() {
        return true;
    }

    @Override
    protected int getMenuResId() {
        return R.menu.menu_search;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        //TODO implement search
//        switch (item.getItemId()) {
//            case R.id.menuItemSearch:
//                //TODO
//                Snackbar.make(root, R.string.not_implemented_yet, Snackbar.LENGTH_SHORT).show();
//                return true;
//            default:
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mSearchQuery = savedInstanceState.getString(EXTRA_SEARCH_QUERY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_SEARCH_QUERY, mSearchQuery);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        SearchView searchView = new SearchView(getActivity());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSearchQuery = newText;
                mAdapter.sortArticles(newText);
                return false;
            }
        });
        changeSearchViewTextColor(searchView);
        MenuItem search = menu.findItem(R.id.menuItemSearch);
        search.setActionView(searchView);

        menuItem = search;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mSearchFAB.setOnClickListener(v -> ((SearchView) menuItem.getActionView()).onActionViewExpanded());
    }

    /**
     * (Грязный хак исправления цвета текста)
     */
    private void changeSearchViewTextColor(View view) {
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(Color.WHITE);
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }
}