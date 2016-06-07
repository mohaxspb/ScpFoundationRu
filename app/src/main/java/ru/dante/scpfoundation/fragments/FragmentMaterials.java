package ru.dante.scpfoundation.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import ru.dante.scpfoundation.utils.DividerItemDecoration;
import ru.dante.scpfoundation.utils.parsing.DownloadProtocols;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.adapters.RecyclerAdapterProtocols;
import ru.dante.scpfoundation.otto.BusProvider;
import ru.dante.scpfoundation.otto.EventArticleDownloaded;

/**
 * Created for MyApplication by Dante on 16.01.2016  22:31.
 */
public class FragmentMaterials extends Fragment implements DownloadProtocols.UpdateProtocol, SharedPreferences.OnSharedPreferenceChangeListener
{
    private RecyclerView recyclerView;
    private static final String LOG = FragmentMaterials.class.getSimpleName();
    private ArrayList<String> listOfProtocols = new ArrayList<>();
    public static final String KEY_PROTOCOLS = "KEY_PROTOCOLS";
    private String url;

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
        for (int i = 0; i < listOfProtocols.size(); i++)
        {
            if (listOfProtocols.get(i).contains(eventArticleDownloaded.getLink()))
            {
                recyclerView.getAdapter().notifyItemChanged(i);
                break;
            }
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

    public static Fragment createFragment(String url)
    {
        Fragment fragment = new FragmentMaterials();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(LOG, "on create view called");
        View v = inflater.inflate(R.layout.fragment_article, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        Bundle arguments = this.getArguments();
        url = arguments.getString("url");

        if (savedInstanceState != null)
        {
            listOfProtocols = savedInstanceState.getStringArrayList(KEY_PROTOCOLS);
            if(listOfProtocols==null)
            {
                listOfProtocols =new ArrayList<>();
            }
            url = savedInstanceState.getString("url");
        }

        if (listOfProtocols.size() == 0)
        {
            DownloadProtocols downloadProtocols = new DownloadProtocols(url, this);
            downloadProtocols.execute();
        } else

        {
            recyclerView.setAdapter(new RecyclerAdapterProtocols(listOfProtocols));
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

    @Override
    public void update(ArrayList<String> articles)
    {
//        this.listOfProtocols.addAll(articles);
        if (!isAdded())
        {
            return;
        }
        if (articles == null)
        {
            Log.e(LOG, "Connection lost");
            Snackbar.make(recyclerView, "Connection lost", Snackbar.LENGTH_LONG).show();
        } else
        {
            listOfProtocols.clear();
            this.listOfProtocols.addAll(articles);
            recyclerView.setAdapter(new RecyclerAdapterProtocols(listOfProtocols));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(KEY_PROTOCOLS, listOfProtocols);
        outState.putString("url", url);
    }
}