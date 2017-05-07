package ru.dante.scpfoundation.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;

import com.afollestad.materialdialogs.MaterialDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.TimeZone;

import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.api.model.response.LeaderBoardResponse;
import ru.dante.scpfoundation.ui.adapter.LeaderboardRecyclerAdapter;
import timber.log.Timber;

public class LeaderboardDialogFragment extends DialogFragment {

    public static final String TAG = LeaderboardDialogFragment.class.getSimpleName();
    private static final String EXTRA_LEADERBOARD_RESPONSE = "EXTRA_LEADERBOARD_RESPONSE";

    private LeaderBoardResponse mLeaderBoardResponse;

    public static DialogFragment newInstance(LeaderBoardResponse leaderBoardResponse) {
        DialogFragment dialogFragment = new LeaderboardDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_LEADERBOARD_RESPONSE, leaderBoardResponse);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getAppComponent().inject(this);

        mLeaderBoardResponse = (LeaderBoardResponse) getArguments().getSerializable(EXTRA_LEADERBOARD_RESPONSE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Timber.d("onCreateDialog");
        final MaterialDialog dialog;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mLeaderBoardResponse.lastUpdated);
        calendar.setTimeZone(TimeZone.getTimeZone(mLeaderBoardResponse.timeZone));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss zzzz", Locale.getDefault());
        String refreshed = simpleDateFormat.format(calendar.getTime());

        MaterialDialog.Builder dialogTextSizeBuilder = new MaterialDialog.Builder(getActivity());
        dialogTextSizeBuilder
                .title(R.string.leaderboard_dialog_title)
                .content(getString(R.string.refreshed, refreshed))
                .positiveText(android.R.string.cancel);

        LeaderboardRecyclerAdapter adapter = new LeaderboardRecyclerAdapter();
        adapter.setItemClickListener(data -> Timber.d("onUserClicked: %s", data));
        Collections.sort(mLeaderBoardResponse.users, (user1, user) -> user.score - user1.score);
        adapter.setData(mLeaderBoardResponse.users);

        dialogTextSizeBuilder.adapter(adapter, new LinearLayoutManager(getActivity()));

        dialog = dialogTextSizeBuilder.build();

        dialog.getRecyclerView().addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        dialog.getRecyclerView().setAdapter(adapter);

        return dialog;
    }
}