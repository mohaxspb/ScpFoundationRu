package ru.dante.scpfoundation.ui.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.service.DownloadAllService;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static ru.dante.scpfoundation.service.DownloadAllService.DownloadType.TYPE_1;
import static ru.dante.scpfoundation.service.DownloadAllService.DownloadType.TYPE_2;
import static ru.dante.scpfoundation.service.DownloadAllService.DownloadType.TYPE_3;
import static ru.dante.scpfoundation.service.DownloadAllService.DownloadType.TYPE_ALL;
import static ru.dante.scpfoundation.service.DownloadAllService.DownloadType.TYPE_ARCHIVE;
import static ru.dante.scpfoundation.service.DownloadAllService.DownloadType.TYPE_EXPERIMETS;
import static ru.dante.scpfoundation.service.DownloadAllService.DownloadType.TYPE_INCIDENTS;
import static ru.dante.scpfoundation.service.DownloadAllService.DownloadType.TYPE_INTERVIEWS;
import static ru.dante.scpfoundation.service.DownloadAllService.DownloadType.TYPE_JOKES;
import static ru.dante.scpfoundation.service.DownloadAllService.DownloadType.TYPE_OTHER;
import static ru.dante.scpfoundation.service.DownloadAllService.DownloadType.TYPE_RU;

/**
 * Created by mohax on 29.05.2017.
 * <p>
 * for ScpFoundationRu
 */
public class DialogUtils {

    //download all consts
    //TODO need to refactor it and use one enum here and in service
    public static final int TYPE_OBJ_1 = 0;
    public static final int TYPE_OBJ_2 = 1;
    public static final int TYPE_OBJ_3 = 2;

    public static final int TYPE_OBJ_RU = 3;

    public static final int TYPE_EXPERIMETS = 4;
    public static final int TYPE_OTHER = 5;
    public static final int TYPE_INCIDENTS = 6;
    public static final int TYPE_INTERVIEWS = 7;
    public static final int TYPE_ARCHIVE = 8;

    public static final int TYPE_JOKES = 9;
    public static final int TYPE_ALL = 10;

    //    private final Context mContext;
    private final MyPreferenceManager mPreferenceManager;
    private final DbProviderFactory mDbProviderFactory;
    private final ApiClient mApiClient;

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
                .negativeText(R.string.cancel)
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
                            //TODO
                            //simply start download all with popup for limit users,
                            //in which tell, that we can't now how many arts he can load
                            numOfArticlesObservable = Observable.just(Integer.MIN_VALUE);
                            break;
                        case Constants.Urls.ARCHIVE:
                            articlesObservable = mApiClient.getMaterialsArchiveArticles();
                            numOfArticlesObservable = articlesObservable.count();
                            break;
                        case Constants.Urls.JOKES:
                            articlesObservable = mApiClient.getMaterialsJokesArticles();
                            numOfArticlesObservable = articlesObservable.count();
                            break;
                        case Constants.Urls.OBJECTS_1:
                        case Constants.Urls.OBJECTS_2:
                        case Constants.Urls.OBJECTS_3:
                        case Constants.Urls.OBJECTS_RU:
                            articlesObservable = mApiClient.getObjectsArticles(link);
                            numOfArticlesObservable = articlesObservable.count();
                            break;
                        default:
                            articlesObservable = mApiClient.getMaterialsArticles(link);
                            numOfArticlesObservable = articlesObservable.count();
                            break;
                    }

                    //FIXME
//                    DownloadAllService.startDownloadWithType(mContext, type);
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
            @DownloadAllService.DownloadType String type) {
        MaterialDialog progress = new MaterialDialog.Builder(context)
                .progress(true, 0)
                .content(R.string.downlad_art_list)
                .cancelable(false)
                .build();

        progress.show();

        countObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        numOfArts->{

                        },
                        e->{
                            Timber.e(e);
                            progress.dismiss();
                        }
                );
    }

    private void showProgressDialog(Context context, String content) {
        new MaterialDialog.Builder(context)
                .progress(true, 0)
                .content(content)
                .cancelable(false)
                .show();
    }

    private void showRangeDialog() {

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