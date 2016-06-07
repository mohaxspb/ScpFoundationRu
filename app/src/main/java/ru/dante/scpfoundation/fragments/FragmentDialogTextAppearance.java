package ru.dante.scpfoundation.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import ru.dante.scpfoundation.R;


public class FragmentDialogTextAppearance extends DialogFragment
{
    private final static String LOG = FragmentDialogTextAppearance.class.getSimpleName();
    private SharedPreferences pref;
    private Context ctx;

    public static FragmentDialogTextAppearance newInstance()
    {
        return new FragmentDialogTextAppearance();
    }

    @Override
    public void onCreate(Bundle savedState)
    {
        super.onCreate(savedState);
        Log.i(LOG, "onCreate");
        this.ctx = this.getActivity();

        this.pref = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Log.i(LOG, "onCreateDialog");

        final MaterialDialog dialogTextSize;

        MaterialDialog.Builder dialogTextSizeBuilder = new MaterialDialog.Builder(ctx);
        dialogTextSizeBuilder.title("Настройки размера текста")
                .positiveText("Закрыть")
                .customView(R.layout.dialog_text_size, true);

        dialogTextSize = dialogTextSizeBuilder.build();

        View customView = dialogTextSize.getCustomView();

        if (customView == null)
        {
            return dialogTextSize;
        }

        final SeekBar seekbarUI = (SeekBar) customView.findViewById(R.id.seekbar_ui);
        final SeekBar seekbarArticle = (SeekBar) customView.findViewById(R.id.seekbar_article);
        final TextView tvUi = (TextView) customView.findViewById(R.id.text_size_ui);
        final TextView tvArticle = (TextView) customView.findViewById(R.id.text_size_article);

        seekbarUI.setMax(150);
        final float scaleUI = pref.getFloat(ctx.getString(R.string.pref_design_key_text_size_ui), 0.75f);
//        Log.i(LOG, "size = " + String.valueOf(scaleUI));
        int curProgressUI = (int) ((scaleUI - 0.50f) * 100);
        seekbarUI.setProgress(curProgressUI);
        seekbarUI.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @SuppressLint("CommitPrefEdits")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
//                Log.d(LOG, "progress = " + String.valueOf(progress));
                float size = (progress / 100f) + 0.50f;
//                Log.i(LOG, "size = " + String.valueOf(size));
                tvUi.setTextSize(size * 21);
                pref.edit().putFloat(ctx.getString(R.string.pref_design_key_text_size_ui), size).commit();
            }
        });

        seekbarArticle.setMax(150);
        float scaleArt = pref.getFloat(ctx.getString(R.string.pref_design_key_text_size_article), 0.75f);
        int curProgressArt = (int) ((scaleArt - 0.50f) * 100);
        seekbarArticle.setProgress(curProgressArt);
        seekbarArticle.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @SuppressLint("CommitPrefEdits")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                float size = (progress / 100f) + 0.50f;
                tvArticle.setTextSize(size * 21);
                pref.edit().putFloat(ctx.getString(R.string.pref_design_key_text_size_article), size).commit();
            }
        });

        return dialogTextSize;
    }
}