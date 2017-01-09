package ru.dante.scpfoundation.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import ru.dante.scpfoundation.Article;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.adapters.RecyclerAdapterObjects;
import ru.dante.scpfoundation.otto.BusProvider;
import ru.dante.scpfoundation.otto.EventArticleDownloaded;
import ru.dante.scpfoundation.utils.CacheUtils;
import ru.dante.scpfoundation.utils.DividerItemDecoration;
import ru.dante.scpfoundation.utils.parsing.DownloadObjects;

/**
 * Created by Dante on 16.01.2016.
 * <p>
 * for scp_ru
 */
public class FragmentObjects extends Fragment implements DownloadObjects.UpdateArticlesList, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG = FragmentObjects.class.getSimpleName();

    public static final String KEY_ARTICLES = "KEY_ARTICLES";
    private static final String KEY_SEARCH_QUERY = "KEY_SEARCH_QUERY";

    private ImageView loadingIndicator;
    private SearchView searchView;
    private MenuItem menuItem;
    private Menu menu;
    private RecyclerView recyclerView;
    private String url;
    private String searchQuery = "";
    private ArrayList<Article> listOfObjects = new ArrayList<>();

    private Context ctx;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.ctx = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onArticleDownloaded(EventArticleDownloaded eventArticleDownloaded) {
        for (int i = 0; i < listOfObjects.size(); i++) {
            if (listOfObjects.get(i).getURL().equals(eventArticleDownloaded.getLink())) {
                recyclerView.getAdapter().notifyItemChanged(i);
                break;
            }
        }
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
                return;
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }

    public static Fragment newInstance(String url) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        Fragment article = new FragmentObjects();
        article.setArguments(bundle);
        return article;
    }

    public static Fragment newInstance(ArrayList<Article> articles) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_ARTICLES, articles);
        Fragment article = new FragmentObjects();
        article.setArguments(bundle);
        return article;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG, "on create view called");
        View v = inflater.inflate(R.layout.fragment_objects, container, false);
        loadingIndicator = (ImageView) v.findViewById(R.id.loading_indicator);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        FloatingActionButton searchButton = (FloatingActionButton) v.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG, "поиск нажат");
//                (У нас ушло 1,5 мать его часа на эту гребаную кнопку)
//                searchView.setIconified(true);
//                        ((SearchView) menuItem.getActionView()).setIconified(true);
//                MenuItemCompat.expandActionView(menuItem);
//                menuItem.expandActionView();
//                menu.findItem(1000).expandActionView();
//                MenuItemCompat.expandActionView(menu.findItem(1000));
//                ((SearchView) menu.findItem(1000).getActionView()).setIconified(true);
//                Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolBar);
//                Menu menu = toolbar.getMenu();
//                menu.findItem(1000).expandActionView();
//                MenuItemCompat.expandActionView(menu.findItem(1000));
//                ((SearchView) menu.findItem(1000).getActionView()).setIconified(true);
                ((SearchView) menuItem.getActionView()).onActionViewExpanded();
            }
        });

        Bundle arguments = this.getArguments();
        if (arguments.containsKey("url")) {
            url = arguments.getString("url");
        } else if (arguments.containsKey(KEY_ARTICLES)) {
            listOfObjects = arguments.getParcelableArrayList(KEY_ARTICLES);
        }

        if (savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(KEY_SEARCH_QUERY);
        }

        if (savedInstanceState != null) {
            listOfObjects = savedInstanceState.getParcelableArrayList(KEY_ARTICLES);
            if (listOfObjects == null) {
                listOfObjects = new ArrayList<>();
            }
        }

        if (listOfObjects.size() == 0 && url != null) {
            ArrayList<Article> objectsFromCache = CacheUtils.getObjectsFromCache(ctx, CacheUtils.getTypeByUrl(url));
            if (objectsFromCache.size() == 0) {
                DownloadObjects downloadObjects = new DownloadObjects(url, this, ctx);
                downloadObjects.execute();
                loadingIndicator.setVisibility(View.VISIBLE);
                loadingIndicator
                        .animate()
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .rotationBy(360)
                        .setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                loadingIndicator
                                        .animate()
                                        .setInterpolator(new AccelerateDecelerateInterpolator())
                                        .rotationBy(360)
                                        .setDuration(500)
                                        .setListener(this);
                            }
                        });
            } else {
                listOfObjects = objectsFromCache;
                recyclerView.setAdapter(new RecyclerAdapterObjects(listOfObjects, searchQuery));
            }
        } else {
//            RecyclerAdapterArticle adapterArticle= new RecyclerAdapterArticle(articleText);
            recyclerView.setAdapter(new RecyclerAdapterObjects(listOfObjects, searchQuery));
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        pref.registerOnSharedPreferenceChangeListener(this);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        searchView = new SearchView(getActivity());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                RecyclerAdapterObjects adapterObjects = ((RecyclerAdapterObjects) recyclerView.getAdapter());
                if (adapterObjects == null) {
                    return false;
                }
                searchQuery = newText;
                adapterObjects.sortArticles(newText);
                return false;
            }
        });
        changeSearchViewTextColor(searchView);
//        searchView.setIconifiedByDefault(false);
        menu.add(0, 1000, 0, "Поиск");
        MenuItem search = menu.findItem(1000);
        search.setActionView(searchView);
        search.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menuItem = search;
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void update(ArrayList<Article> articles) {
        if (!isAdded()) {
            return;
        }
        loadingIndicator.animate().cancel();
        loadingIndicator.setVisibility(View.GONE);
        if (articles == null) {
            Snackbar.make(recyclerView, "Connection lost", Snackbar.LENGTH_LONG).show();
            return;
        }
        listOfObjects.clear();
        this.listOfObjects.addAll(articles);
        recyclerView.setAdapter(new RecyclerAdapterObjects(listOfObjects, ""));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_ARTICLES, listOfObjects);
        outState.putString(KEY_SEARCH_QUERY, searchQuery);
    }
}