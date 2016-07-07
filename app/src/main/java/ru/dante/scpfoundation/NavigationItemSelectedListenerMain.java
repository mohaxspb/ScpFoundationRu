package ru.dante.scpfoundation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import ru.dante.scpfoundation.activities.ActivityArticles;
import ru.dante.scpfoundation.activities.ActivityGallery;
import ru.dante.scpfoundation.activities.ActivityMain;
import ru.dante.scpfoundation.fragments.FragmentArticle;
import ru.dante.scpfoundation.fragments.FragmentFavorite;
import ru.dante.scpfoundation.fragments.FragmentMaterialsAll;
import ru.dante.scpfoundation.fragments.FragmentNewArticles;
import ru.dante.scpfoundation.fragments.FragmentObjects;
import ru.dante.scpfoundation.fragments.FragmentOffline;
import ru.dante.scpfoundation.fragments.FragmentRateArticles;
import ru.dante.scpfoundation.fragments.FragmentSearch;
import ru.dante.scpfoundation.utils.RandomPage;


/**
 * Created for SCP Eng by Dante on 27.03.2016  18:03.
 */
public class NavigationItemSelectedListenerMain implements NavigationView.OnNavigationItemSelectedListener
{
    private static final String LOG = NavigationItemSelectedListenerMain.class.getSimpleName();
    ActivityMain activityMain;
    private SharedPreferences pref;

    public NavigationItemSelectedListenerMain(ActivityMain activityMain)
    {
        this.activityMain = activityMain;
        pref = PreferenceManager.getDefaultSharedPreferences(activityMain);
    }

    public static String getTitleById(Integer id)
    {
        String title = "";
        if (id == null)
        {
            return "Материалы";
        }
        switch (id)
        {
            case R.id.news:
                title = "Новости";
                break;
            case R.id.rate_articles:
                title = "Статьи по рейтингу";
                break;
            case R.id.new_articles:
                title = "Новые статьи";
                break;
            case R.id.about:
                title = "Об организации";
                break;
            case R.id.objects_I:
                title = "Объекты I";
                break;
            case R.id.objects_II:
                title = "Объекты II";
                break;
            case R.id.objects_III:
                title = "Объекты III";
                break;
            case R.id.objects_RU:
                title = "Объекты RU";
                break;
            case R.id.favorite:
                title = "Избранное";
                break;
            case R.id.offline:
                title = "Офлайн";
                break;
            case R.id.stories:
                title = "Рассказы";
                break;
            case R.id.files:
                title = "Материалы";
                break;
            case R.id.site_search:
                title = "Поиск";
                break;
        }
        return title;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem)
    {
        int size = activityMain.getListOfDrawerMenuPressedIds().size();
        if (size != 0
                && activityMain.getListOfDrawerMenuPressedIds().get(size - 1) != null
                && menuItem.getItemId() == activityMain.getListOfDrawerMenuPressedIds().get(size - 1))
        {
            activityMain.getDrawerLayout().closeDrawers();
            return true;
        }
        FragmentManager fragmentManager = activityMain.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment;
        String fragmentName;

        if (menuItem.getItemId() != R.id.gallery && menuItem.getItemId() != R.id.random_page)
        {
            activityMain.addIdtoToListOfDrawerMenuPressedIds(menuItem.getItemId());
        }

        switch (menuItem.getItemId())
        {
            case R.id.news:
                fragmentName = "news";
                fragment = fragmentManager.findFragmentByTag(fragmentName);
                if (fragment == null)
                {
                    fragment = FragmentArticle.newInstance(Const.Urls.NEWS, "Новости");
                }
                fragmentTransaction.replace(R.id.content_frame, fragment, fragmentName);
                fragmentTransaction.addToBackStack(fragmentName);
                fragmentTransaction.commit();
                break;
            case R.id.new_articles:
                Log.d(LOG, "новые статьи нажаты");

                fragment = new FragmentNewArticles();
                fragmentName = ((FragmentNewArticles) fragment).LOG;
                fragment = fragmentManager.findFragmentByTag(fragmentName);
                if (fragment == null)
                {
                    fragment = new FragmentNewArticles();
                }
                fragmentTransaction.replace(R.id.content_frame, fragment, fragmentName);
                fragmentTransaction.addToBackStack(fragmentName);
                fragmentTransaction.commit();
                break;
            case R.id.rate_articles:
                Log.d(LOG, "rate articles click");

                fragment = new FragmentRateArticles();
                fragmentName = ((FragmentRateArticles) fragment).LOG;
                fragment = fragmentManager.findFragmentByTag(fragmentName);
                if (fragment == null)
                {
                    fragment = new FragmentRateArticles();
                }
                fragmentTransaction.replace(R.id.content_frame, fragment, fragmentName);
                fragmentTransaction.addToBackStack(fragmentName);
                fragmentTransaction.commit();
                break;
            case R.id.about:
                fragment = FragmentArticle.newInstance("http://scpfoundation.ru/about-the-scp-foundation", "Об организации");
                fragmentName = ((FragmentArticle) fragment).LOG;
                fragment = fragmentManager.findFragmentByTag(fragmentName);
                if (fragment == null)
                {
                    fragment = FragmentArticle.newInstance(Const.Urls.ABOUT_SCP, "Об организации");
                }
                fragmentTransaction.replace(R.id.content_frame, fragment, fragmentName);
                fragmentTransaction.addToBackStack(fragmentName);
                fragmentTransaction.commit();
                break;
            case R.id.objects_I:
                fragmentName = "http://scpfoundation.ru/scp-list";

                fragment = fragmentManager.findFragmentByTag(fragmentName);
                if (fragment == null)
                {
                    fragment = FragmentObjects.newInstance(Const.Urls.OBJECTS_1);
                }
                fragmentTransaction.replace(R.id.content_frame, fragment, fragmentName);
                fragmentTransaction.addToBackStack(fragmentName);
                fragmentTransaction.commit();
                break;
            case R.id.objects_II:
                fragmentName = "http://scpfoundation.ru/scp-list-2";
                fragment = fragmentManager.findFragmentByTag(fragmentName);
                if (fragment == null)
                {
                    fragment = FragmentObjects.newInstance(Const.Urls.OBJECTS_2);
                }
                fragmentTransaction.replace(R.id.content_frame, fragment, fragmentName);
                fragmentTransaction.addToBackStack(fragmentName);
                fragmentTransaction.commit();
                break;
            case R.id.objects_III:
                fragmentName = "http://scpfoundation.ru/scp-list-3";
                fragment = fragmentManager.findFragmentByTag(fragmentName);
                if (fragment == null)
                {
                    fragment = FragmentObjects.newInstance(Const.Urls.OBJECTS_3);
                }
                fragmentTransaction.replace(R.id.content_frame, fragment, fragmentName);
                fragmentTransaction.addToBackStack(fragmentName);
                fragmentTransaction.commit();
                break;
            case R.id.objects_RU:
                fragmentName = "http://scpfoundation.ru/scp-list-ru";
                fragment = fragmentManager.findFragmentByTag(fragmentName);
                if (fragment == null)
                {
                    fragment = FragmentObjects.newInstance(Const.Urls.OBJECTS_RU);
                }
                fragmentTransaction.replace(R.id.content_frame, fragment, fragmentName);
                fragmentTransaction.addToBackStack(fragmentName);
                fragmentTransaction.commit();
                break;
            case R.id.random_page:
                if (pref.contains(activityMain.getString(R.string.pref_key_random_url)))
                {
                    Intent intent = new Intent(activityMain, ActivityArticles.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title", "");
                    bundle.putString("url", pref.getString(activityMain.getString(R.string.pref_key_random_url), ""));
                    intent.putExtras(bundle);
                    activityMain.startActivity(intent);
                    pref.edit().remove(activityMain.getString(R.string.pref_key_random_url)).apply();
                    RandomPage.getRandomPage(activityMain);
                } else
                {
                    RandomPage.getRandomPage(activityMain);
                    Toast.makeText(activityMain, "Создаю случайную статью,нажмите еще раз", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.favorite:
                ArrayList<Article> favoriteArticles = new ArrayList<>();
                final SharedPreferences sharedPreferencesFavorites = activityMain.getSharedPreferences(activityMain.getString(R.string.pref_favorites), Context.MODE_PRIVATE);
                Set<String> keySet = sharedPreferencesFavorites.getAll().keySet();
                for (String key : keySet)
                {
                    Article article = new Article();
                    article.setURL(key);
                    article.setTitle(sharedPreferencesFavorites.getString(key, ""));
                    favoriteArticles.add(article);
                }
                fragmentName = "favorite";
                fragment = fragmentManager.findFragmentByTag(fragmentName);
                if (fragment == null)
                {
                    fragment = FragmentFavorite.newInstance(favoriteArticles);
                }
                fragmentTransaction.replace(R.id.content_frame, fragment, fragmentName);
                fragmentTransaction.addToBackStack(fragmentName);
                fragmentTransaction.commit();
                break;
            case R.id.offline:
                ArrayList<Article> offlineArticles = new ArrayList<>();
                final SharedPreferences sharedPreferencesOffline = activityMain.getSharedPreferences(activityMain.getString(R.string.pref_offline), Context.MODE_PRIVATE);
                Set<String> keySetOffline = sharedPreferencesOffline.getAll().keySet();
                for (String key : keySetOffline)
                {
                    if (key.endsWith("text") || key.endsWith("show"))
                    {
                        continue;
                    } else
                    {
                        if (!sharedPreferencesOffline.getBoolean(key + "show", false))
                        {
                            continue;
                        }
                    }
                    Article article = new Article();
                    article.setURL(key);
                    article.setTitle(sharedPreferencesOffline.getString(key, ""));
                    article.setArticlesText(sharedPreferencesOffline.getString(key + "text", ""));
                    offlineArticles.add(article);
                }
                fragmentName = "offline";
                fragment = fragmentManager.findFragmentByTag(fragmentName);
                if (fragment == null)
                {
                    fragment = FragmentOffline.newInstance(offlineArticles);
                }
                fragmentTransaction.replace(R.id.content_frame, fragment, fragmentName);
                fragmentTransaction.addToBackStack(fragmentName);
                fragmentTransaction.commit();
                break;
            case R.id.files:
                Log.d(LOG, "Материалы");
                fragmentName = FragmentMaterialsAll.LOG;
                fragment = fragmentManager.findFragmentByTag(fragmentName);
                if (fragment == null)
                {
                    fragment = new FragmentMaterialsAll();
                }
                fragmentTransaction.replace(R.id.content_frame, fragment, fragmentName);
                fragmentTransaction.addToBackStack(fragmentName);
                fragmentTransaction.commit();
                break;
            case R.id.stories:
                fragment = FragmentArticle.newInstance(Const.Urls.STORIES, "Рассказы");
                fragmentName = ((FragmentArticle) fragment).LOG;
                fragment = fragmentManager.findFragmentByTag(fragmentName);
                if (fragment == null)
                {
                    fragment = FragmentArticle.newInstance(Const.Urls.STORIES, "Рассказы");
                }
                fragmentTransaction.replace(R.id.content_frame, fragment, fragmentName);
                fragmentTransaction.addToBackStack(fragmentName);
                fragmentTransaction.commit();
                break;
            case R.id.gallery:
                Intent galleryIntent = new Intent(activityMain, ActivityGallery.class);
                activityMain.startActivity(galleryIntent);
                break;
            case R.id.site_search:
                fragmentName = "site_search";
                fragment = fragmentManager.findFragmentByTag(fragmentName);
                if (fragment == null)
                {
                    fragment = FragmentSearch.newInstance();
                }
                fragmentTransaction.replace(R.id.content_frame, fragment, fragmentName);
                fragmentTransaction.addToBackStack(fragmentName);
                fragmentTransaction.commit();
                break;
        }
        if (menuItem.getItemId() != R.id.gallery && menuItem.getItemId() != R.id.random_page)
        {
            menuItem.setChecked(true);
            activityMain.getToolbar().setTitle(NavigationItemSelectedListenerMain.getTitleById(menuItem.getItemId()));
        }
        activityMain.getDrawerLayout().closeDrawers();
        return true;
    }
}