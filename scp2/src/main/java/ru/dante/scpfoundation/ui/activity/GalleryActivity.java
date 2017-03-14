package ru.dante.scpfoundation.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.db.model.RealmString;
import ru.dante.scpfoundation.db.model.VkImage;
import ru.dante.scpfoundation.monetization.util.MyAdListener;
import ru.dante.scpfoundation.mvp.base.MonetizationActions;
import ru.dante.scpfoundation.mvp.contract.GalleryScreenMvp;
import ru.dante.scpfoundation.ui.adapter.ImagesPagerAdapter;
import ru.dante.scpfoundation.ui.base.BaseDrawerActivity;
import ru.dante.scpfoundation.ui.dialog.SubscriptionsFragmentDialog;
import ru.dante.scpfoundation.ui.fragment.FragmentMaterialsAll;
import ru.dante.scpfoundation.util.IntentUtils;
import timber.log.Timber;

public class GalleryActivity
        extends BaseDrawerActivity<GalleryScreenMvp.View, GalleryScreenMvp.Presenter>
        implements GalleryScreenMvp.View {

    public static final String EXTRA_SHOW_DISABLE_ADS = "EXTRA_SHOW_DISABLE_ADS";

    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.progressCenter)
    View mProgressContainer;
    @BindView(R.id.placeHolder)
    View mPlaceHolder;
    @BindView(R.id.refresh)
    Button mRefresh;

    private ImagesPagerAdapter mAdapter;

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
                    });
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
        Timber.d("onCreate");
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra(EXTRA_SHOW_DISABLE_ADS)) {
            Snackbar snackbar = Snackbar.make(mRoot, R.string.remove_ads, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.yes_bliad, v -> {
                snackbar.dismiss();
                BottomSheetDialogFragment subsDF = SubscriptionsFragmentDialog.newInstance();
                subsDF.show(getSupportFragmentManager(), subsDF.getTag());

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, Constants.Firebase.Analitics.StartScreen.MAIN_TO_ARTICLE_SNACK_BAR);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            });
            snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.material_amber_500));
            snackbar.show();
        }

        if (mToolbar != null) {
            mToolbar.setTitle(R.string.gallery);
        }
        mAdapter = new ImagesPagerAdapter();
        mViewPager.setAdapter(mAdapter);
//        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
//            @Override
//            public void onPageSelected(int position) {
//                mCurPos
//            }
//        });

        if (mPresenter.getData() != null) {
            mAdapter.setData(mPresenter.getData());
        } else {
            mPresenter.getDataFromDb();
            mPresenter.updateData();
        }
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
                List<RealmString> allUrls = mAdapter.getData().get(mViewPager.getCurrentItem()).allUrls;
                IntentUtils.shareUrl(allUrls.get(allUrls.size() - 1).getVal());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showData(List<VkImage> data) {
        Timber.d("showData: %s", data.size());
        mAdapter.setData(data);
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
//        showEmptyPlaceholder(false);
//        showCenterProgress(true);
        mPresenter.updateData();
    }
}