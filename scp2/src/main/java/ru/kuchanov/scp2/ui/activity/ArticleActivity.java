package ru.kuchanov.scp2.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import ru.kuchanov.scp2.Constants;
import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.mvp.contract.ArticleScreenMvp;
import ru.kuchanov.scp2.ui.adapter.ArticlesPagerAdapter;
import ru.kuchanov.scp2.ui.base.BaseDrawerActivity;
import ru.kuchanov.scp2.ui.dialog.TextSizeDialogFragment;
import ru.kuchanov.scp2.ui.fragment.ArticleFragment;
import ru.kuchanov.scp2.util.IntentUtils;
import timber.log.Timber;

public class ArticleActivity
        extends BaseDrawerActivity<ArticleScreenMvp.View, ArticleScreenMvp.Presenter>
        implements ArticleScreenMvp.View, ArticleFragment.ToolbarStateSetter {

    public static final String EXTRA_ARTICLES_URLS_LIST = "EXTRA_ARTICLES_URLS_LIST";
    public static final String EXTRA_POSITION = "EXTRA_POSITION";

    @BindView(R.id.content)
    ViewPager mViewPager;

    private int mCurPosition;
    private List<String> mUrls;

    private ArticlesPagerAdapter mAdapter;

    public static void startActivity(Context context, ArrayList<String> urls, int position) {
        Timber.d("startActivity: urls.size() %s, position: %s", urls.size(), position);
        Intent intent = new Intent(context, ArticleActivity.class);
        intent.putExtra(EXTRA_ARTICLES_URLS_LIST, urls);
        intent.putExtra(EXTRA_POSITION, position);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String url) {
        Timber.d("startActivity: %s", url);
        startActivity(context, new ArrayList<String>() {{
            add(url);
        }}, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate");
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra(EXTRA_ARTICLES_URLS_LIST)) {
            mUrls = getIntent().getStringArrayListExtra(EXTRA_ARTICLES_URLS_LIST);
            mPresenter.setArticlesUrls(mUrls);
            mCurPosition = getIntent().getIntExtra(EXTRA_POSITION, 0);
        }
        mAdapter = new ArticlesPagerAdapter(getSupportFragmentManager());
        mAdapter.setData(getIntent().getStringArrayListExtra(EXTRA_ARTICLES_URLS_LIST));
        mViewPager.setAdapter(mAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurPosition = position;
            }
        });

        mViewPager.setCurrentItem(mCurPosition);
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
        return R.layout.activity_article;
    }

    @Override
    protected void callInjections() {
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    protected int getMenuResId() {
        return R.menu.menu_article;
    }

    @NonNull
    @Override
    public ArticleScreenMvp.Presenter createPresenter() {
        return mPresenter;
    }

    @Override
    public void onNavigationItemClicked(int id) {
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
                //TODO
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
                //TODO launch new activity
                break;
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
                //TODO
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Timber.d("onOptionsItemSelected with id: %s", item);
        switch (item.getItemId()) {
            case android.R.id.home:
                //TODO move to abstract
                onBackPressed();
                return true;
            case R.id.night_mode_item:
                mMyPreferenceManager.setIsNightMode(!mMyPreferenceManager.isNightMode());
                recreate();
                return true;
            case R.id.text_size:
                TextSizeDialogFragment fragmentDialogTextAppearance = TextSizeDialogFragment.newInstance();
                fragmentDialogTextAppearance.show(getFragmentManager(), TextSizeDialogFragment.TAG);
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
            case R.id.menuItemShare:
                IntentUtils.shareUrl(mUrls.get(mCurPosition));
                return true;
            case R.id.menuItemBrowser:
                IntentUtils.openUrl(mUrls.get(mCurPosition));
                return true;
            case R.id.menuItemFavorite:
                mPresenter.toggleFavorite(mUrls.get(mCurPosition));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(String title) {
        if (mToolbar != null) {
            mToolbar.setTitle(title);
        }
    }

    @Override
    public void setFavoriteState(boolean isInFavorite) {
        Timber.d("setFavoriteState: %s", isInFavorite);
        if (mToolbar != null && mToolbar.getMenu() != null) {
            MenuItem item = mToolbar.getMenu().findItem(R.id.menuItemFavorite);
            if (item != null) {
                item.setIcon(isInFavorite ? R.drawable.ic_favorite_white_24dp : R.drawable.ic_favorite_border_white_24dp);
                item.setTitle(isInFavorite ? R.string.favorites_remove : R.string.favorites_add);
            }
        }
    }
}