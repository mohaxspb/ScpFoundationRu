package ru.dante.scpfoundation.ui.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.List;

import butterknife.ButterKnife;
import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.Constants.Firebase.RemoteConfigKeys;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProvider;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.db.model.User;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.service.DownloadAllService;
import ru.dante.scpfoundation.ui.dialog.SubscriptionsFragmentDialog;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by mohax on 29.05.2017.
 * <p>
 * for ScpFoundationRu
 */
public class DialogUtils {

    //download all consts
    //TODO need to refactor it and use one enum here and in service
    private static final int TYPE_OBJ_1 = 0;
    private static final int TYPE_OBJ_2 = 1;
    private static final int TYPE_OBJ_3 = 2;

    private static final int TYPE_OBJ_RU = 3;

    private static final int TYPE_EXPERIMETS = 4;
    private static final int TYPE_OTHER = 5;
    private static final int TYPE_INCIDENTS = 6;
    private static final int TYPE_INTERVIEWS = 7;
    private static final int TYPE_ARCHIVE = 8;

    private static final int TYPE_JOKES = 9;
    private static final int TYPE_ALL = 10;

    //    private final Context mContext;
    private MyPreferenceManager mPreferenceManager;
    private DbProviderFactory mDbProviderFactory;
    private ApiClient mApiClient;

    public DialogUtils(
            MyPreferenceManager preferenceManager,
            DbProviderFactory dbProviderFactory,
            ApiClient apiClient
    ) {
        mPreferenceManager = preferenceManager;
        mDbProviderFactory = dbProviderFactory;
        mApiClient = apiClient;
    }

    public void showDownloadDialog(Context mContext) {
        MaterialDialog materialDialog;
        materialDialog = new MaterialDialog.Builder(mContext)
                .title(R.string.download_all_title)
                .items(R.array.download_types)
                .itemsCallbackSingleChoice(-1, (dialog, itemView, which, text) -> {
                    Timber.d("which: %s, text: %s", which, text);
                    if (!DownloadAllService.isRunning()) {
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                    }
                    return true;
                })
                .alwaysCallSingleChoiceCallback()
                .positiveText(R.string.download)
                .negativeText(android.R.string.cancel)
                .autoDismiss(false)
                .onNegative((dialog, which) -> dialog.dismiss())
                .onPositive((dialog, which) -> {
                    Timber.d("onPositive clicked");
                    Timber.d("dialog.getSelectedIndex(): %s", dialog.getSelectedIndex());

                    @DownloadAllService.DownloadType
                    String type;
                    switch (dialog.getSelectedIndex()) {
                        case TYPE_OBJ_1:
                            type = DownloadAllService.DownloadType.TYPE_1;
                            break;
                        case TYPE_OBJ_2:
                            type = DownloadAllService.DownloadType.TYPE_2;
                            break;
                        case TYPE_OBJ_3:
                            type = DownloadAllService.DownloadType.TYPE_3;
                            break;
                        case TYPE_OBJ_RU:
                            type = DownloadAllService.DownloadType.TYPE_RU;
                            break;
                        case TYPE_EXPERIMETS:
                            type = DownloadAllService.DownloadType.TYPE_EXPERIMETS;
                            break;
                        case TYPE_OTHER:
                            type = DownloadAllService.DownloadType.TYPE_OTHER;
                            break;
                        case TYPE_INCIDENTS:
                            type = DownloadAllService.DownloadType.TYPE_INCIDENTS;
                            break;
                        case TYPE_INTERVIEWS:
                            type = DownloadAllService.DownloadType.TYPE_INTERVIEWS;
                            break;
                        case TYPE_ARCHIVE:
                            type = DownloadAllService.DownloadType.TYPE_ARCHIVE;
                            break;
                        case TYPE_JOKES:
                            type = DownloadAllService.DownloadType.TYPE_JOKES;
                            break;
                        case TYPE_ALL:
                            type = DownloadAllService.DownloadType.TYPE_ALL;
                            break;
                        default:
                            throw new IllegalArgumentException("unexpected type: " + dialog.getSelectedIndex());
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
                    FirebaseAnalytics.getInstance(mContext).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                    String link;
                    switch (type) {
                        case DownloadAllService.DownloadType.TYPE_1:
                            link = Constants.Urls.OBJECTS_1;
                            break;
                        case DownloadAllService.DownloadType.TYPE_2:
                            link = Constants.Urls.OBJECTS_2;
                            break;
                        case DownloadAllService.DownloadType.TYPE_3:
                            link = Constants.Urls.OBJECTS_3;
                            break;
                        case DownloadAllService.DownloadType.TYPE_RU:
                            link = Constants.Urls.OBJECTS_RU;
                            break;
                        case DownloadAllService.DownloadType.TYPE_EXPERIMETS:
                            link = Constants.Urls.PROTOCOLS;
                            break;
                        case DownloadAllService.DownloadType.TYPE_OTHER:
                            link = Constants.Urls.OTHERS;
                            break;
                        case DownloadAllService.DownloadType.TYPE_INCIDENTS:
                            link = Constants.Urls.INCEDENTS;
                            break;
                        case DownloadAllService.DownloadType.TYPE_INTERVIEWS:
                            link = Constants.Urls.INTERVIEWS;
                            break;
                        case DownloadAllService.DownloadType.TYPE_ARCHIVE:
                            link = Constants.Urls.ARCHIVE;
                            break;
                        case DownloadAllService.DownloadType.TYPE_JOKES:
                            link = Constants.Urls.JOKES;
                            break;
                        case DownloadAllService.DownloadType.TYPE_ALL:
                            link = DownloadAllService.DownloadType.TYPE_ALL;
                            break;
                        default:
                            throw new IllegalArgumentException("unexpected type");
                    }

                    Observable<Integer> numOfArticlesObservable;
                    Observable<List<Article>> articlesObservable;
                    switch (link) {
                        case DownloadAllService.DownloadType.TYPE_ALL:
                            //simply start download all with popup for limit users,
                            //in which tell, that we can't now how many arts he can load
                            numOfArticlesObservable = Observable.just(Integer.MIN_VALUE);
                            break;
                        case Constants.Urls.ARCHIVE:
                            articlesObservable = mApiClient.getMaterialsArchiveArticles();
                            numOfArticlesObservable = articlesObservable.map(List::size);
                            break;
                        case Constants.Urls.JOKES:
                            articlesObservable = mApiClient.getMaterialsJokesArticles();
                            numOfArticlesObservable = articlesObservable.map(List::size);
                            break;
                        case Constants.Urls.OBJECTS_1:
                        case Constants.Urls.OBJECTS_2:
                        case Constants.Urls.OBJECTS_3:
                        case Constants.Urls.OBJECTS_RU:
                            articlesObservable = mApiClient.getObjectsArticles(link);
                            numOfArticlesObservable = articlesObservable.map(List::size);
                            break;
                        default:
                            articlesObservable = mApiClient.getMaterialsArticles(link);
                            numOfArticlesObservable = articlesObservable.map(List::size);
                            break;
                    }
                    loadArticlesAndCountThem(mContext, numOfArticlesObservable, type);
                    dialog.dismiss();
                })
                .neutralText(R.string.stop_download)
                .onNeutral((dialog, which) -> {
                    Timber.d("onNeutral clicked");
                    DownloadAllService.stopDownload(mContext);
                    dialog.dismiss();
                })
                .build();

        materialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);

        if (DownloadAllService.isRunning()) {
            materialDialog.getActionButton(DialogAction.NEUTRAL).setEnabled(true);
        } else {
            materialDialog.getActionButton(DialogAction.NEUTRAL).setEnabled(false);
        }

        materialDialog.getRecyclerView().setOverScrollMode(View.OVER_SCROLL_NEVER);

        materialDialog.show();
    }

    private void loadArticlesAndCountThem(
            Context context,
            Observable<Integer> countObservable,
            @DownloadAllService.DownloadType String type
    ) {
        MaterialDialog progress = new MaterialDialog.Builder(context)
                .progress(true, 0)
                .content(R.string.downlad_art_list)
                .cancelable(false)
                .build();

        progress.show();

        countObservable
                .flatMap(numOfArts -> {
                    DbProvider dbProvider = mDbProviderFactory.getDbProvider();

                    FirebaseRemoteConfig remConf = FirebaseRemoteConfig.getInstance();
                    int limit = (int) remConf.getLong(RemoteConfigKeys.DOWNLOAD_FREE_ARTICLES_LIMIT);
                    int numOfScorePerArt = (int) remConf.getLong(RemoteConfigKeys.DOWNLOAD_SCORE_PER_ARTICLE);

                    User user = mDbProviderFactory.getDbProvider().getUserSync();
                    if (user != null) {
                        limit += user.score / numOfScorePerArt;
                    }
                    dbProvider.close();
                    return Observable.just(new Pair<>(numOfArts, limit));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        numOfArtsAndLimit -> {
                            progress.dismiss();
                            FirebaseRemoteConfig remConf = FirebaseRemoteConfig.getInstance();
                            Timber.d("mPreferenceManager.isHasSubscription(): %s",
                                    mPreferenceManager.isHasSubscription());
                            Timber.d("remConf.getBoolean(RemoteConfigKeys.DOWNLOAD_ALL_ENABLED_FOR_FREE): %s",
                                    remConf.getBoolean(RemoteConfigKeys.DOWNLOAD_ALL_ENABLED_FOR_FREE));
                            boolean ignoreLimit = mPreferenceManager.isHasSubscription()
                                    || remConf.getBoolean(RemoteConfigKeys.DOWNLOAD_ALL_ENABLED_FOR_FREE);

                            if (type.equals(DownloadAllService.DownloadType.TYPE_ALL)) {
                                if (!ignoreLimit) {
                                    //simply start download all with popup for limit users,
                                    //in which tell, that we can't now how many arts he can load
                                    new MaterialDialog.Builder(context)
                                            .title(R.string.download_all)
                                            .content(context.getString(R.string.download_all_with_limit, numOfArtsAndLimit.second))
                                            .positiveText(R.string.download)
                                            .onPositive((dialog, which) ->
                                                    DownloadAllService.startDownloadWithType(context, type, 0, numOfArtsAndLimit.second))
                                            .negativeText(android.R.string.cancel)
                                            .build()
                                            .show();
                                } else {
                                    DownloadAllService.startDownloadWithType(context, type, DownloadAllService.RANGE_NONE, DownloadAllService.RANGE_NONE);
                                }
                            } else {
                                showRangeDialog(context, type, numOfArtsAndLimit.first, numOfArtsAndLimit.second, ignoreLimit);
                            }
                        },
                        e -> {
                            Timber.e(e);
                            progress.dismiss();
                        }
                );
    }

    private void showRangeDialog(
            Context context,
            @DownloadAllService.DownloadType String type,
            int numOfArticles,
            int limit,
            boolean ignoreLimit
    ) {
        Timber.d("showRangeDialog type/numOfArticles/limit/ignoreLimit: %s/%s/%s/%s",
                type, numOfArticles, limit, ignoreLimit);
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_download_range, false)
                .title(R.string.downlad_art_list_range)
                .cancelable(false)
                .negativeText(android.R.string.cancel)
                .onNegative((dialog1, which) -> dialog1.dismiss())
                .positiveText(R.string.download)
                .build();

        View view = dialog.getCustomView();
        CrystalRangeSeekbar seekbar = ButterKnife.findById(view, R.id.rangeSeekbar);
        seekbar.setMaxValue(numOfArticles).apply();

        if (!ignoreLimit) {
            if (limit < numOfArticles) {
                seekbar.setMinStartValue(0).apply();
                seekbar.setMaxValue(numOfArticles).apply();

                seekbar.setFixGap(limit).apply();
            }
        } else {
            seekbar.setMinStartValue(0).apply();
            seekbar.setMaxStartValue(numOfArticles).apply();
        }

        TextView min = ButterKnife.findById(view, R.id.min);
        TextView max = ButterKnife.findById(view, R.id.max);
        TextView userLimit = ButterKnife.findById(view, R.id.userLimit);
        TextView increaseLimit = ButterKnife.findById(view, R.id.increaseLimit);
        ImageView info = ButterKnife.findById(view, R.id.info);

        boolean isNightMode = mPreferenceManager.isNightMode();
        int tint = isNightMode ? Color.WHITE : ContextCompat.getColor(context, R.color.zbs_color_red);
        info.setColorFilter(tint);

        FirebaseRemoteConfig remConf = FirebaseRemoteConfig.getInstance();
        int scorePerArt = (int) remConf.getLong(RemoteConfigKeys.DOWNLOAD_SCORE_PER_ARTICLE);
        int freeOfflineLimit = (int) remConf.getLong(RemoteConfigKeys.DOWNLOAD_FREE_ARTICLES_LIMIT);

        String limitDescriptionText;
        if (ignoreLimit) {
            limitDescriptionText = context.getString(R.string.limit_description, freeOfflineLimit, freeOfflineLimit, scorePerArt);
        } else {
            limitDescriptionText = context.getString(R.string.limit_description_disabled_free_downloads, freeOfflineLimit, scorePerArt);
        }
        info.setOnClickListener(view1 -> new MaterialDialog.Builder(context)
                .title(R.string.info)
                .content(limitDescriptionText)
                .positiveText(android.R.string.ok)
                .show());

        increaseLimit.setVisibility(ignoreLimit ? View.INVISIBLE : View.VISIBLE);
        increaseLimit.setOnClickListener(v -> {
            BottomSheetDialogFragment subsDF = SubscriptionsFragmentDialog.newInstance();
            subsDF.show(((AppCompatActivity) context).getSupportFragmentManager(), subsDF.getTag());

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, Constants.Firebase.Analitics.StartScreen.DOWNLOAD_DIALOG);
            FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        });

        userLimit.setText(context.getString(R.string.user_limit, ignoreLimit
                ? context.getString(R.string.no_limit) : String.valueOf(limit)));

        seekbar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
            min.setText(String.valueOf(minValue));
            max.setText(String.valueOf(maxValue));

            dialog.getActionButton(DialogAction.POSITIVE)
                    .setOnClickListener(v -> {
                        DownloadAllService.startDownloadWithType(context, type, minValue.intValue(), maxValue.intValue());
                        dialog.dismiss();
                    });
        });

        dialog.show();
    }

    public void showFaqDialog(Context context) {
        new MaterialDialog.Builder(context)
                .title(R.string.faq)
                .positiveText(R.string.close)
                .items(R.array.fag_items)
                .alwaysCallSingleChoiceCallback()
                .itemsCallback((dialog, itemView, position, text) -> {
                    Timber.d("itemsCallback: %s", text);
                    new MaterialDialog.Builder(context)
                            .title(text)
                            .content(context.getResources().getStringArray(R.array.fag_items_content)[position])
                            .positiveText(R.string.close)
                            .build()
                            .show();
                })
                .build()
                .show();
    }

    //TODO think how to restore image dialog Maybe use fragment dialog?..
    public void showImageDialog(Context mContext, String imgUrl) {
        Timber.d("showImageDialog");
        Dialog nagDialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        nagDialog.setCancelable(true);
        nagDialog.setContentView(R.layout.preview_image);

        final PhotoView photoView = (PhotoView) nagDialog.findViewById(R.id.image_view_touch);
        photoView.setMaximumScale(5f);

        Glide.with(photoView.getContext())
                .load(imgUrl)
                .placeholder(R.drawable.ic_image_white_48dp)
                .into(photoView);

        nagDialog.show();
    }
}