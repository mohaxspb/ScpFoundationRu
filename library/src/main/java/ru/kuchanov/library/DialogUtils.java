package ru.kuchanov.library;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by mohax on 29.05.2017.
 * <p>
 * for ScpFoundationRu
 */
public abstract class DialogUtils<T extends ArticleModel> {

    private MyPreferenceManagerModel mPreferenceManager;
    private DbProviderFactoryModel mDbProviderFactory;
    private ApiClientModel<T> mApiClient;
    private Class clazz;

    public DialogUtils(
            MyPreferenceManagerModel preferenceManager,
            DbProviderFactoryModel dbProviderFactory,
            ApiClientModel<T> apiClient,
            Class clazz) {
        mPreferenceManager = preferenceManager;
        mDbProviderFactory = dbProviderFactory;
        mApiClient = apiClient;
        this.clazz = clazz;
    }

    public void showDownloadDialog(Context context) {
        List<DownloadEntry> entries = getDownloadTypesEntries(context);

        MaterialDialog materialDialog;
        materialDialog = new MaterialDialog.Builder(context)
                .title(R.string.download_all_title)
                .items(entries)
                .itemsCallbackSingleChoice(-1, (dialog, itemView, which, text) -> {
                    Timber.d("which: %s, text: %s", which, text);
                    if (!isServiceRunning()) {
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
                    Timber.d("dialog.getSelectedIndex(): %s", dialog.getSelectedIndex());

                    DownloadEntry type = entries.get(dialog.getSelectedIndex());

                    logDownloadAttempt(type);

                    Observable<Integer> numOfArticlesObservable;
                    Observable<List<T>> articlesObservable;
                    if (type.resId == R.string.type_all) {
                        //simply start download all with popup for limit users,
                        //in which tell, that we can't now how many arts he can load
                        numOfArticlesObservable = Observable.just(Integer.MIN_VALUE);
                    } else if (type.resId == R.string.type_archive) {
                        articlesObservable = mApiClient.getMaterialsArchiveArticles();
                        numOfArticlesObservable = articlesObservable.map(List::size);
                    } else if (type.resId == R.string.type_jokes) {
                        articlesObservable = mApiClient.getMaterialsJokesArticles();
                        numOfArticlesObservable = articlesObservable.map(List::size);
                    } else if (type.resId == R.string.type_1
                            || type.resId == R.string.type_2
                            || type.resId == R.string.type_3
                            || type.resId == R.string.type_4
                            || type.resId == R.string.type_ru) {
                        articlesObservable = mApiClient.getObjectsArticles(type.url);
                        numOfArticlesObservable = articlesObservable.map(List::size);
                    } else {
                        articlesObservable = mApiClient.getMaterialsArticles(type.url);
                        numOfArticlesObservable = articlesObservable.map(List::size);
                    }
                    loadArticlesAndCountThem(context, numOfArticlesObservable, type);
                    dialog.dismiss();
                })
                .neutralText(R.string.stop_download)
                .onNeutral((dialog, which) -> {
                    Timber.d("onNeutral clicked");
                    DownloadAllService.stopDownload(context, clazz);
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
            DownloadEntry type
    ) {
        MaterialDialog progress = new MaterialDialog.Builder(context)
                .progress(true, 0)
                .content(R.string.downlad_art_list)
                .cancelable(false)
                .build();

        progress.show();

        countObservable
                .flatMap(numOfArts -> {
                    int limit = mPreferenceManager.getFreeOfflineLimit();
                    int numOfScorePerArt = mPreferenceManager.getScorePerArt();

                    DbProviderModel dbProvider = mDbProviderFactory.getDbProvider();
                    limit += mDbProviderFactory.getDbProvider().getScore() / numOfScorePerArt;
                    dbProvider.close();
                    return Observable.just(new Pair<>(numOfArts, limit));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        numOfArtsAndLimit -> {
                            Timber.d("numOfArtsAndLimit: %s/%s", numOfArtsAndLimit.first, numOfArtsAndLimit.second);
                            progress.dismiss();
                            Timber.d("mPreferenceManager.isHasSubscription(): %s",
                                    mPreferenceManager.isHasSubscription());
                            Timber.d("remConf.getBoolean(RemoteConfigKeys.DOWNLOAD_ALL_ENABLED_FOR_FREE): %s",
                                    mPreferenceManager.isDownloadAllEnabledForFree());
                            boolean ignoreLimit = mPreferenceManager.isHasSubscription()
                                    || mPreferenceManager.isDownloadAllEnabledForFree();

                            if (type.resId == R.string.type_all) {
                                if (!ignoreLimit) {
                                    //simply start download all with popup for limit users,
                                    //in which tell, that we can't now how many arts he can load
                                    new MaterialDialog.Builder(context)
                                            .title(R.string.download_all)
                                            .content(context.getString(R.string.download_all_with_limit, numOfArtsAndLimit.second))
                                            .positiveText(R.string.download)
                                            .onPositive((dialog, which) ->
                                                    DownloadAllService.startDownloadWithType(
                                                            context,
                                                            type,
                                                            0,
                                                            numOfArtsAndLimit.second,
                                                            clazz
                                                    )
                                            )
                                            //TODO add increase/remove limit button
                                            .negativeText(android.R.string.cancel)
                                            .build()
                                            .show();
                                } else {
                                    DownloadAllService.startDownloadWithType(
                                            context,
                                            type,
                                            DownloadAllService.RANGE_NONE,
                                            DownloadAllService.RANGE_NONE,
                                            clazz
                                    );
                                }
                            } else {
                                if (numOfArtsAndLimit.first == 1) {
                                    DownloadAllService.startDownloadWithType(
                                            context,
                                            type,
                                            0,
                                            1,
                                            clazz
                                    );
                                } else {
                                    showRangeDialog(context, type, numOfArtsAndLimit.first, numOfArtsAndLimit.second, ignoreLimit);
                                }
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
            DownloadEntry type,
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
        CrystalRangeSeekbar seekbar = (CrystalRangeSeekbar) view.findViewById(R.id.rangeSeekbar);
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

        TextView min = (TextView) view.findViewById(R.id.min);
        TextView max = (TextView) view.findViewById(R.id.max);
        TextView userLimit = (TextView) view.findViewById(R.id.userLimit);
        TextView increaseLimit = (TextView) view.findViewById(R.id.increaseLimit);
        ImageView info = (ImageView) view.findViewById(R.id.info);

        boolean isNightMode = mPreferenceManager.isNightMode();
        int tint = isNightMode ? Color.WHITE : ContextCompat.getColor(context, R.color.downloads_zbs_color_red);
        info.setColorFilter(tint);

        int scorePerArt = mPreferenceManager.getScorePerArt();
        int freeOfflineLimit = mPreferenceManager.getFreeOfflineLimit();

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
        increaseLimit.setOnClickListener(v -> onIncreaseLimitClick(context));

        userLimit.setText(context.getString(R.string.user_limit, ignoreLimit
                ? context.getString(R.string.no_limit) : String.valueOf(limit)));

        seekbar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
            min.setText(String.valueOf(minValue));
            max.setText(String.valueOf(maxValue));

            dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(v -> {
                DownloadAllService.startDownloadWithType(
                        context,
                        type,
                        minValue.intValue(),
                        maxValue.intValue(),
                        clazz
                );
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    public abstract List<DownloadEntry> getDownloadTypesEntries(Context context);

    protected abstract boolean isServiceRunning();

    /**
     * show dialog with subscriptions
     */
    protected abstract void onIncreaseLimitClick(Context context);

    protected abstract void logDownloadAttempt(DownloadEntry type);
}