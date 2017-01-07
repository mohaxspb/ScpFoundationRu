package ru.kuchanov.scp2.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;

import java.util.ArrayList;

import ru.kuchanov.scp2.Constants;
import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.mvp.contract.ArticleScreenMvp;
import ru.kuchanov.scp2.ui.base.BaseDrawerActivity;
import timber.log.Timber;

public class ArticleActivity
        extends BaseDrawerActivity<ArticleScreenMvp.View, ArticleScreenMvp.Presenter>
        implements ArticleScreenMvp.View {

    public static final String EXTRA_ARTICLES_URLS_LIST = "EXTRA_ARTICLES_URLS_LIST";
    public static final String EXTRA_POSITION = "EXTRA_POSITION";

    private int mCurPosition;

    public static void startActivity(Context context, ArrayList<String> urls, int position) {
        Intent intent = new Intent(context, ArticleActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(EXTRA_ARTICLES_URLS_LIST, data);
        intent.putExtra(EXTRA_ARTICLES_URLS_LIST, urls);
        intent.putExtra(EXTRA_POSITION, position);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String url) {
        startActivity(context, new ArrayList<String>() {{
            add(url);
        }}, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra(EXTRA_ARTICLES_URLS_LIST)) {
            mPresenter.setArticlesUrls(getIntent().getStringArrayListExtra(EXTRA_ARTICLES_URLS_LIST));
            mCurPosition = getIntent().getIntExtra(EXTRA_POSITION, 0);
        }
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
        return R.menu.menu_main;
    }

    @NonNull
    @Override
    public ArticleScreenMvp.Presenter createPresenter() {
        return mPresenter;
    }

    @Override
    public void onNavigationItemClicked(int id) {
        Timber.d("onNavigationItemClicked with id: %s", id);
        String link;
        switch (id) {
            case R.id.about:
                link = Constants.Urls.ABOUT_SCP;
                break;
            case R.id.news:
                link = Constants.Urls.ABOUT_SCP;
                break;
            case R.id.mostRatedArticles:
                link = Constants.Urls.ABOUT_SCP;
                break;
            case R.id.mostRecentArticles:
                link = Constants.Urls.ABOUT_SCP;
                break;
            case R.id.random_page:
                //TODO
                break;
            case R.id.objects_I:
                link = Constants.Urls.ABOUT_SCP;
                break;
            case R.id.objects_II:
                link = Constants.Urls.ABOUT_SCP;
                break;
            case R.id.objects_III:
                link = Constants.Urls.ABOUT_SCP;
                break;
            case R.id.objects_RU:
                link = Constants.Urls.ABOUT_SCP;
                break;
            case R.id.files:
                //TODO launch new activity
                break;
            case R.id.stories:
                link = Constants.Urls.ABOUT_SCP;
                break;
            case R.id.favorite:
                link = Constants.Urls.ABOUT_SCP;
                break;
            case R.id.offline:
                link = Constants.Urls.ABOUT_SCP;
                break;
            case R.id.gallery:
                //TODO
                break;
            case R.id.site_search:
                link = Constants.Urls.ABOUT_SCP;
                break;
            default:
                Timber.e("unexpected item ID");
                break;
        }
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