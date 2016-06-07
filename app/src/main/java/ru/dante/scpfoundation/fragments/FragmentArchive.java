package ru.dante.scpfoundation.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import ru.dante.scpfoundation.Article;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.adapters.RecyclerAdapterObjects;
import ru.dante.scpfoundation.otto.BusProvider;
import ru.dante.scpfoundation.otto.EventArticleDownloaded;
import ru.dante.scpfoundation.utils.DividerItemDecoration;
import ru.dante.scpfoundation.utils.parsing.DownloadArchive;

/**
 * Created for MyApplication by Dante on 11.04.2016  22:53.
 */
public class FragmentArchive extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Article>>
{
    public static final String KEY_URL = "KEY_URL";
    private static final String LOG = FragmentArchive.class.getSimpleName();
    private Context ctx;
    private String url;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ArrayList<Article> articles = new ArrayList<>();

    @Override
    public void onResume()
    {
        super.onResume();
        if (articles == null || articles.size() == 0)
        {
            //workaround from
            //http://stackoverflow.com/a/26910973/3212712
            swipeRefreshLayout.post(new Runnable()
            {
                @Override
                public void run()
                {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
//            loader.forceLoad();
//            loader.startLoading();
//            loader.
        }
    }

    private Loader<ArrayList<Article>> loader;

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Article.KEY_ARTICLE, articles);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(LOG, "on create view called");
        View v = inflater.inflate(R.layout.fragment_jokes, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setEnabled(false);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
//        {
//            @Override
//            public void onRefresh()
//            {
//                //workaround from
//                //http://stackoverflow.com/a/26910973/3212712
//                swipeRefreshLayout.post(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        swipeRefreshLayout.setRefreshing(true);
//                    }
//                });
//                loader.reset();
//                loader.onContentChanged();
//            }
//        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        this.url = bundle.getString(KEY_URL);
        Bundle args = new Bundle();
        args.putString(KEY_URL, url);
        loader = getLoaderManager().initLoader(0, args, this);
        if (savedInstanceState != null)
        {
            articles = savedInstanceState.getParcelableArrayList(Article.KEY_ARTICLE);
        }
        setHasOptionsMenu(true);
    }

    public static Fragment newInstanse(String url)
    {
        Fragment fragment = new FragmentArchive();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        this.ctx = context;
    }

    @Override
    public Loader<ArrayList<Article>> onCreateLoader(int id, Bundle args)
    {
        return new DownloadArchive(ctx, args);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Article>> loader, ArrayList<Article> data)
    {
        //workaround from
        //http://stackoverflow.com/a/26910973/3212712
        swipeRefreshLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        if (data == null)
        {
            Snackbar.make(recyclerView, "Connection lost", Snackbar.LENGTH_LONG).show();
            loader.reset();
            loader.onContentChanged();
            return;
        }
        articles.clear();
        articles.addAll(data);
        recyclerView.setAdapter(new RecyclerAdapterObjects(data, ""));
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Article>> loader)
    {

    }

    @Override
    public void onStart()
    {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onArticleDownloaded(EventArticleDownloaded eventArticleDownloaded)
    {
        for (int i = 0; i < articles.size(); i++)
        {
            if (articles.get(i).getURL().equals(eventArticleDownloaded.getLink()))
            {
                recyclerView.getAdapter().notifyItemChanged(i);
                break;
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == 1000)
        {
            new MaterialDialog.Builder(ctx)
                    .title("О Разделе")
//                    .content(Html.fromHtml(getString(R.string.about_archive)))
                    .content(R.string.about_archive)
                    .positiveText(R.string.cancel)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        menu.add(0, 1000, 0, "О разделе");
        MenuItem share = menu.findItem(1000);
        share.setIcon(R.drawable.ic_info_outline_white_48dp);
        share.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        super.onCreateOptionsMenu(menu, inflater);
    }
}
