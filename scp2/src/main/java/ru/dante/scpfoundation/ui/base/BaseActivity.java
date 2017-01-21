package ru.dante.scpfoundation.ui.base;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.yandex.metrica.YandexMetrica;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dante.scpfoundation.BuildConfig;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.manager.InAppBillingServiceConnectionObservable;
import ru.dante.scpfoundation.manager.MyNotificationManager;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.monetization.model.Item;
import ru.dante.scpfoundation.monetization.util.MyAdListener;
import ru.dante.scpfoundation.mvp.base.AdsActions;
import ru.dante.scpfoundation.mvp.base.BaseMvp;
import ru.dante.scpfoundation.ui.dialog.NewVersionDialogFragment;
import ru.dante.scpfoundation.ui.dialog.SetttingsBottomSheetDialogFragment;
import ru.dante.scpfoundation.ui.dialog.SubscriptionsFragmentDialog;
import ru.dante.scpfoundation.ui.dialog.TextSizeDialogFragment;
import ru.dante.scpfoundation.util.SystemUtils;
import timber.log.Timber;

/**
 * Created by mohax on 31.12.2016.
 * <p>
 * for scp_ru
 */
public abstract class BaseActivity<V extends BaseMvp.View, P extends BaseMvp.Presenter<V>>
        extends MvpActivity<V, P>
        implements BaseMvp.View, AdsActions, SharedPreferences.OnSharedPreferenceChangeListener {

    @BindView(R.id.root)
    protected View mRoot;
    @BindView(R.id.content)
    protected View mContent;
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
    private RewardedVideoAd mRewardedVideoAd;
    private MaterialDialog mDialogRewardProgress;

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

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void startRewardedVideoFlow() {
        if (mMyPreferenceManager.isRewardedDescriptionShown()) {
            //load ads
            mDialogRewardProgress = new MaterialDialog.Builder(this)
                    .progress(true, 0)
                    .title(R.string.dialog_download)
                    .content(R.string.wait)
                    .build();
            mDialogRewardProgress.show();
            loadRewardedVideoAd();
        } else {
            new MaterialDialog.Builder(this)
                    .title(R.string.ads_reward_description_title)
                    .content(R.string.ads_reward_description_content)
                    .positiveText(R.string.ads_reward_ok)
                    .onPositive((dialog, which) -> {
                        mMyPreferenceManager.setRewardedDescriptionIsNotShown(true);
                        startRewardedVideoFlow();
                    })
                    .show();
        }
    }

    @Override
    public void loadRewardedVideoAd() {
        if (!mRewardedVideoAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder()
                    //do not use test device as test ads turns on in networks console
//                    .addTestDevice("A22E60ED57ABD5DD2947708F10EB5342")
                    .build();
            mRewardedVideoAd.loadAd(BuildConfig.AD_UNIT_ID_REWARDED, adRequest);
        }
    }

    @Override
    public void showRewardedVideo() {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    @Override
    public boolean isTimeToShowAds() {
        Timber.d("isTimeToShowAds mOwnedMarketItems.isEmpty(): %s, mMyPreferenceManager.isTimeToShowAds(): %s",
                mOwnedMarketItems.isEmpty(),
                mMyPreferenceManager.isTimeToShowAds());
        return mOwnedMarketItems.isEmpty() && mMyPreferenceManager.isTimeToShowAds();
    }

    @Override
    public boolean isAdsLoaded() {
        return mInterstitialAd.isLoaded();
    }

    @Override
    public void showInterstitial() {
        mInterstitialAd.setAdListener(new MyAdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Snackbar snackbar = Snackbar.make(mRoot, R.string.remove_ads, Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.yes_bliad, v -> {
                    snackbar.dismiss();
                    BottomSheetDialogFragment subsDF = SubscriptionsFragmentDialog.newInstance();
                    subsDF.show(getSupportFragmentManager(), subsDF.getTag());
                });
                snackbar.setActionTextColor(ContextCompat.getColor(BaseActivity.this, R.color.material_amber_500));
                snackbar.show();
            }
        });
        mInterstitialAd.show();
    }

    @Override
    public void showInterstitial(MyAdListener adListener) {
        mInterstitialAd.setAdListener(adListener);
        mInterstitialAd.show();
    }

    @Override
    public void initAds() {
        MobileAds.initialize(getApplicationContext(), getString(R.string.ads_app_id));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.ad_unit_id_interstitial));
        mInterstitialAd.setAdListener(new MyAdListener());

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                Timber.d("onRewardedVideoAdLoaded");
                mDialogRewardProgress.dismiss();
                showRewardedVideo();
            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                Timber.d("onRewarded type: %s, amount: %s", rewardItem.getType(), rewardItem.getAmount());

                mMyPreferenceManager.applyRewardFromAds();

                Snackbar.make(mRoot, R.string.ads_reward_gained, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                Timber.e("onRewardedVideoAdFailedToLoad: %s", i);
                loadRewardedVideoAd();

                Snackbar.make(mRoot, R.string.ads_reward_error_loading, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void requestNewInterstitial() {
        Timber.d("requestNewInterstitial");
        if (mInterstitialAd.isLoading()) {
            Timber.d("loading already in progress");
        } else {
            AdRequest.Builder adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR);

            if (BuildConfig.DEBUG) {
                @SuppressLint("HardwareIds")
                String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                String deviceId;
                deviceId = SystemUtils.MD5(androidId);
                if (deviceId != null) {
                    deviceId = deviceId.toUpperCase();
                    adRequest.addTestDevice(deviceId);
                }
//                boolean isTestDevice = adRequest.isTestDevice(this);
//                Timber.v("is Admob Test Device ? %s, %s", deviceId, isTestDevice);
            }

            mInterstitialAd.loadAd(adRequest.build());
        }
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

            if (isTimeToShowAds()) {
                requestNewInterstitial();
            }
        }
    };

    @Override
    public void updateOwnedMarketItems() {
        SubscriptionsFragmentDialog.getOwnedInappsObserveble(this, mService)
                .subscribe(
                        items -> {
                            Timber.d("market items: %s", items);
                            mOwnedMarketItems = items;
                            supportInvalidateOptionsMenu();
                        },
                        error -> {
                            Timber.e(error, "errror while getting owned items");
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

    /**
     * workaround from http://stackoverflow.com/a/30337653/3212712 to show menu icons
     */
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
        Snackbar.make(mRoot, throwable.getMessage(), Snackbar.LENGTH_SHORT);
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
            case R.id.night_mode_item:
                mMyPreferenceManager.setIsNightMode(!mMyPreferenceManager.isNightMode());
                return true;
            case R.id.text_size:
                BottomSheetDialogFragment fragmentDialogTextAppearance = TextSizeDialogFragment.newInstance(TextSizeDialogFragment.TextSizeType.ALL);
                fragmentDialogTextAppearance.show(getSupportFragmentManager(), TextSizeDialogFragment.TAG);
                return true;
            case R.id.info:
                DialogFragment dialogFragment = NewVersionDialogFragment.newInstance(getString(R.string.app_info));
                dialogFragment.show(getFragmentManager(), NewVersionDialogFragment.TAG);
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

        mRewardedVideoAd.resume(this);
    }

    @Override
    public void onPause() {
        YandexMetrica.onPauseActivity(this);
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Timber.d("onSharedPreferenceChanged with key: %s", key);
        switch (key) {
            case MyPreferenceManager.Keys.NIGHT_MODE:
                recreate();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }
}