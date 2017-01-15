package ru.dante.scpfoundation.ui.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.inject.Inject;

import ru.dante.scpfoundation.BuildConfig;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import timber.log.Timber;


public class NewVersionDialogFragment extends DialogFragment {
    public static final String TAG = NewVersionDialogFragment.class.getSimpleName();

    public static NewVersionDialogFragment newInstance() {
        return new NewVersionDialogFragment();
    }

    @Inject
    protected MyPreferenceManager mMyPreferenceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getAppComponent().inject(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Timber.d("onCreateDialog");
        final MaterialDialog dialogTextSize;
        String newVersionFeatures = "";
        try {
            newVersionFeatures = readFromAssets(getActivity(), "newVersionFeatures" + BuildConfig.VERSION_CODE + ".txt");
        } catch (IOException e) {
            Timber.e(e, "error while read newVersionFeatures from file");
        }

        MaterialDialog.Builder dialogTextSizeBuilder = new MaterialDialog.Builder(getActivity());
        dialogTextSizeBuilder
                .content(Html.fromHtml(newVersionFeatures))
                .title(R.string.new_version_features)
                .positiveText(R.string.hurray);
//                .customView(R.layout.dialog_text_size, true);

        dialogTextSize = dialogTextSizeBuilder.build();

        return dialogTextSize;
    }

    //TODO move to utils
    public static String readFromAssets(Context context, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename), "UTF-8"));

        // do reading, usually loop until end of file reading
        StringBuilder sb = new StringBuilder();
        String mLine = reader.readLine();
        while (mLine != null) {
            sb.append(mLine); // process line
            mLine = reader.readLine();
        }
        reader.close();
        return sb.toString();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mMyPreferenceManager.setCurAppVersion(BuildConfig.VERSION_CODE);
    }
}