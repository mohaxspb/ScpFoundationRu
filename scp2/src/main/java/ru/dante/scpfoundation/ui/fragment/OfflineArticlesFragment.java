package ru.dante.scpfoundation.ui.fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.analytics.FirebaseAnalytics;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.mvp.contract.OfflineArticles;
import ru.dante.scpfoundation.service.DownloadAllService;
import ru.dante.scpfoundation.ui.base.BaseActivity;
import timber.log.Timber;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class OfflineArticlesFragment
        extends BaseListArticlesWithSearchFragment<OfflineArticles.View, OfflineArticles.Presenter>
        implements OfflineArticles.View {

    public static final String TAG = OfflineArticlesFragment.class.getSimpleName();

    public static OfflineArticlesFragment newInstance() {
        return new OfflineArticlesFragment();
    }

    @Override
    protected void callInjections() {
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    protected boolean isHasOptionsMenu() {
        return true;
    }

    @Override
    protected int getMenuResId() {
        return R.menu.menu_offline;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItemDownloadAll:
                showDownloadDialog();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void getDataFromApi() {
        //do not use any server to store offlines
    }

    @Override
    public void resetOnScrollListener() {
        //we do not have paging
    }

    @Override
    protected boolean isSwipeRefreshEnabled() {
        //have api for it, we do not need to update list
        return false;
    }

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

    @Override
    public void showDownloadDialog() {
        MaterialDialog materialDialog;
        materialDialog = new MaterialDialog.Builder(getActivity())
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
                .onNegative((dialog, which) -> {
                    Timber.i("onNegative clicked");
                    dialog.dismiss();
                })
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
                    FirebaseAnalytics.getInstance(getActivity()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                    DownloadAllService.startDownloadWithType(getActivity(), type);
                    dialog.dismiss();
                })
                .neutralText(R.string.stop_download)
                .onNeutral((dialog, which) -> {
                    Timber.d("onNeutral clicked");
                    DownloadAllService.stopDownload(getActivity());
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
}