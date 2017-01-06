package ru.kuchanov.scp2.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;

import butterknife.BindView;
import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.db.model.Article;
import ru.kuchanov.scp2.db.model.RealmString;
import ru.kuchanov.scp2.mvp.contract.ArticleMvp;
import ru.kuchanov.scp2.ui.adapter.RecyclerAdapterArticle;
import ru.kuchanov.scp2.ui.base.BaseFragment;
import ru.kuchanov.scp2.ui.util.SetTextViewHTML;
import ru.kuchanov.scp2.util.DialogUtils;
import timber.log.Timber;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class ArticleFragment
        extends BaseFragment<ArticleMvp.View, ArticleMvp.Presenter>
        implements ArticleMvp.View, SetTextViewHTML.TextItemsClickListener {

    public static final String TAG = ArticleFragment.class.getSimpleName();

    public static final String EXTRA_URL = "EXTRA_URL";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_ARTICLE = "EXTRA_ARTICLE";

    //tabs
    private static final String KEY_CURRENT_SELECTED_TAB = "KEY_CURRENT_SELECTED_TAB";

    @BindView(R.id.progressCenter)
    ProgressBar mProgressBarCenter;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    //tabs
    private int mCurrentSelectedTab = 0;

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
        mAdapter.setTextItemsClickListener(this);
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
        if (mArticle.hasTabs) {
            tabLayout.clearOnTabSelectedListeners();
            tabLayout.removeAllTabs();
            for (String title : RealmString.toStringList(article.tabsTitles)) {
                tabLayout.addTab(tabLayout.newTab().setText(title));
            }
            tabLayout.setVisibility(View.VISIBLE);

            Article currentTabArticle = new Article();
            currentTabArticle.hasTabs = true;
            currentTabArticle.text = RealmString.toStringList(mArticle.tabsTexts).get(mCurrentSelectedTab);
            mAdapter.setData(currentTabArticle);

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    Timber.d("onTabSelected: %s", tab.getPosition());
                    mCurrentSelectedTab = tab.getPosition();
                    Article currentTabArticle = new Article();
                    currentTabArticle.hasTabs = true;
                    currentTabArticle.text = RealmString.toStringList(mArticle.tabsTexts).get(mCurrentSelectedTab);
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

    @Override
    public void onLinkClicked(String link) {
        //TODO implement open predefined main activities link clicked
//                for (String pressedLink : Constants.Urls.ALL_LINKS_ARRAY) {
//                    if (link.equals(pressedLink)) {
//                        ActivityMain.startActivityMain(link, ctx);
//                        return;
//                    }
//                }

        //TODO start new activity
        showError(new IllegalStateException("not implemented yet"));
    }

    @Override
    public void onSnoskaClicked(String link) {
        List<String> articlesTextParts = mAdapter.getArticlesTextParts();
        if (TextUtils.isDigitsOnly(link)) {
            String linkToFind = "footnote-" + link;
            for (int i = articlesTextParts.size() - 1; i >= 0; i--) {
                Document document = Jsoup.parse(articlesTextParts.get(i));
                Elements divTag = document.getElementsByAttributeValue("id", linkToFind);
                if (divTag.size() != 0) {
                    divTag.first().getElementsByTag("pizda").first().remove();
                    String textThatWeTryToFindSoManyTime = divTag.text();
                    textThatWeTryToFindSoManyTime = textThatWeTryToFindSoManyTime.substring(3, textThatWeTryToFindSoManyTime.length());
                    new MaterialDialog.Builder(getActivity())
                            .title("Сноска " + link)
                            .content(textThatWeTryToFindSoManyTime)
                            .show();
                    break;
                }
            }
        }
    }

    @Override
    public void onBibliographyClicked(String link) {
        List<String> articlesTextParts = mAdapter.getArticlesTextParts();
        for (int i = articlesTextParts.size() - 1; i >= 0; i--) {
            Document document = Jsoup.parse(articlesTextParts.get(i));
            Elements divTag = document.getElementsByAttributeValue("id", link);
            if (divTag.size() != 0) {
                String textThatWeTryToFindSoManyTime = divTag.text();
                textThatWeTryToFindSoManyTime = textThatWeTryToFindSoManyTime.substring(3, textThatWeTryToFindSoManyTime.length());
                new MaterialDialog.Builder(getActivity())
                        .title("Библиография")
                        .content(textThatWeTryToFindSoManyTime)
                        .show();
                break;
            }
        }
    }

    @Override
    public void onTocClicked(String link) {
        List<String> articlesTextParts = mAdapter.getArticlesTextParts();
        String digits = "";
        for (char c : link.toCharArray()) {
            if (TextUtils.isDigitsOnly(String.valueOf(c))) {
                digits += String.valueOf(c);
            }
        }
        for (int i = 0; i < articlesTextParts.size(); i++) {
            if (articlesTextParts.get(i).contains("id=\"" + "toc" + digits + "\"")) {
//                (i+1 так как в адаптере есть еще элемент для заголовка)
                mRecyclerView.scrollToPosition(i + 1);
                return;
            }
        }
    }

    @Override
    public void onImageClicked(String link) {
        DialogUtils.showImageDialog(getActivity(), link);
    }

    @Override
    public void onUnsupportedLinkPressed(String link) {
        Snackbar.make(root, R.string.unsupported_link, Snackbar.LENGTH_SHORT).show();
    }
}