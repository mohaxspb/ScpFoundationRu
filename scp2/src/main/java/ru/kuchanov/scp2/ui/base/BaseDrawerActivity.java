package ru.kuchanov.scp2.ui.base;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.mvp.base.DrawerMvp;

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

    protected ActionBarDrawerToggle mDrawerToggle;

    protected int mCurrentSelectedDrawerItemId = getDefaultNavItemId();

    protected abstract int getDefaultNavItemId();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CUR_DRAWER_ITEM_ID, mCurrentSelectedDrawerItemId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            mDrawerToggle.setDrawerIndicatorEnabled(true);

            mDrawerLayout.addDrawerListener(mDrawerToggle);
        }

        mNavigationView.setNavigationItemSelectedListener(item -> {
            mPresenter.onNavigationItemClicked(item.getItemId());
            onNavigationItemClicked(item.getItemId());
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
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
}