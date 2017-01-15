package ru.dante.scpfoundation.ui.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.yandex.metrica.YandexMetrica;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.manager.InAppBillingServiceConnectionObservable;
import ru.dante.scpfoundation.manager.MyNotificationManager;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.monetization.model.Item;
import ru.dante.scpfoundation.monetization.util.MyAdListener;
import ru.dante.scpfoundation.mvp.base.AdsActions;
import ru.dante.scpfoundation.mvp.base.BaseMvp;
import ru.dante.scpfoundation.ui.dialog.SetttingsBottomSheetDialogFragment;
import ru.dante.scpfoundation.ui.dialog.SubscriptionsFragmentDialog;
import timber.log.Timber;

/**
 * Created by mohax on 31.12.2016.
 * <p>
 * for scp_ru
 */
public abstract class BaseActivity<V extends BaseMvp.View, P extends BaseMvp.Presenter<V>>
        extends MvpActivity<V, P>
        implements BaseMvp.View, AdsActions {

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

    //inapps and ads
    private IInAppBillingService mService;
    private List<Item> mOwnedMarketItems = new ArrayList<>();
    private InterstitialAd mInterstitialAd;

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
        mMyNotificationManager.checkAlarm();

        //initAds subs service
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        //ads
        initAds();
    }

    @Override
    public boolean isTimeToShowAds() {
        return mOwnedMarketItems.isEmpty() && mMyPreferenceManager.isTimeToShowAds();
    }

    @Override
    public boolean isAdsLoaded() {
        return mInterstitialAd.isLoaded();
    }

    @Override
    public void showAds() {
        mInterstitialAd.setAdListener(new MyAdListener());
        mInterstitialAd.show();
    }

    @Override
    public void showAds(MyAdListener adListener) {
        mInterstitialAd.setAdListener(adListener);
        mInterstitialAd.show();
    }

    @Override
    public void initAds() {
        MobileAds.initialize(getApplicationContext(), getString(R.string.ads_app_id));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.ad_unit_id_interstitial));
        mInterstitialAd.setAdListener(new MyAdListener());

        if (isTimeToShowAds()) {
            requestNewInterstitial();
        }
    }

    @Override
    public void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("A22E60ED57ABD5DD2947708F10EB5342")
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    public IInAppBillingService getIInAppBillingService() {
        return mService;
    }

    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Timber.d("onServiceDisconnected");
            mService = null;
            InAppBillingServiceConnectionObservable.getInstance().getServiceStatusObservable().onNext(false);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Timber.d("onServiceConnected");
            mService = IInAppBillingService.Stub.asInterface(service);
            InAppBillingServiceConnectionObservable.getInstance().getServiceStatusObservable().onNext(true);
            updateOwnedMarketItems();
        }
    };

    @Override
    public void updateOwnedMarketItems() {
        SubscriptionsFragmentDialog.getOwnedInappsObserveble(this, mService)
                .subscribe(items -> {
                    Timber.d("market items: %s", items);
                    mOwnedMarketItems = items;
                    supportInvalidateOptionsMenu();
                });
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

    //workaround from http://stackoverflow.com/a/30337653/3212712 to showAds menu icons
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

            MenuItem subs = menu.findItem(R.id.subscribe);
            subs.setVisible(mOwnedMarketItems.isEmpty());
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem subs = menu.findItem(R.id.subscribe);
        if (subs != null) {
            subs.setVisible(mOwnedMarketItems.isEmpty());
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void showError(Throwable throwable) {
        //TODO switch errors types
        Snackbar.make(root, throwable.getMessage(), Snackbar.LENGTH_SHORT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Timber.d("settings pressed");
                BottomSheetDialogFragment settingsDF = SetttingsBottomSheetDialogFragment.newInstance();
                settingsDF.show(getSupportFragmentManager(), settingsDF.getTag());
                return true;
            case R.id.subscribe:
                Timber.d("subscribe pressed");
                BottomSheetDialogFragment subsDF = SubscriptionsFragmentDialog.newInstance();
                subsDF.show(getSupportFragmentManager(), subsDF.getTag());
                return true;
            default:
                Timber.wtf("unexpected id: %s", item.getItemId());
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        YandexMetrica.onResumeActivity(this);

        if (isTimeToShowAds() && !isAdsLoaded()) {
            requestNewInterstitial();
        }
    }

    @Override
    public void onPause() {
        YandexMetrica.onPauseActivity(this);
        super.onPause();
    }
}