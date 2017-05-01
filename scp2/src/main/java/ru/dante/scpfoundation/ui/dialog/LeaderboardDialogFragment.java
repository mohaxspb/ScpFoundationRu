package ru.dante.scpfoundation.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;

import com.afollestad.materialdialogs.MaterialDialog;

import javax.inject.Inject;

import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.ui.adapter.FreeAdsDisableRecyclerAdapter;
import ru.dante.scpfoundation.ui.base.BaseActivity;
import timber.log.Timber;

public class LeaderboardDialogFragment extends DialogFragment {

    public static final String TAG = LeaderboardDialogFragment.class.getSimpleName();

    @Inject
    ApiClient mApiClient;
    @Inject
    protected MyPreferenceManager mMyPreferenceManager;

    public static DialogFragment newInstance() {
        return new LeaderboardDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getAppComponent().inject(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Timber.d("onCreateDialog");
        final MaterialDialog dialog;

        MaterialDialog.Builder dialogTextSizeBuilder = new MaterialDialog.Builder(getActivity());
        dialogTextSizeBuilder
                .title(R.string.dialog_free_ads_disable_title)
                .positiveText(android.R.string.cancel);

        FreeAdsDisableRecyclerAdapter adapter = new FreeAdsDisableRecyclerAdapter();
        adapter.setData(data);

        dialogTextSizeBuilder.adapter(adapter, new LinearLayoutManager(getActivity()));

        dialog = dialogTextSizeBuilder.build();

        dialog.getRecyclerView().addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        return dialog;
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }
}