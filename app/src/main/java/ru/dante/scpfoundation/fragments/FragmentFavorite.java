package ru.dante.scpfoundation.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import com.vk.sdk.VKSdk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.dante.scpfoundation.Article;
import ru.dante.scpfoundation.Const;
import ru.dante.scpfoundation.utils.DividerItemDecoration;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.adapters.RecyclerAdapterFavorites;
import ru.dante.scpfoundation.utils.VKUtils;
import ru.dante.scpfoundation.utils.parsing.DownloadObjects;

/**
 * Created for MyApplication by Dante on 16.01.2016  21:03.
 */
public class FragmentFavorite extends Fragment implements DownloadObjects.UpdateArticlesList, SharedPreferences.OnSharedPreferenceChangeListener
{
    SearchView searchView;
    MenuItem menuItem;
    Menu menu;
    RecyclerView recyclerView;
    String url;
    String searchQuery = "";
    static final String KEY_SEARCH_QUERY = "KEY_SEARCH_QUERY";
    private static final String LOG = FragmentFavorite.class.getSimpleName();
    private ArrayList<Article> listOfObjects = new ArrayList<>();
    public static final String KEY_ARTICLES = "KEY_ARTICLES";


    private void uploadFavorites()
    {
        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                makeRequest();
            }
        };
        thread.start();
    }

    private void downloadFavorites()
    {
        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                String answer = null;

                String url = "http://kuchanov.ru/scp/download.php";

                Request.Builder request = new Request.Builder();

                RequestBody formBody = new FormBody.Builder()
                        .add(Const.Favorite.SERVER_DB_FIELD_VK_ID, VKSdk.getAccessToken().userId)
                        .build();
                request.post(formBody);

                request.url(url);
                try
                {
                    OkHttpClient client = new OkHttpClient();
                    Response response = client.newCall(request.build()).execute();
                    answer = response.body().string();


                    Log.d(LOG, answer);
                    if (answer.contains("no such vk_id in DB!"))
                    {
                        getActivity().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(getActivity(), "Вы не сохраняли статьи", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                    if (answer.contains("no favorites for vk_id in DB!"))
                    {
                        getActivity().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(getActivity(), "Вы не сохраняли статьи", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                    String[] titles = answer.split(Const.DIVIDER_GROUP)[0].split(Const.DIVIDER);
                    String[] urls = answer.split(Const.DIVIDER_GROUP)[1].split(Const.DIVIDER);
                    final ArrayList<Article> downloadArticles = new ArrayList<>();
                    final SharedPreferences sharedPreferencesFavorites = getActivity().getSharedPreferences(getString(R.string.pref_favorites), Context.MODE_PRIVATE);
                    for (int i = 0; i < titles.length; i++)
                    {
                        Article article = new Article();
                        article.setTitle(titles[i]);
                        article.setURL(urls[i]);
                        downloadArticles.add(article);
                        sharedPreferencesFavorites.edit().putString(urls[i], titles[i]).commit();

                    }

                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            listOfObjects.clear();
                            listOfObjects.addAll(downloadArticles);
                            recyclerView.getAdapter().notifyDataSetChanged();
                            RecyclerAdapterFavorites adapterObjects = (RecyclerAdapterFavorites) recyclerView.getAdapter();
                            adapterObjects.sortArticles("");
                        }
                    });

                } catch (IOException e)
                {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(getActivity(), "Сервер не отвечает", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        };

        thread.start();
    }

    private void makeRequest()
    {
        String answer = null;

        String url = "http://kuchanov.ru/scp/upload.php";

        Request.Builder request = new Request.Builder();
        ArrayList<Article> favoriteArticles = new ArrayList<>();
        final SharedPreferences sharedPreferencesFavorites = getActivity().getSharedPreferences(getString(R.string.pref_favorites), Context.MODE_PRIVATE);
        Set<String> keySet = sharedPreferencesFavorites.getAll().keySet();
        for (String key : keySet)
        {
            Article article = new Article();
            article.setURL(key);
            article.setTitle(sharedPreferencesFavorites.getString(key, ""));
            favoriteArticles.add(article);
        }
        String titles = "";
        String urls = "";
        for (int i = 0; i < favoriteArticles.size(); i++)
        {
            titles += favoriteArticles.get(i).getTitle();
            urls += favoriteArticles.get(i).getURL();
            if (i != favoriteArticles.size() - 1)
            {
                titles += Const.DIVIDER;
                urls += Const.DIVIDER;
            }
        }
        RequestBody formBody = new FormBody.Builder()
                .add(Const.Favorite.SERVER_DB_FIELD_VK_ID, VKSdk.getAccessToken().userId)
                .add(Const.Favorite.SERVER_DB_FIELD_TITLES, titles)
                .add(Const.Favorite.SERVER_DB_FIELD_URLS, urls)
                .build();
        request.post(formBody);

        request.url(url);
        try
        {
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request.build()).execute();
            answer = response.body().string();
            Log.d(LOG, answer);
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(getActivity(), "Синхронизация завершена", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e)
        {
            e.printStackTrace();
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(getActivity(), "Сервер не отвечает", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if (!isAdded())
        {
            return;
        }
        if (key.equals(getString(R.string.pref_design_key_text_size_ui)))
        {
            if (recyclerView.getAdapter() != null)
            {
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }

    /**
     * (Грязный хак исправления цвета текста)
     */
    private void changeSearchViewTextColor(View view)
    {
        if (view != null)
        {
            if (view instanceof TextView)
            {
                ((TextView) view).setTextColor(Color.WHITE);
            } else if (view instanceof ViewGroup)
            {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++)
                {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }

    public static Fragment newInstance(String url)
    {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        Fragment article = new FragmentFavorite();
        article.setArguments(bundle);
        return article;
    }

    public static Fragment newInstance(ArrayList<Article> articles)
    {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_ARTICLES, articles);
        Fragment article = new FragmentFavorite();
        article.setArguments(bundle);
        return article;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        Log.d(LOG, "on create view called");
        View v = inflater.inflate(R.layout.fragment_objects, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        FloatingActionButton searchButton = (FloatingActionButton) v.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
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
        if (arguments.containsKey("url"))
        {
            url = arguments.getString("url");
        } else if (arguments.containsKey(KEY_ARTICLES))
        {
            listOfObjects = arguments.getParcelableArrayList(KEY_ARTICLES);
        }

        if (savedInstanceState != null)
        {
            searchQuery = savedInstanceState.getString(KEY_SEARCH_QUERY);
        }

        if (savedInstanceState != null)
        {
            listOfObjects = savedInstanceState.getParcelableArrayList(KEY_ARTICLES);

        }

//        if (listOfObjects.size() == 0 && url != null)
//        {
//            DownloadObjects downloadObjects = new DownloadObjects(url, recyclerView, this);
//            downloadObjects.execute();
//        } else
//        {
        recyclerView.setAdapter(new RecyclerAdapterFavorites(listOfObjects, searchQuery, recyclerView));
//        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        pref.registerOnSharedPreferenceChangeListener(this);
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case 1100:
                if (VKSdk.isLoggedIn())
                {
                    uploadFavorites();
                } else
                {
                    VKUtils.showLoginDialog(getActivity(), "Синхронизация невозможна\nАвторизуйтесь");
                }
                break;
            case 1200:
                if (VKSdk.isLoggedIn())
                {
                    downloadFavorites();
                } else
                {
                    VKUtils.showLoginDialog(getActivity(), "Синхронизация невозможна\nАвторизуйтесь");
                }
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        searchView = new SearchView(getActivity());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                RecyclerAdapterFavorites adapterObjects = ((RecyclerAdapterFavorites) recyclerView.getAdapter());
                if (adapterObjects == null)
                {
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
//        (кнопка upload)
        menu.add(0, 1100, 100, "Загрузить на сервер");
        MenuItem upload = menu.findItem(1100);
        upload.setIcon(R.drawable.ic_cloud_upload_white_48dp);
        upload.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
//        (кнопка download)
        menu.add(0, 1200, 200, "Скачать с сервера");
        MenuItem download = menu.findItem(1200);
        download.setIcon(R.drawable.ic_cloud_download_white_48dp);
        download.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);


        menuItem = search;
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void update(ArrayList<Article> articles)
    {
        this.listOfObjects.addAll(articles);
        recyclerView.setAdapter(new RecyclerAdapterFavorites(listOfObjects, "", recyclerView));
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_ARTICLES, listOfObjects);
        outState.putString(KEY_SEARCH_QUERY, searchQuery);
    }
}
