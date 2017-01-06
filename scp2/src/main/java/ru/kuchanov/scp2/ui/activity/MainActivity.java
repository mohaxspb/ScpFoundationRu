package ru.kuchanov.scp2.ui.activity;

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
import ru.kuchanov.scp2.mvp.contract.Main;
import ru.kuchanov.scp2.ui.base.BaseDrawerActivity;
import ru.kuchanov.scp2.ui.fragment.ArticleFragment;
import ru.kuchanov.scp2.ui.fragment.RatedArticlesFragment;
import ru.kuchanov.scp2.ui.fragment.RecentArticlesFragment;
import timber.log.Timber;

public class MainActivity extends BaseDrawerActivity<Main.View, Main.Presenter> implements Main.View {

    private static final String STATE_CUR_DRAWER_ITEM_ID = "STATE_CUR_DRAWER_ITEM_ID";

    private int mCurrentSelectedDrawerItemId = R.id.mostRatedArticles;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CUR_DRAWER_ITEM_ID, mCurrentSelectedDrawerItemId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter.onCreate();

        mNavigationView.setNavigationItemSelectedListener(item -> {
            mPresenter.onNavigationItemClicked(item.getItemId());
            onNavigationItemClicked(item.getItemId());
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
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
    public Main.Presenter createPresenter() {
        return mPresenter;
    }

    @Override
    public void onNavigationItemClicked(int id) {
        Timber.d("onNavigationItemClicked with id: %s", id);
        String title;
        Fragment fragment;
        String tag;
        switch (id) {
            case R.id.about:
                mCurrentSelectedDrawerItemId = id;
                title = getString(R.string.drawer_item_1);
                tag = ArticleFragment.TAG + "#" + Constants.Urls.ABOUT_SCP;
                hideFragments();
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = ArticleFragment.newInstance(Constants.Urls.ABOUT_SCP, getString(R.string.about_org), null);
                    getSupportFragmentManager().beginTransaction()
                            .add(content.getId(), fragment, tag)
                            .commit();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .show(fragment)
                            .commit();
                }
                break;
            case R.id.news:
                mCurrentSelectedDrawerItemId = id;
                title = getString(R.string.drawer_item_2);
                tag = ArticleFragment.TAG + "#" + Constants.Urls.NEWS;
                hideFragments();
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = ArticleFragment.newInstance(Constants.Urls.NEWS, getString(R.string.news), null);
                    getSupportFragmentManager().beginTransaction()
                            .add(content.getId(), fragment, tag)
                            .commit();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .show(fragment)
                            .commit();
                }
                break;
            case R.id.mostRatedArticles:
                mCurrentSelectedDrawerItemId = id;
                title = getString(R.string.drawer_item_3);
                hideFragments();
                fragment = getSupportFragmentManager().findFragmentByTag(RatedArticlesFragment.TAG);
                if (fragment == null) {
                    fragment = RatedArticlesFragment.newInstance();
                    getSupportFragmentManager().beginTransaction()
                            .add(content.getId(), fragment, RatedArticlesFragment.TAG)
                            .commit();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .show(fragment)
                            .commit();
                }
                break;
            case R.id.mostRecentArticles:
                mCurrentSelectedDrawerItemId = id;
                title = getString(R.string.drawer_item_4);
                hideFragments();
                fragment = getSupportFragmentManager().findFragmentByTag(RecentArticlesFragment.TAG);
                if (fragment == null) {
                    fragment = RecentArticlesFragment.newInstance();
                    getSupportFragmentManager().beginTransaction()
                            .add(content.getId(), fragment, RecentArticlesFragment.TAG)
                            .commit();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .show(fragment)
                            .commit();
                }
                break;
            case R.id.random_page:
                title = getString(R.string.drawer_item_5);
                //TODO
                break;
            case R.id.objects_I:
                mCurrentSelectedDrawerItemId = id;
                title = getString(R.string.drawer_item_6);
                //TODO
                break;
            case R.id.objects_II:
                mCurrentSelectedDrawerItemId = id;
                title = getString(R.string.drawer_item_7);
                //TODO
                break;
            case R.id.objects_III:
                mCurrentSelectedDrawerItemId = id;
                title = getString(R.string.drawer_item_8);
                //TODO
                break;
            case R.id.objects_RU:
                mCurrentSelectedDrawerItemId = id;
                title = getString(R.string.drawer_item_9);
                //TODO
                break;
            case R.id.files:
                title = getString(R.string.drawer_item_10);
                //TODO launch new activity
                break;
            case R.id.stories:
                title = getString(R.string.drawer_item_11);
                //TODO
                break;
            case R.id.favorite:
                mCurrentSelectedDrawerItemId = id;
                title = getString(R.string.drawer_item_12);
                //TODO
                break;
            case R.id.offline:
                mCurrentSelectedDrawerItemId = id;
                title = getString(R.string.drawer_item_13);
                //TODO
                break;
            case R.id.gallery:
                title = getString(R.string.drawer_item_14);
                //TODO
                break;
            case R.id.site_search:
                mCurrentSelectedDrawerItemId = id;
                title = getString(R.string.drawer_item_15);
                //TODO
                break;
            default:
                title = "";
                break;
        }
        assert mToolbar != null;
        mToolbar.setTitle(title);
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
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.night_mode_item:
                mMyPreferenceManager.setIsNightMode(!mMyPreferenceManager.isNightMode());
                recreate();
                return true;
            case R.id.text_size:
                //TODO
//                FragmentDialogTextAppearance fragmentDialogTextAppearance = FragmentDialogTextAppearance.newInstance();
//                fragmentDialogTextAppearance.show(getFragmentManager(), "ХЗ");
                return true;
            case R.id.info:
                //TODO
//                new MaterialDialog.Builder(ctx)
//                        .content(R.string.dialog_info_content)
//                        .title("О приложении")
//                        .show();
                return true;
            case R.id.settings:
                //TODO
//                Intent intent = new Intent(ctx, ActivitySettings.class);
//                ctx.startActivity(intent);
                return true;
            case R.id.subscribe:
                //TODO
//                SubscriptionHelper.showSubscriptionDialog(this);
                return true;
        }
        return false;
    }
}