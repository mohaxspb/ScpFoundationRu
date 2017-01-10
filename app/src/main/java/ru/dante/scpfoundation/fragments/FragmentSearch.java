package ru.dante.scpfoundation.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.dante.scpfoundation.Article;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.adapters.RecyclerAdapterSearch;
import ru.dante.scpfoundation.utils.DividerItemDecoration;
import ru.dante.scpfoundation.utils.RecyclerViewOnScrollListener;
import ru.dante.scpfoundation.utils.parsing.DownloadSearch;

/**
 * Created for MyApplication by Dante on 14.04.2016  1:06.
 */
public class FragmentSearch extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener, DownloadSearch.UpdateArticlesList {
    private static final String LOG = FragmentSearch.class.getSimpleName();
    private static final java.lang.String KEY_PAGE = "KEY_PAGE";
    private static final java.lang.String KEY_SEARCH_QUERY = "KEY_SEARCH_QUERY";
    private RecyclerView recyclerView;
    private int currentPageToLoad = 1;
    private Context ctx;

    private List<Article> listArticles = new ArrayList<>();
    private String searchQuery = "";

    public static Fragment newInstance() {
        return new FragmentSearch();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx = context;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        SearchView searchView = new SearchView(getActivity());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e(LOG, "onQueryTextSubmit called");
                searchQuery = query;
                if (recyclerView.getAdapter() != null) {
                    int prevSize = listArticles.size();
                    listArticles.clear();
                    recyclerView.getAdapter().notifyItemRangeRemoved(0, prevSize);
                }
                currentPageToLoad = 1;
                DownloadSearch downloadSearch = new DownloadSearch(searchQuery, ctx, FragmentSearch.this, currentPageToLoad);
                downloadSearch.execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText;
                return false;
            }
        });
        changeSearchViewTextColor(searchView);
        menu.add(0, 1000, 0, "Поиск");
        MenuItem search = menu.findItem(1000);
        search.setActionView(searchView);
        search.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Article.KEY_ARTICLE, (ArrayList<? extends Parcelable>) listArticles);
        outState.putInt(KEY_PAGE, currentPageToLoad);
        outState.putString(KEY_SEARCH_QUERY, searchQuery);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recycler_view_simple, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        resetOnScrollListener();
        if (savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(KEY_SEARCH_QUERY);
            currentPageToLoad = savedInstanceState.getInt(KEY_PAGE);
            listArticles = savedInstanceState.getParcelableArrayList(Article.KEY_ARTICLE);
            if (listArticles == null) {
                listArticles = new ArrayList<>();
            }
        }
        if (listArticles.size() != 0) {
            RecyclerAdapterSearch recyclerAdapterNewArticles = new RecyclerAdapterSearch(listArticles);
            recyclerView.setAdapter(recyclerAdapterNewArticles);
            resetOnScrollListener();
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        pref.registerOnSharedPreferenceChangeListener(this);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void resetOnScrollListener() {
        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(new RecyclerViewOnScrollListener() {
            @Override
            public void onLoadMore() {
                currentPageToLoad++;
                Log.i(LOG, "onLoadMore page: " + currentPageToLoad);
                DownloadSearch downloadSearch = new DownloadSearch(searchQuery, ctx, FragmentSearch.this, currentPageToLoad);
                downloadSearch.execute();
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (!isAdded()) {
            return;
        }
        if (key.equals(getString(R.string.pref_design_key_text_size_ui))) {
            if (recyclerView.getAdapter() != null) {
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }
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

    @Override
    public void update(List<Article> articles, int page) {
        Log.i(LOG, "update page: " + page);
        if (!isAdded()) {
            return;
        }
        if (articles == null) {
            Snackbar.make(recyclerView, "Connection lost", Snackbar.LENGTH_LONG).show();
        } else {
            if (page > 1) {
                int prevSize = listArticles.size();
                listArticles.addAll(articles);
                recyclerView.getAdapter().notifyItemRangeInserted(prevSize, listArticles.size());
            } else {
                listArticles.addAll(articles);
                recyclerView.setAdapter(new RecyclerAdapterSearch(listArticles));
            }
        }
    }
}