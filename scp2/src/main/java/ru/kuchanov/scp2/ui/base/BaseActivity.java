package ru.kuchanov.scp2.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.lang.reflect.Method;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.manager.MyPreferenceManager;
import ru.kuchanov.scp2.mvp.base.BaseDataPresenter;
import ru.kuchanov.scp2.mvp.base.BaseMvpView;
import ru.kuchanov.scp2.manager.MyNotificationManager;
import timber.log.Timber;

/**
 * Created by mohax on 31.12.2016.
 * <p>
 * for scp_ru
 */
public abstract class BaseActivity<V extends BaseMvpView, P extends BaseDataPresenter<V>>
        extends MvpActivity<V, P>
        implements BaseMvpView {

    @BindView(R.id.root)
    protected View root;
    @BindView(R.id.content)
    protected View content;
    @Nullable
    @BindView(R.id.toolBar)
    protected Toolbar mToolbar;

    @Inject
    protected P mPresenter;
    @Inject
    protected MyPreferenceManager mMyPreferenceManager;
    @Inject
    protected MyNotificationManager mMyNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        callInjections();
        if (mMyPreferenceManager.isNightMode()) {
            setTheme(R.style.SCP_Theme_Dark);
        } else {
            setTheme(R.style.SCP_Theme_Light);
        }
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResId());
        ButterKnife.bind(this);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        mPresenter.onCreate();

        //setAlarm for notification
        //FIXME delete setting notif
        mMyPreferenceManager.setNotificationEnabled(true);
        mMyNotificationManager.checkAlarm();
    }

    /**
     * @return id of activity layout
     */
    protected abstract int getLayoutResId();

    /**
     * inject DI here
     */
    protected abstract void callInjections();

    /**
     * Override it to add menu or return 0 if you don't want it
     */
    protected abstract int getMenuResId();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getMenuResId() != 0) {
            getMenuInflater().inflate(getMenuResId(), menu);
        }
        return true;
    }

    //workaround from http://stackoverflow.com/a/30337653/3212712 to show menu icons
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Timber.e(e, "onMenuOpened...unable to set icons for overflow menu");
                }
            }

            boolean nightModeIsOn = mMyPreferenceManager.isNightMode();
            MenuItem themeMenuItem = menu.findItem(R.id.night_mode_item);
            if (nightModeIsOn) {
                themeMenuItem.setIcon(R.drawable.ic_brightness_low_white_24dp);
                themeMenuItem.setTitle(R.string.day_mode);
            } else {
                themeMenuItem.setIcon(R.drawable.ic_brightness_3_white_24dp);
                themeMenuItem.setTitle(R.string.night_mode);
            }

        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public void showError(Throwable throwable) {
        //TODO switch errors types
        Snackbar.make(root, throwable.getMessage(), Snackbar.LENGTH_SHORT);
    }
}