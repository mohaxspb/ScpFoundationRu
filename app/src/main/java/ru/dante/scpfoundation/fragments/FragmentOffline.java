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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import ru.dante.scpfoundation.Article;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.ServiceDownloadAll;
import ru.dante.scpfoundation.adapters.RecyclerAdapterObjects;
import ru.dante.scpfoundation.utils.DividerItemDecoration;
import ru.dante.scpfoundation.utils.parsing.DownloadObjects;

/**
 * Created by Dante on 16.01.2016 21:45.
 * For MyApplication.
 */
public class FragmentOffline extends Fragment implements DownloadObjects.UpdateArticlesList, SharedPreferences.OnSharedPreferenceChangeListener
{
    public static final String KEY_ARTICLES = "KEY_ARTICLES";
    static final String KEY_SEARCH_QUERY = "KEY_SEARCH_QUERY";
    private static final String LOG = FragmentOffline.class.getSimpleName();
    SearchView searchView;
    MenuItem menuItem;
    Menu menu;
    RecyclerView recyclerView;
    String url;
    String searchQuery = "";
    private Context ctx;
    private ArrayList<Article> listOfObjects = new ArrayList<>();

    public static Fragment newInstance(ArrayList<Article> articles)
    {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_ARTICLES, articles);
        Fragment article = new FragmentOffline();
        article.setArguments(bundle);
        return article;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        this.ctx = context;
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
                return;
            }
            else if (view instanceof ViewGroup)
            {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++)
                {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
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
        }
        else if (arguments.containsKey(KEY_ARTICLES))
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

        if (listOfObjects.size() == 0 && url != null)
        {
            DownloadObjects downloadObjects = new DownloadObjects(url, this, ctx);
            downloadObjects.execute();
        }
        else
        {
//            RecyclerAdapterArticle adapterArticle= new RecyclerAdapterArticle(articleText);
            recyclerView.setAdapter(new RecyclerAdapterObjects(listOfObjects, searchQuery));
        }
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
                MaterialDialog materialDialog;
                materialDialog = new MaterialDialog.Builder(ctx)
                        .title(R.string.download_all_title)
                        .items(R.array.download_types)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
                        {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text)
                            {
                                Log.i(LOG, "which: " + which + " text: " + text);
                                if (!ServiceDownloadAll.isRunning())
                                {
                                    dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                }
                                return true;
                            }
                        })
                        .alwaysCallSingleChoiceCallback()
                        .positiveText(R.string.download)
                        .negativeText(R.string.cancel)
                        .autoDismiss(false)
                        .onNegative(new MaterialDialog.SingleButtonCallback()
                        {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which)
                            {
                                Log.i(LOG, "onNegative clicked");
                                dialog.dismiss();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback()
                        {
                            public static final int TYPE_OBJ_1 = 0;
                            public static final int TYPE_OBJ_2 = 1;
                            public static final int TYPE_OBJ_3 = 2;
                            public static final int TYPE_OBJ_RU = 3;
                            public static final int TYPE_ALL = 4;

                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which)
                            {
                                Log.i(LOG, "onPositive clicked");
                                Log.i(LOG, "dialog.getSelectedIndex(): " + dialog.getSelectedIndex());
                                ServiceDownloadAll.DownloadTypes type;
                                switch (dialog.getSelectedIndex())
                                {

                                    case TYPE_OBJ_1:
                                        type = ServiceDownloadAll.DownloadTypes.Type1;
                                        break;
                                    case TYPE_OBJ_2:
                                        type = ServiceDownloadAll.DownloadTypes.Type2;
                                        break;
                                    case TYPE_OBJ_3:
                                        type = ServiceDownloadAll.DownloadTypes.Type3;
                                        break;
                                    case TYPE_OBJ_RU:
                                        type = ServiceDownloadAll.DownloadTypes.TypeRu;
                                        break;
                                    default:
                                    case TYPE_ALL:
                                        type = ServiceDownloadAll.DownloadTypes.TypeAll;
                                        break;
                                }
                                ServiceDownloadAll.startDownloadWithType(ctx, type);
                                dialog.dismiss();
                            }
                        })
                        .neutralText(R.string.stop_download)
                        .onNeutral(new MaterialDialog.SingleButtonCallback()
                        {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which)
                            {
                                Log.i(LOG, "onNeutral clicked");
                                ServiceDownloadAll.stopDownload(ctx);
                                dialog.dismiss();
                            }
                        })
                        .build();

                materialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);

                if (ServiceDownloadAll.isRunning())
                {
                    materialDialog.getActionButton(DialogAction.NEUTRAL).setEnabled(true);
                }
                else
                {
                    materialDialog.getActionButton(DialogAction.NEUTRAL).setEnabled(false);
                }

                materialDialog.show();
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
                RecyclerAdapterObjects adapterObjects = ((RecyclerAdapterObjects) recyclerView.getAdapter());
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
/*
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
*/


        //        (кнопка upload)
        menu.add(0, 1100, 100, "Скачать всё");
        MenuItem upload = menu.findItem(1100);
        upload.setIcon(R.drawable.ic_get_app_white_48dp);
        upload.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

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
        recyclerView.setAdapter(new RecyclerAdapterObjects(listOfObjects, ""));
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
//        outState.putString("url", url);
//        articleText= Html.toHtml((Spanned) textView.getText());
        outState.putParcelableArrayList(KEY_ARTICLES, listOfObjects);
        outState.putString(KEY_SEARCH_QUERY, searchQuery);
    }
}
