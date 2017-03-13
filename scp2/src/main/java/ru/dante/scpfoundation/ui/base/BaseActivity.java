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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.vending.billing.IInAppBillingService;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.SkippableVideoCallbacks;
import com.appodeal.ads.utils.Log;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;
import com.yandex.metrica.YandexMetrica;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dante.scpfoundation.BuildConfig;
import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.db.model.User;
import ru.dante.scpfoundation.manager.InAppBillingServiceConnectionObservable;
import ru.dante.scpfoundation.manager.MyNotificationManager;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.monetization.model.Item;
import ru.dante.scpfoundation.monetization.util.InappHelper;
import ru.dante.scpfoundation.monetization.util.MyAdListener;
import ru.dante.scpfoundation.monetization.util.MyNonSkippableVideoCallbacks;
import ru.dante.scpfoundation.mvp.base.BaseMvp;
import ru.dante.scpfoundation.mvp.base.MonetizationActions;
import ru.dante.scpfoundation.ui.dialog.NewVersionDialogFragment;
import ru.dante.scpfoundation.ui.dialog.SetttingsBottomSheetDialogFragment;
import ru.dante.scpfoundation.ui.dialog.SubscriptionsFragmentDialog;
import ru.dante.scpfoundation.ui.dialog.TextSizeDialogFragment;
import ru.dante.scpfoundation.util.SystemUtils;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by mohax on 31.12.2016.
 * <p>
 * for scp_ru
 */
public abstract class BaseActivity<V extends BaseMvp.View, P extends BaseMvp.Presenter<V>>
        extends MvpActivity<V, P>
        implements BaseMvp.View, MonetizationActions, SharedPreferences.OnSharedPreferenceChangeListener {

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

    protected FirebaseAnalytics mFirebaseAnalytics;

    //inapps and ads
    private IInAppBillingService mService;
    private List<Item> mOwnedMarketItems = new ArrayList<>();
    private InterstitialAd mInterstitialAd;

    @NonNull
    @Override
    public P createPresenter() {
        return mPresenter;
    }

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
        //analitics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //remote config
        initAndUpdateRemoteConfig();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void initAds() {
        //init frameworks
        MobileAds.initialize(getApplicationContext(), getString(R.string.ads_app_id));

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.ad_unit_id_interstitial));
        mInterstitialAd.setAdListener(new MyAdListener());

        //appodeal
        String appKey = "96b84a34ca52ac1c82b8f3c61bfd0ade7abf5c2be24f2862";
        Appodeal.disableLocationPermissionCheck();
        Appodeal.confirm(Appodeal.SKIPPABLE_VIDEO);
        if (BuildConfig.DEBUG) {
            Appodeal.setTesting(true);
            Appodeal.setLogLevel(Log.LogLevel.debug);
        }
        Appodeal.initialize(this, appKey, Appodeal.NON_SKIPPABLE_VIDEO | Appodeal.SKIPPABLE_VIDEO);
        Appodeal.setNonSkippableVideoCallbacks(new MyNonSkippableVideoCallbacks() {

            @Override
            public void onNonSkippableVideoFinished() {
                super.onNonSkippableVideoFinished();
                mMyPreferenceManager.applyRewardFromAds();
                long numOfMillis = FirebaseRemoteConfig.getInstance()
                        .getLong(Constants.Firebase.RemoteConfigKeys.REWARDED_VIDEO_COOLDOWN_IN_MILLIS);
                long hours = numOfMillis / 1000 / 60 / 60;
                Snackbar.make(mRoot, getString(R.string.ads_reward_gained, hours), Snackbar.LENGTH_LONG).show();

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, Constants.Firebase.Analitics.EventType.REWARD_GAINED);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        });
    }

    @Override
    public void startRewardedVideoFlow() {
        //analitics
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, Constants.Firebase.Analitics.EventType.REWARD_REQUESTED);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        if (mMyPreferenceManager.isRewardedDescriptionShown()) {
            showRewardedVideo();
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
    public void showRewardedVideo() {
        if (Appodeal.isLoaded(Appodeal.NON_SKIPPABLE_VIDEO)) {
            Appodeal.show(this, Appodeal.NON_SKIPPABLE_VIDEO);
        } else {
            Snackbar.make(mRoot, R.string.reward_not_loaded_yet, Snackbar.LENGTH_SHORT).show();
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

    /**
     * ads adsListener with showing SnackBar after ads closing and calles {@link #showInterstitial(MyAdListener)}
     */
    @Override
    public void showInterstitial() {
        MyAdListener adListener = new MyAdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Snackbar snackbar = Snackbar.make(mRoot, R.string.remove_ads, Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.yes_bliad, v -> {
                    snackbar.dismiss();
                    BottomSheetDialogFragment subsDF = SubscriptionsFragmentDialog.newInstance();
                    subsDF.show(getSupportFragmentManager(), subsDF.getTag());

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, Constants.Firebase.Analitics.StartScreen.SNACK_BAR);
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                });
                snackbar.setActionTextColor(ContextCompat.getColor(BaseActivity.this, R.color.material_green_500));
                snackbar.show();
            }
        };
        showInterstitial(adListener);
    }

    /**
     * checks if it's time to show rewarded instead of simple interstitial
     * and it's ready and shows rewarded video or interstitial
     */
    @Override
    public void showInterstitial(MyAdListener adListener) {
        if (mMyPreferenceManager.isTimeToShowVideoInsteadOfInterstitial() && Appodeal.isLoaded(Appodeal.SKIPPABLE_VIDEO)) {
            Appodeal.setSkippableVideoCallbacks(new SkippableVideoCallbacks() {
                @Override
                public void onSkippableVideoLoaded() {

                }

                @Override
                public void onSkippableVideoFailedToLoad() {

                }

                @Override
                public void onSkippableVideoShown() {

                }

                @Override
                public void onSkippableVideoFinished() {
                    mMyPreferenceManager.setNumOfInterstitialsShown(0);

                    Appodeal.setNonSkippableVideoCallbacks(new MyNonSkippableVideoCallbacks() {
                        @Override
                        public void onNonSkippableVideoFinished() {
                            super.onNonSkippableVideoFinished();
                            mMyPreferenceManager.applyRewardFromAds();
                            long numOfMillis = FirebaseRemoteConfig.getInstance()
                                    .getLong(Constants.Firebase.RemoteConfigKeys.REWARDED_VIDEO_COOLDOWN_IN_MILLIS);
                            int hours = (int) (numOfMillis / 1000 / 60 / 60);
                            Snackbar.make(mRoot, getString(R.string.ads_reward_gained, hours), Snackbar.LENGTH_LONG).show();

                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, Constants.Firebase.Analitics.EventType.REWARD_GAINED);
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                        }
                    });
                }

                @Override
                public void onSkippableVideoClosed(boolean b) {

                }
            });
            Appodeal.show(this, Appodeal.NON_SKIPPABLE_VIDEO);
        } else {
            mInterstitialAd.setAdListener(adListener);
            mInterstitialAd.show();
        }
    }

    @Override
    public void requestNewInterstitial() {
        Timber.d("requestNewInterstitial");
        if (mInterstitialAd.isLoading()) {
            Timber.d("loading already in progress");
        } else {
            AdRequest.Builder adRequest = new AdRequest.Builder();

            if (BuildConfig.DEBUG) {
                @SuppressLint("HardwareIds")
                String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                String deviceId;
                deviceId = SystemUtils.MD5(androidId);
                if (deviceId != null) {
                    deviceId = deviceId.toUpperCase();
                    adRequest.addTestDevice(deviceId);
                }
                adRequest.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
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
        InappHelper.getOwnedInappsObserveble(this, mService)
                .subscribe(
                        items -> {
                            Timber.d("market items: %s", items);
                            mOwnedMarketItems = items;
                            supportInvalidateOptionsMenu();
                        },
                        error -> Timber.e(error, "errror while getting owned items"));
    }

    @Override
    public List<Item> getOwnedItems() {
        return mOwnedMarketItems;
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

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, Constants.Firebase.Analitics.StartScreen.MENU);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
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
        if (!BuildConfig.DEBUG) {
            YandexMetrica.onResumeActivity(this);
        }

        if (isTimeToShowAds() && !isAdsLoaded()) {
            requestNewInterstitial();
        }
    }

    @Override
    public void onPause() {
        if (!BuildConfig.DEBUG) {
            YandexMetrica.onPauseActivity(this);
        }
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken vkAccessToken) {
                //Пользователь успешно авторизовался
                Timber.d("Auth successfull: %s", vkAccessToken.email);
                if (vkAccessToken.email != null) {
                    VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_200")).executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            //noinspection unchecked
                            VKApiUser vkApiUser = ((VKList<VKApiUser>) response.parsedModel).get(0);
                            Timber.d("User name %s %s", vkApiUser.first_name, vkApiUser.last_name);

                            User user = new User();
                            user.network = User.NetworkType.VK;
                            user.fullName = vkApiUser.first_name + " " + vkApiUser.last_name;
                            user.firstName = vkApiUser.first_name;
                            user.lastName = vkApiUser.last_name;
                            user.avatar = vkApiUser.photo_200;

                            mPresenter.onUserLogined(user);
                        }

                        @Override
                        public void onError(VKError error) {
                            super.onError(error);
                            Toast.makeText(BaseActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(BaseActivity.this, R.string.error_login_no_email, Toast.LENGTH_SHORT).show();
                    VKSdk.logout();
                }
            }

            @Override
            public void onError(VKError error) {
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
                Timber.e(error.errorMessage);
                Toast.makeText(BaseActivity.this, error.errorMessage, Toast.LENGTH_SHORT).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initAndUpdateRemoteConfig() {
        //remote config
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Create Remote Config Setting to enable developer mode.
        // Fetching configs from the server is normally limited to 5 requests per hour.
        // Enabling developer mode allows many more requests to be made per hour, so developers
        // can test different config values during development.
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        // Set default Remote Config values. In general you should have in app defaults for all
        // values that you may configure using Remote Config later on. The idea is that you
        // use the in app defaults and when you need to adjust those defaults, you set an updated
        // value in the App Manager console. Then the next time you application fetches from the
        // server, the updated value will be used. You can set defaults via an xml file like done
        // here or you can set defaults inline by using one of the other setDefaults methods.S
        // [START set_default_values]
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        // cacheExpirationSeconds is set to cacheExpiration here, indicating that any previously
        // fetched and cached config would be considered expired because it would have been fetched
        // more than cacheExpiration seconds ago. Thus the next fetch would go to the server unless
        // throttling is in progress. The default expiration duration is 43200 (12 hours).
        long cacheExpiration = 20000; //default 43200
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Timber.d("Fetch Succeeded");
                // Once the config is successfully fetched it must be activated before newly fetched
                // values are returned.
                mFirebaseRemoteConfig.activateFetched();
            } else {
                Timber.d("Fetch Failed");
            }
        });
    }
}