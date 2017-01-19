package ru.dante.scpfoundation.ui.base;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.mvp.base.DrawerMvp;
import ru.dante.scpfoundation.ui.activity.ArticleActivity;
import timber.log.Timber;

/**
 * Created by mohax on 02.01.2017.
 * <p>
 * for scp_ru
 */
public abstract class BaseDrawerActivity<V extends DrawerMvp.View, P extends DrawerMvp.Presenter<V>>
        extends BaseActivity<V, P>
        implements DrawerMvp.View {

    private static final String STATE_CUR_DRAWER_ITEM_ID = "STATE_CUR_DRAWER_ITEM_ID";
    protected static final int SELECTED_DRAWER_ITEM_NONE = -1;

    @BindView(R.id.root)
    protected DrawerLayout mDrawerLayout;
    @BindView(R.id.navigationView)
    protected NavigationView mNavigationView;

    protected MaterialDialog dialog;

    protected ActionBarDrawerToggle mDrawerToggle;

    protected int mCurrentSelectedDrawerItemId;

    protected abstract int getDefaultNavItemId();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CUR_DRAWER_ITEM_ID, mCurrentSelectedDrawerItemId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrentSelectedDrawerItemId = getDefaultNavItemId();

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);

            actionBar.setDisplayHomeAsUpEnabled(true);

            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {
                public void onDrawerClosed(View view) {
                    supportInvalidateOptionsMenu();
                }

                public void onDrawerOpened(View drawerView) {
                }
            };
            mDrawerToggle.setDrawerIndicatorEnabled(isDrawerIndicatorEnabled());

            mDrawerLayout.addDrawerListener(mDrawerToggle);
        }

        mNavigationView.setNavigationItemSelectedListener(item -> {
            mPresenter.onNavigationItemClicked(item.getItemId());
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return onNavigationItemClicked(item.getItemId());
        });

        if (savedInstanceState != null) {
            mCurrentSelectedDrawerItemId = savedInstanceState.getInt(STATE_CUR_DRAWER_ITEM_ID);
        }
        if (mCurrentSelectedDrawerItemId != SELECTED_DRAWER_ITEM_NONE) {
            mNavigationView.setCheckedItem(mCurrentSelectedDrawerItemId);
        } else {
            mNavigationView.getMenu().setGroupCheckable(0, false, true);
        }
    }

    /**
     * @return true if need to show hamburger. False if want show arrow
     */
    protected abstract boolean isDrawerIndicatorEnabled();

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Timber.d("onOptionsItemSelected with id: %s", item);

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void startArticleActivity(String url) {
        ArticleActivity.startActivity(this, url);
    }

    @Override
    public void showProgressDialog(boolean show) {
        if (show) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
            builder.title(R.string.dialog_random_page_title);
            builder.content(R.string.dialog_random_page_message);
            builder.progress(true, 0);
            builder.cancelable(false);
            dialog = builder.build();
            dialog.show();
        } else if (dialog != null) {
            dialog.dismiss();
        }
    }
}