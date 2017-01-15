package ru.dante.scpfoundation.ui.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import timber.log.Timber;


public class TextSizeDialogFragment extends DialogFragment {
    public static final String TAG = TextSizeDialogFragment.class.getSimpleName();

    @Inject
    MyPreferenceManager mMyPreferenceManager;

    @BindView(R.id.seekbarUi)
    SeekBar seekbarUI;
    @BindView(R.id.seekbarArticle)
    SeekBar seekbarArticle;
    @BindView(R.id.textSizeUi)
    TextView tvUi;
    @BindView(R.id.textSizeArticle)
    TextView tvArticle;

    protected Unbinder mUnbinder;

    public static TextSizeDialogFragment newInstance() {
        return new TextSizeDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        Timber.d("onCreate");
        MyApplication.getAppComponent().inject(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Timber.d("onCreateDialog");
        final MaterialDialog dialogTextSize;

        MaterialDialog.Builder dialogTextSizeBuilder = new MaterialDialog.Builder(getActivity());
        dialogTextSizeBuilder
                .title(R.string.text_size_dialog_title)
                .positiveText(R.string.close)
                .customView(R.layout.dialog_text_size, true);

        dialogTextSize = dialogTextSizeBuilder.build();

        View customView = dialogTextSize.getCustomView();

        if (customView == null) {
            return dialogTextSize;
        }
        mUnbinder = ButterKnife.bind(this, customView);

        seekbarUI.setMax(150);
        float scaleUI = mMyPreferenceManager.getUiTextScale();

        int curProgressUI = (int) ((scaleUI - 0.50f) * 100);
        seekbarUI.setProgress(curProgressUI);
        seekbarUI.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float size = (progress / 100f) + 0.50f;
                int textSizePrimaryInDp = (int) (getResources().getDimension(R.dimen.text_size_primary)
                        / getResources().getDisplayMetrics().density);
//                Timber.d("text_size_primary: %s", textSizePrimaryInDp);
                tvUi.setTextSize(size * textSizePrimaryInDp);
                mMyPreferenceManager.setUiTextScale(size);
            }
        });

        seekbarArticle.setMax(150);
        float scaleArt = mMyPreferenceManager.getArticleTextScale();
        int curProgressArt = (int) ((scaleArt - 0.50f) * 100);
        seekbarArticle.setProgress(curProgressArt);
        seekbarArticle.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float size = (progress / 100f) + 0.50f;
                int textSizePrimaryInDp = (int) (getResources().getDimension(R.dimen.text_size_primary)
                        / getResources().getDisplayMetrics().density);
                tvArticle.setTextSize(size * textSizePrimaryInDp);
                mMyPreferenceManager.setArticleTextScale(size);
            }
        });
        return dialogTextSize;
    }

    @Override
    public void onDestroyView() {
        Timber.d("onDestroyView");
        super.onDestroyView();
        mUnbinder.unbind();
    }
}