package ru.kuchanov.scpcore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import ru.kuchanov.scpcore.BaseApplication;
import ru.kuchanov.scpcore.Constants;
import ru.kuchanov.scpcore.R;
import ru.kuchanov.scpcore.db.model.Article;
import ru.kuchanov.scpcore.db.model.ArticleTag;
import ru.kuchanov.scpcore.monetization.util.MyAdListener;
import ru.kuchanov.scpcore.mvp.base.MonetizationActions;
import ru.kuchanov.scpcore.mvp.contract.DataSyncActions;
import ru.kuchanov.scpcore.mvp.contract.TagsScreenMvp;
import ru.kuchanov.scpcore.ui.base.BaseDrawerActivity;
import ru.kuchanov.scpcore.ui.dialog.TextSizeDialogFragment;
import ru.kuchanov.scpcore.ui.fragment.ArticleFragment;
import ru.kuchanov.scpcore.ui.fragment.MaterialsAllFragment;
import ru.kuchanov.scpcore.ui.fragment.TagsSearchFragment;
import ru.kuchanov.scpcore.ui.fragment.TagsSearchResultsArticlesFragment;
import timber.log.Timber;

import static ru.kuchanov.scpcore.ui.activity.MainActivity.EXTRA_SHOW_DISABLE_ADS;

public class TagSearchActivity
        extends BaseDrawerActivity<TagsScreenMvp.View, TagsScreenMvp.Presenter>
        implements TagsScreenMvp.View, ArticleFragment.ToolbarStateSetter, TagsSearchFragment.ShowTagsSearchResults {

    public static final String EXTRA_TAGS = "EXTRA_TAGS";

    private List<ArticleTag> mTags;

    public static void startActivity(Context context, List<ArticleTag> tagList) {
        Timber.d("startActivity");
        if (context instanceof MonetizationActions) {
            MonetizationActions monetizationActions = (MonetizationActions) context;
            if (monetizationActions.isTimeToShowAds()) {
                if (monetizationActions.isAdsLoaded()) {
                    monetizationActions.showInterstitial(new MyAdListener() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            Intent intent = new Intent(context, TagSearchActivity.class);
                            intent.putExtra(EXTRA_TAGS, (ArrayList<String>) ArticleTag.getStringsFromTags(tagList));
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
        Intent intent = new Intent(context, TagSearchActivity.class);
        intent.putExtra(EXTRA_TAGS, (ArrayList<String>) ArticleTag.getStringsFromTags(tagList));
        context.startActivity(intent);
    }

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
                            Intent intent = new Intent(context, TagSearchActivity.class);
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
        Intent intent = new Intent(context, TagSearchActivity.class);
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

        if (getIntent().hasExtra(EXTRA_TAGS)) {
            mTags = ArticleTag.getTagsFromStringList(getIntent().getStringArrayListExtra(EXTRA_TAGS));
        }

        if (savedInstanceState == null) {
            if (mTags != null && !mTags.isEmpty()) {
                showResults(null, mTags);
            } else {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.content, TagsSearchFragment.newInstance(ArticleTag.getStringsFromTags(mTags)), TagsSearchFragment.TAG)
                        .addToBackStack(MaterialsAllFragment.TAG)
                        .commit();
            }
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
        BaseApplication.getAppComponent().inject(this);
    }

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
            MaterialsActivity.startActivity(this);
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
            getSupportFragmentManager().popBackStackImmediate(TagsSearchFragment.TAG, 0);

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
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void showResults(List<Article> data, List<ArticleTag> tags) {
        Fragment fragmentResults = TagsSearchResultsArticlesFragment.newInstance(data, tags);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, fragmentResults, TagsSearchResultsArticlesFragment.TAG)
                .addToBackStack(TagsSearchResultsArticlesFragment.TAG)
                .commit();
    }
}