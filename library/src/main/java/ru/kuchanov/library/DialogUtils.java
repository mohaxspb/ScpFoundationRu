package ru.kuchanov.library;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;

import java.util.ArrayList;
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
public abstract class DialogUtils<S extends DownloadAllService> {

    //download all consts
    //TODO need to refactor it and use one enum here and in service
//    private static final int TYPE_OBJ_1 = 0;
//    private static final int TYPE_OBJ_2 = 1;
//    private static final int TYPE_OBJ_3 = 2;
//
//    private static final int TYPE_OBJ_RU = 3;
//
//    private static final int TYPE_EXPERIMETS = 4;
//    private static final int TYPE_OTHER = 5;
//    private static final int TYPE_INCIDENTS = 6;
//    private static final int TYPE_INTERVIEWS = 7;
//    private static final int TYPE_ARCHIVE = 8;
//
//    private static final int TYPE_JOKES = 9;
//    private static final int TYPE_ALL = 10;

//    public enum DownloadType {
//
//        TYPE_1(R.string.type_1),
//        TYPE_2(R.string.type_2),
//        TYPE_3(R.string.type_3),
//        TYPE_4(R.string.type_4),
//        TYPE_RU(R.string.type_ru),
//
//        TYPE_EXPERIMENTS(R.string.type_experiments),
//        TYPE_OTHER(R.string.type_other),
//        TYPE_INCIDENTS(R.string.type_incidents),
//        TYPE_INTERVIEWS(R.string.type_interviews),
//        TYPE_ARCHIVE(R.string.type_archive),
//        TYPE_JOKES(R.string.type_jokes),
//
//        TYPE_ALL(R.string.type_all);
//
//        @StringRes
//        private final int mTitle;
//
//        DownloadType(@StringRes int title) {
//            this.mTitle = title;
//        }
//
//        @StringRes
//        public int getTitle() {
//            return mTitle;
//        }
//
//        class Entry {
//            private Context mContext;
//
//            Entry(Context context) {
//                mContext = context;
//            }
//
//            @Override
//            public String toString() {
//                return mContext.getString(mTitle);
//            }
//
//            public DownloadType getType() {
//                return DownloadType.this;
//            }
//        }
//
//        public List<Entry> getEntries(Context context) {
//            List<Entry> entries = new ArrayList<>();
//
//            for (DownloadType downloadType : DownloadType.values()) {
//                entries.add(new Entry(context));
//            }
//
//            return entries;
//        }
//
//        public List<Entry> getEntries(List<DownloadType> downloadTypes, Context context) {
//            List<Entry> entries = new ArrayList<>();
//
//            for (DownloadType downloadType : downloadTypes) {
//                entries.add(new Entry(context));
//            }
//
//            return entries;
//        }
//    }

    private MyPreferenceManagerModel mPreferenceManager;
    private DbProviderFactoryModel mDbProviderFactory;
    private ApiClientModel mApiClient;
    private Class clazz;

    public DialogUtils(
            MyPreferenceManagerModel preferenceManager,
            DbProviderFactoryModel dbProviderFactory,
            ApiClientModel apiClient,
            Class clazz) {
        mPreferenceManager = preferenceManager;
        mDbProviderFactory = dbProviderFactory;
        mApiClient = apiClient;
        this.clazz = clazz;
    }

    public abstract List<DownloadEntry> getDownloadTypesEntries(Context context);

    public interface OnDownloadPositiveClickListener {
        void onPositiveClick(int selectedItemPosition);
    }

    public void showDownloadDialog(Context context, OnDownloadPositiveClickListener onDownloadPositiveClickListener) {
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
                    Timber.d("onPositive clicked");
                    Timber.d("dialog.getSelectedIndex(): %s", dialog.getSelectedIndex());
                    onDownloadPositiveClickListener.onPositiveClick(dialog.getSelectedIndex());
//
//                    DownloadType type = entries.get(dialog.getSelectedIndex()).getType();
//
//                    logDownloadAttempt(type);
//
//                    String link;
//                    switch (entries.get(dialog.getSelectedIndex()).getType()) {
//                        case TYPE_1:
//                            link = Constants.Urls.OBJECTS_1;
//                            break;
//                        case TYPE_2:
//                            link = Constants.Urls.OBJECTS_2;
//                            break;
//                        case TYPE_3:
//                            link = Constants.Urls.OBJECTS_3;
//                            break;
//                        case TYPE_RU:
//                            link = Constants.Urls.OBJECTS_RU;
//                            break;
//                        case TYPE_EXPERIMENTS:
//                            link = Constants.Urls.PROTOCOLS;
//                            break;
//                        case TYPE_OTHER:
//                            link = Constants.Urls.OTHERS;
//                            break;
//                        case TYPE_INCIDENTS:
//                            link = Constants.Urls.INCEDENTS;
//                            break;
//                        case TYPE_INTERVIEWS:
//                            link = Constants.Urls.INTERVIEWS;
//                            break;
//                        case TYPE_ARCHIVE:
//                            link = Constants.Urls.ARCHIVE;
//                            break;
//                        case TYPE_JOKES:
//                            link = Constants.Urls.JOKES;
//                            break;
//                        case TYPE_ALL:
//                            link = TYPE_ALL.toString();
//                            break;
//                        default:
//                            throw new IllegalArgumentException("unexpected type");
//                    }
//
//                    Observable<Integer> numOfArticlesObservable;
//                    Observable<List<ArticleModel>> articlesObservable;
//                    switch (link) {
//                        case DownloadAllService.DownloadType.TYPE_ALL:
//                            //simply start download all with popup for limit users,
//                            //in which tell, that we can't now how many arts he can load
//                            numOfArticlesObservable = Observable.just(Integer.MIN_VALUE);
//                            break;
//                        case Constants.Urls.ARCHIVE:
//                            articlesObservable = mApiClient.getMaterialsArchiveArticles();
//                            numOfArticlesObservable = articlesObservable.map(List::size);
//                            break;
//                        case Constants.Urls.JOKES:
//                            articlesObservable = mApiClient.getMaterialsJokesArticles();
//                            numOfArticlesObservable = articlesObservable.map(List::size);
//                            break;
//                        case Constants.Urls.OBJECTS_1:
//                        case Constants.Urls.OBJECTS_2:
//                        case Constants.Urls.OBJECTS_3:
//                        case Constants.Urls.OBJECTS_RU:
//                            articlesObservable = mApiClient.getObjectsArticles(link);
//                            numOfArticlesObservable = articlesObservable.map(List::size);
//                            break;
//                        default:
//                            articlesObservable = mApiClient.getMaterialsArticles(link);
//                            numOfArticlesObservable = articlesObservable.map(List::size);
//                            break;
//                    }
//                    loadArticlesAndCountThem(context, numOfArticlesObservable, type);
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

    protected abstract boolean isServiceRunning();

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
                            progress.dismiss();
                            Timber.d("mPreferenceManager.isHasSubscription(): %s",
                                    mPreferenceManager.isHasSubscription());
                            Timber.d("remConf.getBoolean(RemoteConfigKeys.DOWNLOAD_ALL_ENABLED_FOR_FREE): %s",
                                    mPreferenceManager.isDownloadAllEnabledForFree());
                            boolean ignoreLimit = mPreferenceManager.isHasSubscription()
                                    || mPreferenceManager.isDownloadAllEnabledForFree();

//                            if (type.equals(DownloadAllService.DownloadType.TYPE_ALL)) {
//                                if (!ignoreLimit) {
//                                    //simply start download all with popup for limit users,
//                                    //in which tell, that we can't now how many arts he can load
//                                    new MaterialDialog.Builder(context)
//                                            .title(R.string.download_all)
//                                            .content(context.getString(R.string.download_all_with_limit, numOfArtsAndLimit.second))
//                                            .positiveText(R.string.download)
//                                            .onPositive((dialog, which) ->
//                                                    DownloadAllService.startDownloadWithType(context, type, 0, numOfArtsAndLimit.second))
//                                            .negativeText(android.R.string.cancel)
//                                            .build()
//                                            .show();
//                                } else {
//                                    DownloadAllService.startDownloadWithType(context, type, DownloadAllService.RANGE_NONE, DownloadAllService.RANGE_NONE);
//                                }
//                            } else {
//                                showRangeDialog(context, type, numOfArtsAndLimit.first, numOfArtsAndLimit.second, ignoreLimit);
//                            }
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
        increaseLimit.setOnClickListener(v -> onIncreaseLimitClick());

        userLimit.setText(context.getString(R.string.user_limit, ignoreLimit
                ? context.getString(R.string.no_limit) : String.valueOf(limit)));

        seekbar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
            min.setText(String.valueOf(minValue));
            max.setText(String.valueOf(maxValue));

            dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(v -> {
                DownloadAllService.startDownloadWithType(context, type.resId, minValue.intValue(), maxValue.intValue(), clazz);
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    /**
     * show dialog with subscriptions
     */
    protected abstract void onIncreaseLimitClick();

    protected abstract void logDownloadAttempt(DownloadEntry type);
}