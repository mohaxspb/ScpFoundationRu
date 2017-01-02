package ru.kuchanov.scp2.ui.base;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import butterknife.BindView;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.mvp.base.Drawer;

/**
 * Created by mohax on 02.01.2017.
 * <p>
 * for scp_ru
 */
public abstract class BaseDrawerActivity<V extends Drawer.View, P extends Drawer.Presenter<V>>
        extends BaseActivity<V, P> {

    @BindView(R.id.root)
    protected DrawerLayout mDrawerLayout;
    @BindView(R.id.navigationView)
    protected NavigationView mNavigationView;
}