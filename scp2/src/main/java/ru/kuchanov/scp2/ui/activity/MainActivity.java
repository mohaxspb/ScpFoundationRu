package ru.kuchanov.scp2.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;

import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.mvp.contract.Main;
import ru.kuchanov.scp2.ui.fragment.AboutFragment;
import ru.kuchanov.scp2.ui.base.BaseDrawerActivity;
import timber.log.Timber;

public class MainActivity extends BaseDrawerActivity<Main.View, Main.Presenter> implements Main.View {

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
        //TODO
        String title;
        Fragment fragment;
        switch (id) {
            case R.id.about:
                title = getString(R.string.drawer_item_1);
                fragment = getSupportFragmentManager().findFragmentByTag(AboutFragment.TAG);
                if (fragment == null) {
                    fragment = AboutFragment.newInstance();
                    getSupportFragmentManager().beginTransaction()
                            .add(content.getId(), fragment, AboutFragment.TAG)
                            .commit();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(content.getId(), fragment, AboutFragment.TAG)
                            .commit();
                }
                break;
            case R.id.news:
                title = getString(R.string.drawer_item_2);
                break;
            case R.id.rate_articles:
                title = getString(R.string.drawer_item_3);
                break;
            case R.id.new_articles:
                title = getString(R.string.drawer_item_4);
                break;
            case R.id.objects_I:
                title = getString(R.string.drawer_item_5);
                break;
            case R.id.objects_II:
                title = getString(R.string.drawer_item_6);
                break;
            case R.id.objects_III:
                title = getString(R.string.drawer_item_7);
                break;
            case R.id.objects_RU:
                title = getString(R.string.drawer_item_8);
                break;
            case R.id.favorite:
                title = getString(R.string.drawer_item_9);
                break;
            case R.id.offline:
                title = getString(R.string.drawer_item_10);
                break;
            case R.id.stories:
                title = getString(R.string.drawer_item_11);
                break;
            case R.id.files:
                title = getString(R.string.drawer_item_12);
                break;
            case R.id.site_search:
                title = getString(R.string.drawer_item_13);
                break;
            default:
                title = "";
                break;
        }
        assert mToolbar != null;
        mToolbar.setTitle(title);
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