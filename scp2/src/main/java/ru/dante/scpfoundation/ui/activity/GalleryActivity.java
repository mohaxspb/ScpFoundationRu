package ru.dante.scpfoundation.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import ru.dante.scpfoundation.BuildConfig;
import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.db.model.RealmString;
import ru.dante.scpfoundation.db.model.VkImage;
import ru.dante.scpfoundation.monetization.util.MyAdListener;
import ru.dante.scpfoundation.mvp.base.MonetizationActions;
import ru.dante.scpfoundation.mvp.contract.GalleryScreenMvp;
import ru.dante.scpfoundation.ui.adapter.ImagesPagerAdapter;
import ru.dante.scpfoundation.ui.adapter.RecyclerAdapterImages;
import ru.dante.scpfoundation.ui.base.BaseDrawerActivity;
import ru.dante.scpfoundation.ui.fragment.FragmentMaterialsAll;
import ru.dante.scpfoundation.util.IntentUtils;
import ru.dante.scpfoundation.util.StorageUtils;
import ru.dante.scpfoundation.util.SystemUtils;
import timber.log.Timber;

public class GalleryActivity
        extends BaseDrawerActivity<GalleryScreenMvp.View, GalleryScreenMvp.Presenter>
        implements GalleryScreenMvp.View {

    public static final String EXTRA_SHOW_DISABLE_ADS = "EXTRA_SHOW_DISABLE_ADS";

    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.progressCenter)
    View mProgressContainer;
    @BindView(R.id.placeHolder)
    View mPlaceHolder;
    @BindView(R.id.refresh)
    Button mRefresh;

    @BindView(R.id.banner)
    AdView mAdView;

    private ImagesPagerAdapter mAdapter;
    private RecyclerAdapterImages mRecyclerAdapter;

    public static void startActivity(Context context) {
        Timber.d("startActivity");
        if (context instanceof MonetizationActions) {
            MonetizationActions monetizationActions = (MonetizationActions) context;
            if (monetizationActions.isTimeToShowAds()) {
                if (monetizationActions.isAdsLoaded()) {
                    monetizationActions.showInterstitial(new MyAdListener() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            Intent intent = new Intent(context, GalleryActivity.class);
                            intent.putExtra(EXTRA_SHOW_DISABLE_ADS, true);
                            context.startActivity(intent);
                        }
                    }, true);
                    return;
                } else {
                    Timber.d("Ads not loaded yet");
                }
            } else {
                Timber.d("it's not time to showInterstitial ads");
            }
        } else {
            Timber.wtf("context IS NOT instanceof MonetizationActions");
        }
        Intent intent = new Intent(context, GalleryActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra(EXTRA_SHOW_DISABLE_ADS)) {
            showSnackBarWithAction(Constants.Firebase.CallToActionReason.REMOVE_ADS);
            getIntent().removeExtra(EXTRA_SHOW_DISABLE_ADS);
        }

        if (mToolbar != null) {
            mToolbar.setTitle(R.string.gallery);
        }
        mAdapter = new ImagesPagerAdapter();
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position %  FirebaseRemoteConfig.getInstance()
                        .getLong(Constants.Firebase.RemoteConfigKeys.NUM_OF_GALLERY_PHOTOS_BETWEEN_INTERSITIAL) == 0) {
                    showInterstitial(new MyAdListener() {
                        @Override
                        public void onAdClosed() {
                            showSnackBarWithAction(Constants.Firebase.CallToActionReason.REMOVE_ADS);
                            requestNewInterstitial();
                        }
                    }, false);
                }
            }
        });

        mRecyclerAdapter = new RecyclerAdapterImages();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerAdapter.setImageClickListener((position, v) -> mViewPager.setCurrentItem(position));

        //ads
        initAds();

        if (mPresenter.getData() != null) {
            mAdapter.setData(mPresenter.getData());
            mRecyclerAdapter.setData(mPresenter.getData());
        } else {
            mPresenter.getDataFromDb();
            mPresenter.updateData();
        }
    }

    @Override
    public void initAds() {
        super.initAds();

        if (!isAdsLoaded()) {
            requestNewInterstitial();
        }

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
        mAdView.loadAd(adRequest.build());
    }

    @Override
    protected boolean isDrawerIndicatorEnabled() {
        return false;
    }

    @Override
    protected int getDefaultNavItemId() {
        return SELECTED_DRAWER_ITEM_NONE;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_gallery;
    }

    @Override
    protected void callInjections() {
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    protected int getMenuResId() {
        return R.menu.menu_gallery;
    }

    @Override
    public boolean onNavigationItemClicked(int id) {
        Timber.d("onNavigationItemClicked with id: %s", id);
        String link = null;
        switch (id) {
            case R.id.about:
                link = Constants.Urls.ABOUT_SCP;
                break;
            case R.id.news:
                link = Constants.Urls.NEWS;
                break;
            case R.id.mostRatedArticles:
                link = Constants.Urls.RATE;
                break;
            case R.id.mostRecentArticles:
                link = Constants.Urls.NEW_ARTICLES;
                break;
            case R.id.random_page:
                mPresenter.getRandomArticleUrl();
                break;
            case R.id.objects_I:
                link = Constants.Urls.OBJECTS_1;
                break;
            case R.id.objects_II:
                link = Constants.Urls.OBJECTS_2;
                break;
            case R.id.objects_III:
                link = Constants.Urls.OBJECTS_3;
                break;
            case R.id.objects_RU:
                link = Constants.Urls.OBJECTS_RU;
                break;
            case R.id.files:
                getSupportFragmentManager().popBackStackImmediate(FragmentMaterialsAll.TAG, 0);
                return false;
            case R.id.stories:
                link = Constants.Urls.STORIES;
                break;
            case R.id.favorite:
                link = Constants.Urls.FAVORITES;
                break;
            case R.id.offline:
                link = Constants.Urls.OFFLINE;
                break;
            case R.id.gallery:
                //nothing to do
                break;
            case R.id.siteSearch:
                link = Constants.Urls.SEARCH;
                break;
            default:
                Timber.e("unexpected item ID");
                break;
        }
        if (link != null) {
            MainActivity.startActivity(this, link);
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Timber.d("onOptionsItemSelected with id: %s", item);
        switch (item.getItemId()) {
            case R.id.share:
                if (mAdapter.getData().isEmpty()) {
                    return true;
                }
                List<RealmString> allUrls = mAdapter.getData().get(mViewPager.getCurrentItem()).allUrls;
                IntentUtils.shareUrl(allUrls.get(allUrls.size() - 1).getVal());
                return true;
            case R.id.save_image:
                Bitmap image = mAdapter.getImage();
                if (image == null)
                    return true;
                if (StorageUtils.saveImageToGallery(this, image))
                    Toast.makeText(GalleryActivity.this,
                            R.string.image_saved, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(GalleryActivity.this,
                            R.string.image_saving_error, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showData(List<VkImage> data) {
        Timber.d("showData: %s", data.size());
        mAdapter.setData(data);
        mRecyclerAdapter.setData(data);
    }

    @Override
    public void showCenterProgress(boolean show) {
        Timber.d("showCenterProgress: %s", show);
        mProgressContainer.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showEmptyPlaceholder(boolean show) {
        Timber.d("showEmptyPlaceholder: %s", show);
        mPlaceHolder.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.refresh)
    public void onRefreshClicked() {
        Timber.d("onRefreshClicked");
        mPresenter.updateData();
    }
}