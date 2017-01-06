package ru.kuchanov.scp2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.db.model.Article;
import ru.kuchanov.scp2.mvp.contract.ArticleMvp;
import ru.kuchanov.scp2.ui.adapter.RecyclerAdapterArticle;
import ru.kuchanov.scp2.ui.base.BaseFragment;
import timber.log.Timber;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class ArticleFragment extends BaseFragment<ArticleMvp.View, ArticleMvp.Presenter> implements ArticleMvp.View {

    public static final String TAG = ArticleFragment.class.getSimpleName();

    public static final String EXTRA_URL = "EXTRA_URL";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_ARTICLE = "EXTRA_ARTICLE";

    //tabs
    private static final String KEY_HAS_TABS = "KEY_HAS_TABS";
    private static final String KEY_TABS_TITLE = "KEY_TABS_TITLE";
    private static final String KEY_TABS_TEXT = "KEY_TABS_TEXT";
    private static final String KEY_CURRENT_SELECTED_TAB = "KEY_CURRENT_SELECTED_TAB";

    @BindView(R.id.progressCenter)
    ProgressBar mProgressBarCenter;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    private boolean hasTabs = false;
    List<String> tabsTitles = new ArrayList<>();
    List<String> tabsText = new ArrayList<>();
    int mCurrentSelectedTab = 0;

    private String title;
    private String url;

    private RecyclerAdapterArticle mAdapter;
    private Article mArticle;

    public static ArticleFragment newInstance(String url, String title, @Nullable Article article) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_URL, url);
        args.putString(EXTRA_TITLE, title);
        if (article != null) {
            args.putSerializable(EXTRA_ARTICLE, article);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_ARTICLE, mArticle);

        //tabs
        outState.putBoolean(KEY_HAS_TABS, hasTabs);
        outState.putStringArrayList(KEY_TABS_TITLE, (ArrayList<String>) tabsTitles);
        outState.putStringArrayList(KEY_TABS_TEXT, (ArrayList<String>) tabsText);
        outState.putInt(KEY_CURRENT_SELECTED_TAB, mCurrentSelectedTab);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate");
        super.onCreate(savedInstanceState);
        title = getArguments().getString(EXTRA_TITLE);
        url = getArguments().getString(EXTRA_URL);
        mArticle = getArguments().containsKey(EXTRA_ARTICLE)
                ? (Article) getArguments().getSerializable(EXTRA_ARTICLE)
                : savedInstanceState != null && savedInstanceState.containsKey(EXTRA_ARTICLE)
                ? (Article) savedInstanceState.getSerializable(EXTRA_ARTICLE)
                : null;

        if (savedInstanceState != null) {
            hasTabs = savedInstanceState.getBoolean(KEY_HAS_TABS);
            tabsTitles = savedInstanceState.getStringArrayList(KEY_TABS_TITLE);
            tabsText = savedInstanceState.getStringArrayList(KEY_TABS_TEXT);
            mCurrentSelectedTab = savedInstanceState.getInt(KEY_CURRENT_SELECTED_TAB);
        }
    }

    @Override
    protected int getResId() {
        return R.layout.fragment_article;
    }

    @NonNull
    @Override
    public ArticleMvp.Presenter createPresenter() {
        return mPresenter;
    }

    @Override
    protected void callInjections() {
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    protected void initViews() {
        Timber.d("initViews");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new RecyclerAdapterArticle();
        mRecyclerView.setAdapter(mAdapter);

        mPresenter.setArticleId(url);
        mPresenter.getDataFromDb();

        if (mArticle != null) {
            showData(mArticle);
        }

        mSwipeRefreshLayout.setColorSchemeResources(R.color.zbs_color_red);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            Timber.d("onRefresh");
            mPresenter.getDataFromApi();
        });
    }

    @Override
    public void enableSwipeRefresh(boolean enable) {
        if (!isAdded() || mSwipeRefreshLayout == null) {
            return;
        }
        mSwipeRefreshLayout.setEnabled(enable);
    }

    @Override
    public void showSwipeProgress(boolean show) {
        if (!isAdded()) {
            return;
        }
        if (!mSwipeRefreshLayout.isRefreshing() && !show) {
            return;
        }
        mSwipeRefreshLayout.setRefreshing(show);
    }

    @Override
    public void showCenterProgress(boolean show) {
        if (!isAdded() || mProgressBarCenter == null) {
            return;
        }
        mProgressBarCenter.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showData(Article article) {
        Timber.d("showData: %s", article);
        mArticle = article;
        if (!isAdded()) {
            return;
        }
        if (mArticle == null || mArticle.text == null) {
            return;
        }
        String fullArticlesText = mArticle.text;
        Document document = Jsoup.parse(fullArticlesText);
        Element yuiNavset = document.getElementsByAttributeValueStarting("class", "yui-navset").first();
        if (yuiNavset != null) {
            hasTabs = true;

            Element titles = yuiNavset.getElementsByClass("yui-nav").first();
            Elements liElements = titles.getElementsByTag("li");
            Element yuiContent = yuiNavset.getElementsByClass("yui-content").first();

            tabsText.clear();
            for (Element tab : yuiContent.children()) {
                tabsText.add(tab.html());
            }
            tabsTitles.clear();
            for (Element li : liElements) {
                tabsTitles.add(li.text());
            }
            tabLayout.clearOnTabSelectedListeners();
            tabLayout.removeAllTabs();
            for (String title : tabsTitles) {
                tabLayout.addTab(tabLayout.newTab().setText(title));
            }
            tabLayout.setVisibility(View.VISIBLE);

            Article currentTabArticle = new Article();
            currentTabArticle.text = tabsText.get(mCurrentSelectedTab);
            mAdapter.setData(currentTabArticle);

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    Timber.d("onTabSelected: %s", tab.getPosition());
                    mCurrentSelectedTab = tab.getPosition();
                    Article currentTabArticle = new Article();
                    currentTabArticle.text = tabsText.get(mCurrentSelectedTab);
                    mAdapter.setData(currentTabArticle);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            TabLayout.Tab selectedTab = tabLayout.getTabAt(mCurrentSelectedTab);
            if (selectedTab != null) {
                selectedTab.select();
            }
        } else {
            tabLayout.setVisibility(View.GONE);
            mAdapter.setData(mArticle);
        }
    }
}