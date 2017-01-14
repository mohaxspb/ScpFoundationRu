package ru.kuchanov.scp2.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;

import java.util.List;

import ru.kuchanov.scp2.Constants;
import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.mvp.contract.MainMvp;
import ru.kuchanov.scp2.ui.base.BaseDrawerActivity;
import ru.kuchanov.scp2.ui.dialog.TextSizeDialogFragment;
import ru.kuchanov.scp2.ui.fragment.ArticleFragment;
import ru.kuchanov.scp2.ui.fragment.FavoriteArticlesFragment;
import ru.kuchanov.scp2.ui.fragment.Objects1ArticlesFragment;
import ru.kuchanov.scp2.ui.fragment.Objects2ArticlesFragment;
import ru.kuchanov.scp2.ui.fragment.Objects3ArticlesFragment;
import ru.kuchanov.scp2.ui.fragment.ObjectsRuArticlesFragment;
import ru.kuchanov.scp2.ui.fragment.OfflineArticlesFragment;
import ru.kuchanov.scp2.ui.fragment.RatedArticlesFragment;
import ru.kuchanov.scp2.ui.fragment.RecentArticlesFragment;
import ru.kuchanov.scp2.ui.fragment.SiteSearchArticlesFragment;
import timber.log.Timber;

public class MainActivity
        extends BaseDrawerActivity<MainMvp.View, MainMvp.Presenter>
        implements MainMvp.View {

    public static final String EXTRA_LINK = "EXTRA_LINK";

    public static void startActivity(Context context, String link) {
        Timber.d("startActivity: %s", link);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_LINK, link);
        context.startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Timber.d("onNewIntent");
        super.onNewIntent(intent);

        setIntent(intent);

        setDrawerItemFromIntent();

        mNavigationView.setCheckedItem(mCurrentSelectedDrawerItemId);
        onNavigationItemClicked(mCurrentSelectedDrawerItemId);
        setToolbarTitleByDrawerItemId(mCurrentSelectedDrawerItemId);
    }

    private void setDrawerItemFromIntent() {
        String link = getIntent().getStringExtra(EXTRA_LINK);
        Timber.d("setDrawerItemFromIntent: %s", link);
        switch (link) {
            case Constants.Urls.ABOUT_SCP:
                mCurrentSelectedDrawerItemId = (R.id.about);
                break;
            case Constants.Urls.NEWS:
                mCurrentSelectedDrawerItemId = (R.id.news);
                break;
            case Constants.Urls.MAIN:
            case Constants.Urls.RATE:
                mCurrentSelectedDrawerItemId = R.id.mostRatedArticles;
                break;
            case Constants.Urls.NEW_ARTICLES:
                mCurrentSelectedDrawerItemId = R.id.mostRecentArticles;
                break;
            case Constants.Urls.OBJECTS_1:
                mCurrentSelectedDrawerItemId = (R.id.objects_I);
                break;
            case Constants.Urls.OBJECTS_2:
                mCurrentSelectedDrawerItemId = (R.id.objects_II);
                break;
            case Constants.Urls.OBJECTS_3:
                mCurrentSelectedDrawerItemId = (R.id.objects_III);
                break;
            case Constants.Urls.OBJECTS_RU:
                mCurrentSelectedDrawerItemId = (R.id.objects_RU);
                break;
            case Constants.Urls.STORIES:
                mCurrentSelectedDrawerItemId = (R.id.stories);
                break;
            case Constants.Urls.FAVORITES:
                mCurrentSelectedDrawerItemId = (R.id.favorite);
                break;
            case Constants.Urls.OFFLINE:
                mCurrentSelectedDrawerItemId = (R.id.offline);
                break;
            case Constants.Urls.SEARCH:
                mCurrentSelectedDrawerItemId = (R.id.siteSearch);
                break;
            default:
                mCurrentSelectedDrawerItemId = SELECTED_DRAWER_ITEM_NONE;
                break;
        }
        getIntent().removeExtra(EXTRA_LINK);
    }

    @Override
    protected int getDefaultNavItemId() {
        return R.id.mostRatedArticles;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra(EXTRA_LINK)) {
            setDrawerItemFromIntent();
        }

        if (getSupportFragmentManager().findFragmentById(content.getId()) == null) {
            onNavigationItemClicked(mCurrentSelectedDrawerItemId);
        }
        mNavigationView.setCheckedItem(mCurrentSelectedDrawerItemId);
        setToolbarTitleByDrawerItemId(mCurrentSelectedDrawerItemId);
    }

    @Override
    protected boolean isDrawerIndicatorEnabled() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void callInjections() {
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    protected int getMenuResId() {
        return R.menu.menu_main;
    }

    @NonNull
    @Override
    public MainMvp.Presenter createPresenter() {
        return mPresenter;
    }

    @Override
    public void onNavigationItemClicked(int id) {
        Timber.d("onNavigationItemClicked with id: %s", id);
        setToolbarTitleByDrawerItemId(id);
        switch (id) {
            case R.id.about:
                mCurrentSelectedDrawerItemId = id;
                showFragment(ArticleFragment.newInstance(Constants.Urls.ABOUT_SCP),
                        ArticleFragment.TAG + "#" + Constants.Urls.ABOUT_SCP);
                break;
            case R.id.news:
                mCurrentSelectedDrawerItemId = id;
                showFragment(ArticleFragment.newInstance(Constants.Urls.NEWS), ArticleFragment.TAG + "#" + Constants.Urls.NEWS);
                break;
            case R.id.mostRatedArticles:
                mCurrentSelectedDrawerItemId = id;
                showFragment(RatedArticlesFragment.newInstance(), RatedArticlesFragment.TAG);
                break;
            case R.id.mostRecentArticles:
                mCurrentSelectedDrawerItemId = id;
                showFragment(RecentArticlesFragment.newInstance(), RecentArticlesFragment.TAG);
                break;
            case R.id.random_page:
                //TODO
                break;
            case R.id.objects_I:
                mCurrentSelectedDrawerItemId = id;
                showFragment(Objects1ArticlesFragment.newInstance(), Objects1ArticlesFragment.TAG);
                break;
            case R.id.objects_II:
                mCurrentSelectedDrawerItemId = id;
                showFragment(Objects2ArticlesFragment.newInstance(), Objects2ArticlesFragment.TAG);
                break;
            case R.id.objects_III:
                mCurrentSelectedDrawerItemId = id;
                showFragment(Objects3ArticlesFragment.newInstance(), Objects3ArticlesFragment.TAG);
                break;
            case R.id.objects_RU:
                mCurrentSelectedDrawerItemId = id;
                showFragment(ObjectsRuArticlesFragment.newInstance(), ObjectsRuArticlesFragment.TAG);
                break;
            case R.id.files:
                //TODO launch new activity
                break;
            case R.id.stories:
                mCurrentSelectedDrawerItemId = id;
                showFragment(ArticleFragment.newInstance(Constants.Urls.STORIES),
                        ArticleFragment.TAG + "#" + Constants.Urls.STORIES);
                break;
            case R.id.favorite:
                mCurrentSelectedDrawerItemId = id;
                showFragment(FavoriteArticlesFragment.newInstance(), FavoriteArticlesFragment.TAG);
                break;
            case R.id.offline:
                mCurrentSelectedDrawerItemId = id;
                showFragment(OfflineArticlesFragment.newInstance(), OfflineArticlesFragment.TAG);
                break;
            case R.id.gallery:
                //TODO
                break;
            case R.id.siteSearch:
                mCurrentSelectedDrawerItemId = id;
                showFragment(SiteSearchArticlesFragment.newInstance(), SiteSearchArticlesFragment.TAG);
                break;
            default:
                Timber.e("unexpected item ID");
                break;
        }
    }

    private void showFragment(Fragment fragmentToShow, String tag) {
        hideFragments();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(content.getId(), fragmentToShow, tag)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .show(fragment)
                    .commit();
        }
    }

    @Override
    public void setToolbarTitleByDrawerItemId(int id) {
        Timber.d("setToolbarTitleByDrawerItemId with id: %s", id);
        String title;
        switch (id) {
            case R.id.about:
                title = getString(R.string.drawer_item_1);
                break;
            case R.id.news:
                title = getString(R.string.drawer_item_2);
                break;
            case R.id.mostRatedArticles:
                title = getString(R.string.drawer_item_3);
                break;
            case R.id.mostRecentArticles:
                title = getString(R.string.drawer_item_4);
                break;
            case R.id.objects_I:
                title = getString(R.string.drawer_item_6);
                break;
            case R.id.objects_II:
                title = getString(R.string.drawer_item_7);
                break;
            case R.id.objects_III:
                title = getString(R.string.drawer_item_8);
                break;
            case R.id.objects_RU:
                title = getString(R.string.drawer_item_9);
                break;
            case R.id.stories:
                title = getString(R.string.drawer_item_11);
                break;
            case R.id.favorite:
                title = getString(R.string.drawer_item_12);
                break;
            case R.id.offline:
                title = getString(R.string.drawer_item_13);
                break;
            case R.id.siteSearch:
                title = getString(R.string.drawer_item_15);
                break;
            default:
                Timber.e("unexpected item ID");
                title = null;
                break;
        }
        assert mToolbar != null;
        if (title != null) {
            mToolbar.setTitle(title);
        }
    }

    private void hideFragments() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments == null || fragments.isEmpty()) {
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (Fragment fragment : fragments) {
            transaction.hide(fragment);
        }
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Timber.d("onOptionsItemSelected with id: %s", item);

        switch (item.getItemId()) {
            case android.R.id.home:
                //TODO move to abstract
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.night_mode_item:
                mMyPreferenceManager.setIsNightMode(!mMyPreferenceManager.isNightMode());
                recreate();
                return true;
            case R.id.text_size:
                TextSizeDialogFragment fragmentDialogTextAppearance = TextSizeDialogFragment.newInstance();
                fragmentDialogTextAppearance.show(getFragmentManager(), TextSizeDialogFragment.TAG);
                return true;
            case R.id.info:
                //TODO
//                new MaterialDialog.Builder(ctx)
//                        .content(R.string.dialog_info_content)
//                        .title("О приложении")
//                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}