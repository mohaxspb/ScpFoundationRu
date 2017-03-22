package ru.dante.scpfoundation.ui.activity;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.MenuItem;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vk.sdk.VKAccessToken;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import rx.android.schedulers.AndroidSchedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import ru.dante.scpfoundation.BuildConfig;
import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.mvp.contract.MainMvp;
import ru.dante.scpfoundation.ui.base.BaseDrawerActivity;
import ru.dante.scpfoundation.ui.dialog.NewVersionDialogFragment;
import ru.dante.scpfoundation.ui.dialog.TextSizeDialogFragment;
import ru.dante.scpfoundation.ui.fragment.ArticleFragment;
import ru.dante.scpfoundation.ui.fragment.FavoriteArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.Objects1ArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.Objects2ArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.Objects3ArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.ObjectsRuArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.OfflineArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.RatedArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.RecentArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.SiteSearchArticlesFragment;
import ru.dante.scpfoundation.util.prerate.PreRate;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static ru.dante.scpfoundation.ui.activity.LicenceActivity.EXTRA_SHOW_ABOUT;

public class MainActivity
        extends BaseDrawerActivity<MainMvp.View, MainMvp.Presenter>
        implements MainMvp.View {

    public static final String EXTRA_LINK = "EXTRA_LINK";

    public static void startActivity(Context context, String link) {
        Timber.d("startActivity: %s", link);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_LINK, link);
        context.startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Timber.d("onNewIntent");
        super.onNewIntent(intent);

        setIntent(intent);

        setDrawerItemFromIntent();

        mNavigationView.setCheckedItem(mCurrentSelectedDrawerItemId);
        onNavigationItemClicked(mCurrentSelectedDrawerItemId);
        setToolbarTitleByDrawerItemId(mCurrentSelectedDrawerItemId);
    }

    private void setDrawerItemFromIntent() {
        String link = getIntent().getStringExtra(EXTRA_LINK);
        Timber.d("setDrawerItemFromIntent: %s", link);
        switch (link) {
            case Constants.Urls.ABOUT_SCP:
                mCurrentSelectedDrawerItemId = (R.id.about);
                break;
            case Constants.Urls.NEWS:
                mCurrentSelectedDrawerItemId = (R.id.news);
                break;
            case Constants.Urls.MAIN:
            case Constants.Urls.RATE:
                mCurrentSelectedDrawerItemId = R.id.mostRatedArticles;
                break;
            case Constants.Urls.NEW_ARTICLES:
                mCurrentSelectedDrawerItemId = R.id.mostRecentArticles;
                break;
            case Constants.Urls.OBJECTS_1:
                mCurrentSelectedDrawerItemId = (R.id.objects_I);
                break;
            case Constants.Urls.OBJECTS_2:
                mCurrentSelectedDrawerItemId = (R.id.objects_II);
                break;
            case Constants.Urls.OBJECTS_3:
                mCurrentSelectedDrawerItemId = (R.id.objects_III);
                break;
            case Constants.Urls.OBJECTS_RU:
                mCurrentSelectedDrawerItemId = (R.id.objects_RU);
                break;
            case Constants.Urls.STORIES:
                mCurrentSelectedDrawerItemId = (R.id.stories);
                break;
            case Constants.Urls.FAVORITES:
                mCurrentSelectedDrawerItemId = (R.id.favorite);
                break;
            case Constants.Urls.OFFLINE:
                mCurrentSelectedDrawerItemId = (R.id.offline);
                break;
            case Constants.Urls.SEARCH:
                mCurrentSelectedDrawerItemId = (R.id.siteSearch);
                break;
            default:
                mCurrentSelectedDrawerItemId = SELECTED_DRAWER_ITEM_NONE;
                break;
        }
        getIntent().removeExtra(EXTRA_LINK);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra(EXTRA_LINK)) {
            setDrawerItemFromIntent();
        }

        if (getSupportFragmentManager().findFragmentById(mContent.getId()) == null) {
            onNavigationItemClicked(mCurrentSelectedDrawerItemId);
        }
        mNavigationView.setCheckedItem(mCurrentSelectedDrawerItemId);
        setToolbarTitleByDrawerItemId(mCurrentSelectedDrawerItemId);

        if (mMyPreferenceManager.getCurAppVersion() != BuildConfig.VERSION_CODE) {
            DialogFragment dialogFragment = NewVersionDialogFragment.newInstance(getString(R.string.new_version_features));
            dialogFragment.show(getFragmentManager(), NewVersionDialogFragment.TAG);
        }

        //FIXME test
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Timber.d("onAuthStateChanged:signed_in: %s", user.getUid());
            } else {
                // User is signed out
                Timber.d("onAuthStateChanged:signed_out");
            }
            // ...
        };
    }

    @Override
    protected int getDefaultNavItemId() {
        return getIntent().hasExtra(EXTRA_SHOW_ABOUT) ? R.id.about : R.id.mostRatedArticles;
    }

    @Override
    protected boolean isDrawerIndicatorEnabled() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
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
        setToolbarTitleByDrawerItemId(id);
        switch (id) {
            case R.id.about:
                mCurrentSelectedDrawerItemId = id;
                showFragment(ArticleFragment.newInstance(Constants.Urls.ABOUT_SCP),
                        ArticleFragment.TAG + "#" + Constants.Urls.ABOUT_SCP);
                return true;
            case R.id.news:
                mCurrentSelectedDrawerItemId = id;
                showFragment(ArticleFragment.newInstance(Constants.Urls.NEWS), ArticleFragment.TAG + "#" + Constants.Urls.NEWS);
                return true;
            case R.id.mostRatedArticles:
                mCurrentSelectedDrawerItemId = id;
                showFragment(RatedArticlesFragment.newInstance(), RatedArticlesFragment.TAG);
                return true;
            case R.id.mostRecentArticles:
                mCurrentSelectedDrawerItemId = id;
                showFragment(RecentArticlesFragment.newInstance(), RecentArticlesFragment.TAG);
                return true;
            case R.id.random_page:
                mPresenter.getRandomArticleUrl();
                return false;
            case R.id.objects_I:
                mCurrentSelectedDrawerItemId = id;
                showFragment(Objects1ArticlesFragment.newInstance(), Objects1ArticlesFragment.TAG);
                return true;
            case R.id.objects_II:
                mCurrentSelectedDrawerItemId = id;
                showFragment(Objects2ArticlesFragment.newInstance(), Objects2ArticlesFragment.TAG);
                return true;
            case R.id.objects_III:
                mCurrentSelectedDrawerItemId = id;
                showFragment(Objects3ArticlesFragment.newInstance(), Objects3ArticlesFragment.TAG);
                return true;
            case R.id.objects_RU:
                mCurrentSelectedDrawerItemId = id;
                showFragment(ObjectsRuArticlesFragment.newInstance(), ObjectsRuArticlesFragment.TAG);
                return true;
            case R.id.files:
                MaterialsActivity.startActivity(this);
                return false;
            case R.id.stories:
                mCurrentSelectedDrawerItemId = id;
                showFragment(ArticleFragment.newInstance(Constants.Urls.STORIES),
                        ArticleFragment.TAG + "#" + Constants.Urls.STORIES);
                return true;
            case R.id.favorite:
                mCurrentSelectedDrawerItemId = id;
                showFragment(FavoriteArticlesFragment.newInstance(), FavoriteArticlesFragment.TAG);
                return true;
            case R.id.offline:
                mCurrentSelectedDrawerItemId = id;
                showFragment(OfflineArticlesFragment.newInstance(), OfflineArticlesFragment.TAG);
                return true;
            case R.id.gallery:
//                GalleryActivity.startActivity(this);
                //FIXME test
                authWithCustomToken();
                return false;
            case R.id.siteSearch:
                mCurrentSelectedDrawerItemId = id;
                showFragment(SiteSearchArticlesFragment.newInstance(), SiteSearchArticlesFragment.TAG);
                return true;
            default:
                Timber.e("unexpected item ID");
                return true;
        }
    }

    //FIXME test
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private void authWithCustomToken() {
        Observable.<String>create(subscriber -> {
            OkHttpClient client = new OkHttpClient();
//            String url = "http://192.168.43.56:8080/scp-ru/MyServlet";
            String url = "http://192.168.0.93:8080/scp-ru/MyServlet";
            String params = "?provider=vk&token=" +
                    VKAccessToken.currentToken().accessToken +
                    "&email=" + VKAccessToken.currentToken().email +
                    "&id=" + VKAccessToken.currentToken().userId;
            Request request = new Request.Builder()
//                    .url("http://37.143.14.68:8080/scp-ru-1/MyServlet?provider=vk&token=" + VKAccessToken.currentToken().accessToken)
//                    .url("http://192.168.0.93:8080/scp-ru/MyServlet?provider=vk&token=" + VKAccessToken.currentToken().accessToken)
                    .url(url + params)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    subscriber.onError(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    subscriber.onNext(response.body().string());
                    subscriber.onCompleted();
                }
            });
        })
                .flatMap(response -> TextUtils.isEmpty(response) ? Observable.error(new IllegalArgumentException("empty token")) : Observable.just(response))
                .<AuthResult>flatMap(token -> Observable.create(subscriber -> {
                    Timber.d("token: %s", token);
                    mAuth.signInWithCustomToken(token).addOnCompleteListener(this, task -> {
                        Timber.d("signInWithCustomToken:onComplete: %s", task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Timber.e(task.getException(), "signInWithCustomToken");
                            //TODO
//                                            Toast.makeText(MainActivity.this, "Authentication failed.",
//                                                    Toast.LENGTH_SHORT).show();
                            subscriber.onError(new Throwable("error auth in Firebase with custom token"));
                        } else {
                            Timber.d("signInWithCustomToken task.getResult(): %s", task.getResult());
                            subscriber.onNext(task.getResult());
                            subscriber.onCompleted();
                        }
                    });
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            Timber.d("user: %s", result.getUser().getUid());
                        }
                        , error -> {
                            Timber.e(error);
                        }
                );
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        // ...
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        // ...
    }

    private void showFragment(Fragment fragmentToShow, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragments(transaction);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            transaction
                    .add(mContent.getId(), fragmentToShow, tag)
                    .commit();
        } else {
            transaction
                    .show(fragment)
                    .commit();
        }
    }

    /**
     * adds all found fragments to transaction via hide method
     */
    private void hideFragments(FragmentTransaction transaction) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments == null || fragments.isEmpty()) {
            return;
        }
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isAdded()) {
                transaction.hide(fragment);
            } else {
                Timber.e("fragment != null && fragment.isAdded() FALSE while switch fragments");
//                showError(new IllegalStateException("fragment != null && fragment.isAdded() FALSE while switch fragments"));
            }
        }
    }

    @Override
    public void setToolbarTitleByDrawerItemId(int id) {
        Timber.d("setToolbarTitleByDrawerItemId with id: %s", id);
        String title;
        switch (id) {
            case R.id.about:
                title = getString(R.string.drawer_item_1);
                break;
            case R.id.news:
                title = getString(R.string.drawer_item_2);
                break;
            case R.id.mostRatedArticles:
                title = getString(R.string.drawer_item_3);
                break;
            case R.id.mostRecentArticles:
                title = getString(R.string.drawer_item_4);
                break;
            case R.id.objects_I:
                title = getString(R.string.drawer_item_6);
                break;
            case R.id.objects_II:
                title = getString(R.string.drawer_item_7);
                break;
            case R.id.objects_III:
                title = getString(R.string.drawer_item_8);
                break;
            case R.id.objects_RU:
                title = getString(R.string.drawer_item_9);
                break;
            case R.id.stories:
                title = getString(R.string.drawer_item_11);
                break;
            case R.id.favorite:
                title = getString(R.string.drawer_item_12);
                break;
            case R.id.offline:
                title = getString(R.string.drawer_item_13);
                break;
            case R.id.siteSearch:
                title = getString(R.string.drawer_item_15);
                break;
            default:
                Timber.e("unexpected item ID");
                title = null;
                break;
        }
        assert mToolbar != null;
        if (title != null) {
            mToolbar.setTitle(title);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        PreRate.init(this, "neva.spb.rx@gmail.com", "Отзыв на SCP RU").showIfNeed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreRate.clearDialogIfOpen();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.text_size:
                BottomSheetDialogFragment fragmentDialogTextAppearance =
                        TextSizeDialogFragment.newInstance(TextSizeDialogFragment.TextSizeType.UI);
                fragmentDialogTextAppearance.show(getSupportFragmentManager(), TextSizeDialogFragment.TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}