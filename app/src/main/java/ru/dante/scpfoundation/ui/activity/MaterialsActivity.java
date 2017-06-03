package ru.dante.scpfoundation.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import java.util.Arrays;
import java.util.List;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.monetization.util.MyAdListener;
import ru.dante.scpfoundation.mvp.base.MonetizationActions;
import ru.dante.scpfoundation.mvp.contract.DataSyncActions;
import ru.dante.scpfoundation.mvp.contract.MaterialsScreenMvp;
import ru.dante.scpfoundation.ui.base.BaseDrawerActivity;
import ru.dante.scpfoundation.ui.dialog.TextSizeDialogFragment;
import ru.dante.scpfoundation.ui.fragment.ArticleFragment;
import ru.dante.scpfoundation.ui.fragment.FragmentMaterialsAll;
import ru.dante.scpfoundation.ui.fragment.MaterialsArchiveFragment;
import ru.dante.scpfoundation.ui.fragment.MaterialsExperimentsFragment;
import ru.dante.scpfoundation.ui.fragment.MaterialsIncidentsFragment;
import ru.dante.scpfoundation.ui.fragment.MaterialsInterviewsFragment;
import ru.dante.scpfoundation.ui.fragment.MaterialsJokesFragment;
import ru.dante.scpfoundation.ui.fragment.MaterialsOtherFragment;
import timber.log.Timber;

import static ru.dante.scpfoundation.ui.activity.MainActivity.EXTRA_SHOW_DISABLE_ADS;

public class MaterialsActivity
        extends BaseDrawerActivity<MaterialsScreenMvp.View, MaterialsScreenMvp.Presenter>
        implements MaterialsScreenMvp.View, ArticleFragment.ToolbarStateSetter {

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
                            Intent intent = new Intent(context, MaterialsActivity.class);
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
        Intent intent = new Intent(context, MaterialsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra(EXTRA_SHOW_DISABLE_ADS)) {
            showSnackBarWithAction(Constants.Firebase.CallToActionReason.REMOVE_ADS);
            getIntent().removeExtra(EXTRA_SHOW_DISABLE_ADS);

            @DataSyncActions.ScoreAction
            String action = DataSyncActions.ScoreAction.INTERSTITIAL_SHOWN;
            mPresenter.updateUserScoreForScoreAction(action);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, FragmentMaterialsAll.newInstance(), FragmentMaterialsAll.TAG)
                    .addToBackStack(FragmentMaterialsAll.TAG)
                    .commit();
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
        return R.layout.activity_materials;
    }

    @Override
    protected void callInjections() {
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    protected int getMenuResId() {
        return R.menu.menu_main;
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
            case R.id.objects_IV:
                link = Constants.Urls.OBJECTS_4;
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
                GalleryActivity.startActivity(this);
                break;
            case R.id.siteSearch:
                link = Constants.Urls.SEARCH;
                break;
            case R.id.tagsSearch:
                TagSearchActivity.startActivity(this);
                return true;
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
            case R.id.text_size:
                BottomSheetDialogFragment fragmentDialogTextAppearance =
                        TextSizeDialogFragment.newInstance(TextSizeDialogFragment.TextSizeType.ALL);
                fragmentDialogTextAppearance.show(getSupportFragmentManager(), TextSizeDialogFragment.TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setTitle(String title) {
        if (mToolbar != null) {
            mToolbar.setTitle(title);
        }
    }

    @Override
    public void setFavoriteState(boolean isInFavorite) {
        //nothing to do
    }

    @Override
    public void onMaterialsListItemClicked(int position) {
        List<String> materials = Arrays.asList(getResources().getStringArray(R.array.materials_titles));
        Timber.d("onMaterialsListItemClicked: %s", materials.get(position));

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = MaterialsExperimentsFragment.newInstance();
                fragmentTransaction.replace(R.id.content, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 1:
                fragment = MaterialsIncidentsFragment.newInstance();
                fragmentTransaction.replace(R.id.content, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 2:
                fragment = MaterialsInterviewsFragment.newInstance();
                fragmentTransaction.replace(R.id.content, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 3:
                fragment = MaterialsJokesFragment.newInstance();
                fragmentTransaction.replace(R.id.content, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 4:
                fragment = MaterialsArchiveFragment.newInstance();
                fragmentTransaction.replace(R.id.content, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 5:
                fragment = MaterialsOtherFragment.newInstance();
                fragmentTransaction.replace(R.id.content, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 6:
                ArticleActivity.startActivity(this, Constants.Urls.LEAKS);
                break;
            default:
                throw new RuntimeException("unexpected position in materials list");
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }
}