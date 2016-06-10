package ru.dante.scpfoundation.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import ru.dante.scpfoundation.Article;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.adapters.RecyclerAdapterNewArticles;
import ru.dante.scpfoundation.otto.BusProvider;
import ru.dante.scpfoundation.otto.EventAppInstall;
import ru.dante.scpfoundation.otto.EventArticleDownloaded;
import ru.dante.scpfoundation.otto.EventGiveMeMoney;
import ru.dante.scpfoundation.utils.AttributeGetter;
import ru.dante.scpfoundation.utils.DividerItemDecoration;
import ru.dante.scpfoundation.utils.RecyclerViewOnScrollListener;
import ru.dante.scpfoundation.utils.ScreenProperties;
import ru.dante.scpfoundation.utils.parsing.DownloadNewArticles;

/**
 * Created by Dante on 16.01.2016.
 */
public class FragmentNewArticles extends Fragment implements DownloadNewArticles.UpdateArticlesList, SharedPreferences.OnSharedPreferenceChangeListener
{
    public static final String LOG = FragmentNewArticles.class.getSimpleName();
    private final static String KEY_CURRENT_PAGE_TO_LOAD = "KEY_CURRENT_PAGE_TO_LOAD";
    SwipeRefreshLayout swipeRefreshLayout;
    boolean isLoadingFromTop = true;
    Context ctx;

    @Override
    public void onAttach(Context context)
    {
        this.ctx = context;
        super.onAttach(context);
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
    public void onAppInstallNeedToShow(EventAppInstall eventAppInstall)
    {
        ((RecyclerAdapterNewArticles)recyclerView.getAdapter()).showAppInstall();
    }

    @Subscribe
    public void onGiveMeMoneyNeedToShow(EventGiveMeMoney eventGiveMeMoney)
    {
        ((RecyclerAdapterNewArticles)recyclerView.getAdapter()).showGiveMeMoney();
    }

    @Subscribe
    public void onArticleDownloaded(EventArticleDownloaded eventArticleDownloaded)
    {
        for (int i = 0; i < listArticles.size(); i++)
        {
            if (listArticles.get(i).getURL().equals(eventArticleDownloaded.getLink()))
            {
                recyclerView.getAdapter().notifyItemChanged(i);
                break;
            }
        }
    }

    private void setLoading(final boolean isLoading)
    {
        if (isLoading && swipeRefreshLayout.isRefreshing())
        {
//            Log.i(LOG, "isLoading and  swipeRefreshLayout.isRefreshing() are both TRUE, so RETURN!!!");
            return;
        }

        int actionBarSize = AttributeGetter.getDimentionPixelSize(ctx, android.R.attr.actionBarSize);
        if (isLoading)
        {
            if (this.isLoadingFromTop)
            {
                swipeRefreshLayout.setProgressViewEndTarget(false, actionBarSize);
            } else
            {
                int screenHeight = ScreenProperties.getHeight((AppCompatActivity) ctx);
                swipeRefreshLayout.setProgressViewEndTarget(false, screenHeight - actionBarSize * 2);
            }
//            swipeRefreshLayout.setRefreshing(true);
        } else
        {
            swipeRefreshLayout.setProgressViewEndTarget(false, actionBarSize);
//            swipeRefreshLayout.setRefreshing(false);
        }

        //workaround from
        //http://stackoverflow.com/a/26910973/3212712
        swipeRefreshLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                swipeRefreshLayout.setRefreshing(isLoading);
            }
        });
    }

    int currentPageToLoad = 1;
    ArrayList<Article> listArticles = new ArrayList<>();
    RecyclerView recyclerView;

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(LOG, listArticles);
        outState.putInt(KEY_CURRENT_PAGE_TO_LOAD, currentPageToLoad);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        Log.d(LOG, "on create view called");
        if (savedInstanceState != null)
        {
            listArticles = savedInstanceState.getParcelableArrayList(LOG);
            currentPageToLoad = savedInstanceState.getInt(KEY_CURRENT_PAGE_TO_LOAD);
        }

        View v = inflater.inflate(R.layout.fragment_new_articles, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                currentPageToLoad = 1;
                isLoadingFromTop = true;
                DownloadNewArticles downloadNewArticles = new DownloadNewArticles(currentPageToLoad, FragmentNewArticles.this, ctx);
                downloadNewArticles.execute();
                setLoading(true);
            }
        });
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));
        recyclerView.getItemAnimator().setAddDuration(500);
        recyclerView.getItemAnimator().setRemoveDuration(500);
        recyclerView.getItemAnimator().setMoveDuration(500);
        recyclerView.getItemAnimator().setChangeDuration(500);

//        resetOnScrollListener();

        if (listArticles.size() == 0)
        {
            RecyclerAdapterNewArticles recyclerAdapterNewArticles = new RecyclerAdapterNewArticles(listArticles);
            recyclerView.setAdapter(recyclerAdapterNewArticles);
            DownloadNewArticles downloadNewArticles = new DownloadNewArticles(currentPageToLoad, this, ctx);
            downloadNewArticles.execute();
            setLoading(true);
        } else
        {
            RecyclerAdapterNewArticles recyclerAdapterNewArticles = new RecyclerAdapterNewArticles(listArticles);
            recyclerView.setAdapter(recyclerAdapterNewArticles);
            resetOnScrollListener();
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        pref.registerOnSharedPreferenceChangeListener(this);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    private void resetOnScrollListener()
    {
        recyclerView.clearOnScrollListeners();
        recyclerView.addOnScrollListener(new RecyclerViewOnScrollListener()
        {
            @Override
            public void onLoadMore()
            {
                currentPageToLoad++;
                DownloadNewArticles downloadNewArticles = new DownloadNewArticles(currentPageToLoad, FragmentNewArticles.this, ctx);
                downloadNewArticles.execute();
                isLoadingFromTop = false;
                setLoading(true);
            }
        });
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void update(ArrayList<Article> listArticles)
    {
        if (!isAdded())
        {
            return;
        }
        setLoading(false);
        if (listArticles == null)
        {
            Snackbar.make(recyclerView, "Connection lost", Snackbar.LENGTH_LONG).show();
            if (currentPageToLoad > 1)
            {
                currentPageToLoad--;
            }
            resetOnScrollListener();
            return;
        }
        int previousListSize = this.listArticles.size();
        if (currentPageToLoad == 1)
        {
            this.listArticles.clear();
            if (recyclerView.getAdapter() != null)
            {
                recyclerView.getAdapter().notifyItemRangeRemoved(0, previousListSize);
            }
        }
        this.listArticles.addAll(listArticles);
        if (recyclerView.getAdapter() == null)
        {
            RecyclerAdapterNewArticles recyclerAdapterNewArticles = new RecyclerAdapterNewArticles(this.listArticles);
            recyclerView.setAdapter(recyclerAdapterNewArticles);
        } else
        {
            recyclerView.getAdapter().notifyItemRangeInserted(previousListSize, this.listArticles.size());
        }
        resetOnScrollListener();
        int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        if (firstVisibleItemPosition == 0 && lastVisibleItemPosition == listArticles.size() - 1)
        {
            currentPageToLoad++;
            DownloadNewArticles downloadNewArticles = new DownloadNewArticles(currentPageToLoad, FragmentNewArticles.this, ctx);
            downloadNewArticles.execute();
            isLoadingFromTop = false;
            setLoading(true);
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
//        boolean isNotFirstLoading = sharedPreferences.contains("first articles url");
//        if (isNotFirstLoading)
//        {
//            String firstArticleUrl = sharedPreferences.getString("first articles url", "");
//            for (int i = 0; i < listArticles.size(); i++)
//            {
//                String currentArticlesUrl = listArticles.get(i).getURL();
//                if (firstArticleUrl.equals(currentArticlesUrl))
//                {
//                    if (i == 0)
//                    {
//                        Toast.makeText(getActivity(), "Новых станей не обнаружено", Toast.LENGTH_SHORT).show();
//                    } else
//                    {
//                        Toast.makeText(getActivity(), i + " новых статей", Toast.LENGTH_SHORT).show();
//                    }
//                    break;
//                }
//                else {
//                    if (i==listArticles.size()-1){
//                        Log.d(LOG, "обнаружено больше");
//                       Toast.makeText(getActivity(),"Обнаружено более "+ Const.DEFAULT_NUM_OF_ARTICLE_OF_PAGE+" новых статей",Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        }
        sharedPreferences.edit().putString(ctx.getString(R.string.pref_key_first_article_url), listArticles.get(0).getURL()).commit();
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
}
