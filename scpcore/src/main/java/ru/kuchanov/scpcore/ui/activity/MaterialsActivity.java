package ru.kuchanov.scpcore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import java.util.Arrays;
import java.util.List;

import ru.kuchanov.scpcore.BaseApplication;
import ru.kuchanov.scpcore.Constants;
import ru.kuchanov.scpcore.R;
import ru.kuchanov.scpcore.monetization.util.MyAdListener;
import ru.kuchanov.scpcore.mvp.base.MonetizationActions;
import ru.kuchanov.scpcore.mvp.contract.DataSyncActions;
import ru.kuchanov.scpcore.mvp.contract.MaterialsScreenMvp;
import ru.kuchanov.scpcore.ui.base.BaseDrawerActivity;
import ru.kuchanov.scpcore.ui.dialog.TextSizeDialogFragment;
import ru.kuchanov.scpcore.ui.fragment.ArticleFragment;
import ru.kuchanov.scpcore.ui.fragment.MaterialsAllFragment;
import ru.kuchanov.scpcore.ui.fragment.MaterialsArchiveFragment;
import ru.kuchanov.scpcore.ui.fragment.MaterialsExperimentsFragment;
import ru.kuchanov.scpcore.ui.fragment.MaterialsIncidentsFragment;
import ru.kuchanov.scpcore.ui.fragment.MaterialsInterviewsFragment;
import ru.kuchanov.scpcore.ui.fragment.MaterialsJokesFragment;
import ru.kuchanov.scpcore.ui.fragment.MaterialsOtherFragment;
import timber.log.Timber;

import static ru.kuchanov.scpcore.ui.activity.MainActivity.EXTRA_SHOW_DISABLE_ADS;

public abstract class MaterialsActivity
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
                    .add(R.id.content, MaterialsAllFragment.newInstance(), MaterialsAllFragment.TAG)
                    .addToBackStack(MaterialsAllFragment.TAG)
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

//    @Override
//    protected void callInjections() {
//        BaseApplication.getAppComponent().inject(this);
//    }

    @Override
    protected int getMenuResId() {
        return R.menu.menu_main;
    }

    @Override
    public boolean onNavigationItemClicked(int id) {
        Timber.d("onNavigationItemClicked with id: %s", id);
        String link = null;
        if (id == R.id.about) {
            link = Constants.Urls.ABOUT_SCP;

        } else if (id == R.id.news) {
            link = Constants.Urls.NEWS;

        } else if (id == R.id.mostRatedArticles) {
            link = Constants.Urls.RATE;

        } else if (id == R.id.mostRecentArticles) {
            link = Constants.Urls.NEW_ARTICLES;

        } else if (id == R.id.random_page) {
            mPresenter.getRandomArticleUrl();

        } else if (id == R.id.objects_I) {
            link = Constants.Urls.OBJECTS_1;

        } else if (id == R.id.objects_II) {
            link = Constants.Urls.OBJECTS_2;

        } else if (id == R.id.objects_III) {
            link = Constants.Urls.OBJECTS_3;

        } else if (id == R.id.objects_IV) {
            link = Constants.Urls.OBJECTS_4;

        } else if (id == R.id.objects_RU) {
            link = Constants.Urls.OBJECTS_RU;

        } else if (id == R.id.files) {
            getSupportFragmentManager().popBackStackImmediate(MaterialsAllFragment.TAG, 0);
            return false;
        } else if (id == R.id.stories) {
            link = Constants.Urls.STORIES;

        } else if (id == R.id.favorite) {
            link = Constants.Urls.FAVORITES;

        } else if (id == R.id.offline) {
            link = Constants.Urls.OFFLINE;

        } else if (id == R.id.gallery) {
            GalleryActivity.startActivity(this);

        } else if (id == R.id.siteSearch) {
            link = Constants.Urls.SEARCH;

        } else if (id == R.id.tagsSearch) {
            TagSearchActivity.startActivity(this);
            return true;
        } else {
            Timber.e("unexpected item ID");

        }
        if (link != null) {
            MainActivity.startActivity(this, link);
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Timber.d("onOptionsItemSelected with id: %s", item);
        int i = item.getItemId();
        if (i == R.id.text_size) {
            BottomSheetDialogFragment fragmentDialogTextAppearance =
                    TextSizeDialogFragment.newInstance(TextSizeDialogFragment.TextSizeType.ALL);
            fragmentDialogTextAppearance.show(getSupportFragmentManager(), TextSizeDialogFragment.TAG);
            return true;
        } else {
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