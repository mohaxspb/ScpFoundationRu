package ru.dante.scpfoundation.ui.base;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import butterknife.BindView;
import ru.dante.scpfoundation.BuildConfig;
import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.api.model.remoteconfig.LevelsJson;
import ru.dante.scpfoundation.db.model.User;
import ru.dante.scpfoundation.monetization.util.InappHelper;
import ru.dante.scpfoundation.mvp.contract.DrawerMvp;
import ru.dante.scpfoundation.ui.activity.ArticleActivity;
import ru.dante.scpfoundation.ui.dialog.SubscriptionsFragmentDialog;
import ru.dante.scpfoundation.ui.holder.HeaderViewHolderLogined;
import ru.dante.scpfoundation.ui.holder.HeaderViewHolderUnlogined;
import ru.dante.scpfoundation.util.SecureUtils;
import timber.log.Timber;

/**
 * Created by mohax on 02.01.2017.
 * <p>
 * for scp_ru
 */
public abstract class BaseDrawerActivity<V extends DrawerMvp.View, P extends DrawerMvp.Presenter<V>>
        extends BaseActivity<V, P>
        implements DrawerMvp.View, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final int REQUEST_CODE_INAPP = 1421;

    private static final String STATE_CUR_DRAWER_ITEM_ID = "STATE_CUR_DRAWER_ITEM_ID";
    protected static final int SELECTED_DRAWER_ITEM_NONE = -1;

    @Inject
    Gson mGson;

    @BindView(R.id.root)
    protected DrawerLayout mDrawerLayout;
    @BindView(R.id.navigationView)
    protected NavigationView mNavigationView;

    protected MaterialDialog dialog;

    protected ActionBarDrawerToggle mDrawerToggle;

    protected int mCurrentSelectedDrawerItemId;

    protected abstract int getDefaultNavItemId();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CUR_DRAWER_ITEM_ID, mCurrentSelectedDrawerItemId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrentSelectedDrawerItemId = getDefaultNavItemId();

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);

            actionBar.setDisplayHomeAsUpEnabled(true);

            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {
                public void onDrawerClosed(View view) {
                    supportInvalidateOptionsMenu();
                }

                public void onDrawerOpened(View drawerView) {
                }
            };
            mDrawerToggle.setDrawerIndicatorEnabled(isDrawerIndicatorEnabled());

            mDrawerLayout.addDrawerListener(mDrawerToggle);
        }

        mNavigationView.setNavigationItemSelectedListener(item -> {
            mPresenter.onNavigationItemClicked(item.getItemId());
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return onNavigationItemClicked(item.getItemId());
        });

        if (savedInstanceState != null) {
            mCurrentSelectedDrawerItemId = savedInstanceState.getInt(STATE_CUR_DRAWER_ITEM_ID);
        }
        if (mCurrentSelectedDrawerItemId != SELECTED_DRAWER_ITEM_NONE) {
            mNavigationView.setCheckedItem(mCurrentSelectedDrawerItemId);
        } else {
            mNavigationView.getMenu().setGroupCheckable(0, false, true);
        }

//        onGetUserFromDB(mPresenter.getUser());
        updateUser(mPresenter.getUser());
    }

    /**
     * @return true if need to show hamburger. False if want show arrow
     */
    protected abstract boolean isDrawerIndicatorEnabled();

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Timber.d("onOptionsItemSelected with id: %s", item);
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isDrawerIndicatorEnabled()) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                } else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onReceiveRandomUrl(String url) {
        ArticleActivity.startActivity(this, url);
    }

    @Override
    public void showProgressDialog(boolean show) {
        if (show) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
            builder.title(R.string.dialog_random_page_title);
            builder.content(R.string.dialog_random_page_message);
            builder.progress(true, 0);
            builder.cancelable(false);
            dialog = builder.build();
            dialog.show();
        } else if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void updateUser(User user) {
        Timber.d("updateUser: %s", user);
        if (user != null) {
            for (int i = 0; i < mNavigationView.getHeaderCount(); i++) {
                mNavigationView.removeHeaderView(mNavigationView.getHeaderView(i));
            }
            View headerLogined = LayoutInflater.from(this).inflate(R.layout.drawer_header_logined, mNavigationView, false);
            mNavigationView.addHeaderView(headerLogined);

            HeaderViewHolderLogined headerViewHolder = new HeaderViewHolderLogined(headerLogined);

            headerViewHolder.logout.setOnClickListener(view -> {
                        mPresenter.logoutUser();
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
            );

            headerViewHolder.levelUp.setOnClickListener(view -> {
                showError(new IllegalStateException("not implemented"));
                InappHelper.getInappsListToBuyObserveble(view.getContext(), getIInAppBillingService()).subscribe(
                        items -> new MaterialDialog.Builder(view.getContext())
                                .title(R.string.dialog_level_up_title)
                                .content(R.string.dialog_level_up_content)
                                .neutralText(android.R.string.cancel)
                                .positiveText(R.string.dialog_level_up_ok_text)
                                .onPositive((dialog1, which) -> {
                                    try {
                                        Bundle buyIntentBundle = getIInAppBillingService().getBuyIntent(
                                                3,
                                                getPackageName(),
                                                items.get(0).productId,
                                                "inapp",
                                                String.valueOf(System.currentTimeMillis()));
                                        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                                        if (pendingIntent != null) {
                                            startIntentSenderForResult(pendingIntent.getIntentSender(), REQUEST_CODE_INAPP, new Intent(), 0, 0, 0, null);
                                        }
                                    } catch (Exception e) {
                                        Timber.e(e, "error ");
                                        Snackbar.make(mRoot, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                    }
                                })
                                .show(),
                        this::showError
                );
            });

            headerViewHolder.inapp.setOnClickListener(view -> {
                BottomSheetDialogFragment subsDF = SubscriptionsFragmentDialog.newInstance();
                subsDF.show(getSupportFragmentManager(), subsDF.getTag());

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, Constants.Firebase.Analitics.StartScreen.DRAWER_HEADER_LOGINED);
                FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            });

            headerViewHolder.name.setText(user.fullName);
            Glide.with(this)
                    .load(user.avatar)
                    .asBitmap()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(headerViewHolder.avatar) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            headerViewHolder.avatar.setImageDrawable(circularBitmapDrawable);
                        }
                    });

            //score and level
            String levelsJsonString = FirebaseRemoteConfig.getInstance().getString(Constants.Firebase.RemoteConfigKeys.LEVELS_JSON);
            LevelsJson levelsJson = mGson.fromJson(levelsJsonString, LevelsJson.class);
            if (levelsJson != null) {
                for (int i = 0; i < levelsJson.levels.size(); i++) {
                    LevelsJson.Level level = levelsJson.levels.get(i);
                    if (user.score < level.score) {
                        LevelsJson.Level prevLevel = levelsJson.levels.get(i - 1);

                        int levelNum = prevLevel.id;
                        String levelTitle = prevLevel.title;

                        int nextLevelScore = level.score;

                        int max = nextLevelScore - prevLevel.score;
                        int value = user.score - prevLevel.score;
                        headerViewHolder.circleProgress.setMaxValue(max);
                        headerViewHolder.circleProgress.setValue(value);

                        headerViewHolder.level.setText(levelTitle);
                        headerViewHolder.levelNum.setText(String.valueOf(levelNum));

                        headerViewHolder.avatar.setOnClickListener(view ->
                                showMessageLong(getString(R.string.profile_score_info, user.score, max - value)));
                        break;
                    } else if (i == levelsJson.levels.size() - 1) {
                        //so max level reached
                        headerViewHolder.circleProgress.setMaxValue(level.score);
                        headerViewHolder.circleProgress.setValue(level.score);

                        headerViewHolder.level.setText(level.title);
                        headerViewHolder.levelNum.setText(String.valueOf(level.id));

                        headerViewHolder.avatar.setOnClickListener(view ->
                                showMessageLong(getString(R.string.profile_score_info_max_level, user.score)));
                    }
                }
            } else {
                Timber.e("levelsJson is null");
            }
            if (mMyPreferenceManager.isAppCracked()) {
                headerViewHolder.circleProgress.setMaxValue(42);
                headerViewHolder.circleProgress.setValue(0);

                headerViewHolder.level.setText(R.string.cracked_level_title);
                headerViewHolder.levelNum.setText(String.valueOf(-1));

                headerViewHolder.avatar.setOnClickListener(view -> showMessage(R.string.cracked_avatar_message));
            }
        } else {
            for (int i = 0; i < mNavigationView.getHeaderCount(); i++) {
                mNavigationView.removeHeaderView(mNavigationView.getHeaderView(i));
            }
            View headerUnlogined = LayoutInflater.from(this).inflate(R.layout.drawer_header_unlogined, mNavigationView, false);
            mNavigationView.addHeaderView(headerUnlogined);

            HeaderViewHolderUnlogined headerViewHolder = new HeaderViewHolderUnlogined(headerUnlogined);

            headerViewHolder.mLogin.setOnClickListener(view -> startLogin(Constants.Firebase.SocialProvider.VK));

            headerViewHolder.mLoginInfo.setOnClickListener(view -> new MaterialDialog.Builder(this)
                    .content(R.string.login_advantages)
                    .title(R.string.login_advantages_title)
                    .positiveText(android.R.string.ok)
                    .show());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.d("called in fragment");
        if (requestCode == REQUEST_CODE_INAPP) {
//            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
//            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == Activity.RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    Timber.d("You have bought the %s", sku);

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, sku);
                    bundle.putFloat(FirebaseAnalytics.Param.VALUE, .5f);
                    bundle.putFloat(FirebaseAnalytics.Param.PRICE, .5f);
                    FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, bundle);

                    if (SecureUtils.checkLuckyPatcher(this) || SecureUtils.checkIfPackageChanged(this)) {
                        Bundle args = new Bundle();
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        args.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "CRACK_" + sku + ((firebaseUser != null) ? firebaseUser.getUid() : ""));
                        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, args);
                    } else {
                        if (sku.equals(BuildConfig.INAPP_SKUS[0])) {
                            //levelUp 5
                            mMyPreferenceManager.setHasLevelUpInapp(true);
                            //add 10 000 score
                            mPresenter.updateUserScoreForInapp(sku);
                        }
                    }
                } catch (JSONException e) {
                    Timber.e(e, "Failed to parse purchase data.");
                    showError(e);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}