package ru.dante.scpfoundation.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.otto.Subscribe;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import ru.dante.scpfoundation.Article;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.adapters.RecyclerAdapterArticle;
import ru.dante.scpfoundation.otto.BusProvider;
import ru.dante.scpfoundation.otto.EventBibliographyLinkPress;
import ru.dante.scpfoundation.otto.EventSnoskaLinkPress;
import ru.dante.scpfoundation.otto.EventTocLinkPress;
import ru.dante.scpfoundation.utils.AttributeGetter;
import ru.dante.scpfoundation.utils.FavoriteUtils;
import ru.dante.scpfoundation.utils.OfflineUtils;
import ru.dante.scpfoundation.utils.licensingfuckup.LicenseUtils;
import ru.dante.scpfoundation.utils.parsing.DownloadArticle;

/**
 * Created for MyApplication by Dante on 16.01.2016  19:43.
 */
public class FragmentArticle extends Fragment implements DownloadArticle.SetArticlesText, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String KEY_HAS_TABS = "KEY_HAS_TABS";
    private static final String KEY_TABS_TITLE = "KEY_TABS_TITLE";
    private static final String KEY_TABS_TEXT = "KEY_TABS_TEXT";
    private static final String KEY_CURRENT_SELECTED_TAB = "KEY_CURRENT_SELECTED_TAB";

    private RecyclerView recyclerView;
    private String url;
    private String artTitle;
    private Article article;
    public String LOG = FragmentArticle.class.getSimpleName();
    private ImageView loadingIndicator;
    private boolean hasTabs = false;
    List<String> tabsTitles = new ArrayList<>();
    List<String> tabsText = new ArrayList<>();
    int currentSelectedTab = 0;
    TabLayout tabLayout;
    Context ctx;

    public static Fragment newInstance(String url, String title) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("title", title);
        FragmentArticle article = new FragmentArticle();
        article.LOG += "#" + url;
        article.setArguments(bundle);
        return article;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(LOG, "onSaveInstanceState called");
        super.onSaveInstanceState(outState);
        outState.putString("url", url);
        outState.putString("title", artTitle);
        outState.putParcelable(Article.KEY_ARTICLE, article);
        outState.putBoolean(KEY_HAS_TABS, hasTabs);
        outState.putStringArrayList(KEY_TABS_TITLE, (ArrayList<String>) tabsTitles);
        outState.putStringArrayList(KEY_TABS_TEXT, (ArrayList<String>) tabsText);
        outState.putInt(KEY_CURRENT_SELECTED_TAB, currentSelectedTab);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG, "onCreate called");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState == null) {
            Log.i(LOG, "savedInstanceState == null");
            Bundle arguments = this.getArguments();
            url = arguments.getString("url");
            artTitle = arguments.getString("title");
            article = new Article();
            article.setURL(url);
            article.setTitle(artTitle);
        } else {
            Log.i(LOG, "savedInstanceState != null");
            article = savedInstanceState.getParcelable(Article.KEY_ARTICLE);
            Log.i(LOG, "article is null:" + String.valueOf(article == null));
            url = savedInstanceState.getString("url");
            artTitle = savedInstanceState.getString("title");
            //tabs
            hasTabs = savedInstanceState.getBoolean(KEY_HAS_TABS);
            tabsTitles = savedInstanceState.getStringArrayList(KEY_TABS_TITLE);
            tabsText = savedInstanceState.getStringArrayList(KEY_TABS_TEXT);
            currentSelectedTab = savedInstanceState.getInt(KEY_CURRENT_SELECTED_TAB);
        }
        this.LOG += "#" + url;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG, "on create view called");
        View v = inflater.inflate(R.layout.fragment_article, container, false);
        loadingIndicator = (ImageView) v.findViewById(R.id.loading_indicator);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tabLayout = (TabLayout) v.findViewById(R.id.tabLayout);

        if (OfflineUtils.hasOfflineWithURL(ctx, url)) {
            article.setArticlesText(OfflineUtils.getTextByUrl(ctx, url));
            setArticle(article);
        }
        if (article.getArticlesText() == null) {
            DownloadArticle downloadArticle = new DownloadArticle(url, this);
            downloadArticle.execute();
            loadingIndicator.setVisibility(View.VISIBLE);
            loadingIndicator.animate().setInterpolator(new AccelerateDecelerateInterpolator()).rotationBy(360).setDuration(500).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loadingIndicator.animate().setInterpolator(new AccelerateDecelerateInterpolator()).rotationBy(360).setDuration(500).setListener(this);
                }
            });
        } else {
            Log.e(LOG, "hasTabs: " + hasTabs);
            if (hasTabs) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabLayout.getLayoutParams();
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                tabLayout.setLayoutParams(params);
                Article currentTabArticle = new Article();
                currentTabArticle.setArticlesText(tabsText.get(currentSelectedTab));
                RecyclerAdapterArticle adapterArticle = new RecyclerAdapterArticle(currentTabArticle);
                recyclerView.setAdapter(adapterArticle);
                tabLayout.removeAllTabs();
                for (String title : tabsTitles) {
                    tabLayout.addTab(tabLayout.newTab().setText(title));
                }
                tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        currentSelectedTab = tab.getPosition();
                        Article currentTabArticle = new Article();
                        currentTabArticle.setArticlesText(tabsText.get(currentSelectedTab));
                        RecyclerAdapterArticle adapterArticle = new RecyclerAdapterArticle(currentTabArticle);
                        recyclerView.setAdapter(adapterArticle);
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
            } else {
                RecyclerAdapterArticle adapterArticle = new RecyclerAdapterArticle(article);
                recyclerView.setAdapter(adapterArticle);
            }
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        pref.registerOnSharedPreferenceChangeListener(this);
        return v;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (FavoriteUtils.hasFavoriteWithURL(ctx, url)) {
            MenuItem favorite = menu.findItem(3000);
            favorite.setIcon(AttributeGetter.getDrawableId(getActivity(), R.attr.favoriteIcon));
            favorite.setTitle("Удалить из избранного");
        } else {
            MenuItem favorite = menu.findItem(3000);
            favorite.setIcon(R.drawable.ic_star_border_white_48dp);
            favorite.setTitle("Добавить в избранное");
        }
        if (OfflineUtils.hasOfflineWithURL(ctx, url)) {
            MenuItem offline = menu.findItem(4000);
            offline.setIcon(R.drawable.ic_beenhere_white_48dp);
            offline.setTitle("Удалить из Offline");
        } else {
            MenuItem offline = menu.findItem(4000);
            offline.setIcon(R.drawable.ic_bookmark_border_white_48dp);
            offline.setTitle("Добавить в Offline");
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1000) {
            shareUrl(url, getActivity());
        }
        if (item.getItemId() == 2000) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            Intent chooser = Intent.createChooser(intent, "Открыть с помощью");
            getActivity().startActivity(chooser);
        }
        if (item.getItemId() == 3000) {
            FavoriteUtils.updateFavoritesOnDevice(getActivity(), url, article.getTitle());

        }
        if (item.getItemId() == 4000) {
            OfflineUtils.updateOfflineOnDevice(ctx, url, article.getTitle(), article.getArticlesText(), true);
            ((AppCompatActivity) ctx).supportInvalidateOptionsMenu();
        }
        if (item.getItemId() == 5000) {
            String content;
            String author = LicenseUtils.getAuthorByUrlOrTitle(ctx, article.getURL(), article.getTitle());
            String license = "Распространяется по лицензии <a rel=\"license\" href=\"http://creativecommons.org/licenses/by-nc-sa/3.0/\">Creative Commons Attribution-NonCommercial-ShareAlike 3.0 License</a>";
            content = author + "<br><br>" + license;
            new MaterialDialog.Builder(ctx)
                    .title("Информация о статье")
                    .positiveText("Закрыть")
                    .content(Html.fromHtml(content))
                    .show();
            Log.i(LOG, "url: " + article.getURL() + " title: " + article.getTitle());
            Log.i(LOG, author);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, 1000, 0, "Поделиться");
        MenuItem share = menu.findItem(1000);
        share.setIcon(R.drawable.ic_share_blue_grey_50_48dp);
        share.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//        (кнопка открыть в браузере)
        menu.add(0, 2000, 0, "Открыть в браузере");
        MenuItem browser = menu.findItem(2000);
        browser.setIcon(R.drawable.ic_open_in_browser_blue_grey_50_48dp);
        browser.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        /*Кнопка "довить в избраное*/
        menu.add(0, 3000, 0, "Добавить в избраное");
        MenuItem favorite = menu.findItem(3000);
        favorite.setIcon(AttributeGetter.getDrawableId(ctx, R.attr.favoriteIconUnselected));
        favorite.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        /*Кнопка "Сохранить офлайн*/
        menu.add(0, 4000, 0, "Добавить в офлайн");
        MenuItem offline = menu.findItem(4000);
        offline.setIcon(R.drawable.ic_bookmark_border_white_48dp);
        offline.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(0, 5000, 0, "О статье");
        MenuItem aboutArticle = menu.findItem(5000);
        aboutArticle.setIcon(R.drawable.ic_assignment_late_white_48dp);
        aboutArticle.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void setArticle(Article article) {
        if (!isAdded()) {
            return;
        }
        loadingIndicator.animate().cancel();
        loadingIndicator.setVisibility(View.GONE);
        if (article == null) {
            Snackbar.make(recyclerView, "Connection lost", Snackbar.LENGTH_LONG).show();
            return;
        }
        this.article = article;
        String fullArticlesText = this.article.getArticlesText();
        Document document = Jsoup.parse(fullArticlesText);
        Element yuiNavset = document.getElementsByAttributeValueStarting("class", "yui-navset").first();
        if (yuiNavset != null) {
            hasTabs = true;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabLayout.getLayoutParams();
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            tabLayout.setLayoutParams(params);
            Element titles = yuiNavset.getElementsByClass("yui-nav").first();
            Elements liElements = titles.getElementsByTag("li");
            tabsTitles.clear();
            tabsText.clear();
            tabLayout.removeAllTabs();
            for (Element li : liElements) {
                tabsTitles.add(li.text());
                tabLayout.addTab(tabLayout.newTab().setText(li.text()));
            }
            Element yuiContent = yuiNavset.getElementsByClass("yui-content").first();
            for (Element tab : yuiContent.children()) {
                tabsText.add(tab.html());
            }
            Article currentTabArticle = new Article();
            currentTabArticle.setArticlesText(tabsText.get(currentSelectedTab));
            RecyclerAdapterArticle adapterArticle = new RecyclerAdapterArticle(currentTabArticle);
            recyclerView.setAdapter(adapterArticle);
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    currentSelectedTab = tab.getPosition();
                    Article currentTabArticle = new Article();
                    currentTabArticle.setArticlesText(tabsText.get(currentSelectedTab));
                    RecyclerAdapterArticle adapterArticle = new RecyclerAdapterArticle(currentTabArticle);
                    recyclerView.setAdapter(adapterArticle);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        } else {
            RecyclerAdapterArticle adapterArticle = new RecyclerAdapterArticle(this.article);
            recyclerView.setAdapter(adapterArticle);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onTocPress(EventTocLinkPress tocLinkPress) {
        RecyclerAdapterArticle recyclerAdapterArticle = (RecyclerAdapterArticle) recyclerView.getAdapter();
        if (recyclerAdapterArticle == null) {
            return;
        }
        ArrayList<String> articlesTextParts = recyclerAdapterArticle.getArticlesTextParts();
        String digits = "";
        for (char c : tocLinkPress.getLink().toCharArray()) {
            if (TextUtils.isDigitsOnly(String.valueOf(c))) {
                digits += String.valueOf(c);
            }
        }
        for (int i = 0; i < articlesTextParts.size(); i++) {
            if (articlesTextParts.get(i).contains("id=\"" + "toc" + digits + "\"")) {
//                (i+1 так как в адапторе есть еще элемент для заголовка)
                recyclerView.scrollToPosition(i + 1);
                return;
            }
        }
    }
    @SuppressWarnings("unused")
    @Subscribe
    public void onSnoskaPress(EventSnoskaLinkPress snoskaLinkPress) {
        RecyclerAdapterArticle recyclerAdapterArticle = (RecyclerAdapterArticle) recyclerView.getAdapter();
        if (recyclerAdapterArticle == null) {
            return;
        }
        ArrayList<String> articlesTextParts = recyclerAdapterArticle.getArticlesTextParts();
        String link = snoskaLinkPress.getLink();
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

    @SuppressWarnings("unused")
    @Subscribe
    public void onBibliographyPress(EventBibliographyLinkPress bibliographyLinkPress) {
        RecyclerAdapterArticle recyclerAdapterArticle = (RecyclerAdapterArticle) recyclerView.getAdapter();
        if (recyclerAdapterArticle == null) {
            return;
        }
        ArrayList<String> articlesTextParts = recyclerAdapterArticle.getArticlesTextParts();
        String link = bibliographyLinkPress.getLink();
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (!isAdded()) {
            return;
        }
        if (key.equals(getString(R.string.pref_design_key_text_size_article))) {
            if (recyclerView.getAdapter() != null) {
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }
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


    @Override
    public void onAttach(Context context) {
        ctx = context;
        super.onAttach(context);
    }

    public static void shareUrl(String url, Context ctx) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        //add link to app
        String fullMessage = url
                + "\n\n"
                + "Отправлено с помощью приложения \"SCP Foundation\"\nhttps://play.google.com/store/apps/details?id=ru.dante.scpfoundation";
        sendIntent.putExtra(Intent.EXTRA_TEXT, fullMessage);
        sendIntent.setType("text/plain");
        ctx.startActivity(Intent.createChooser(sendIntent, ctx.getResources().getText(R.string.share_link)));
    }
}