package ru.dante.scpfoundation.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;

import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.utils.MyUIL;
import ru.dante.scpfoundation.utils.OfflineUtils;

/**
 * Created by Юрий on 21.09.2015 16:36.
 * This fragment shows general preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
public class FragmentPreferenceSystem extends PreferenceFragment
{
    private final static String LOG = FragmentPreferenceSystem.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_design, true);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_notification, true);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_system, true);

        addPreferencesFromResource(R.xml.pref_system);

        Preference prefCleaningImg = findPreference(getString(R.string.pref_cleaning_img_key));
        Preference prefCleaningText = findPreference(getString(R.string.pref_cleaning_text_key));
        prefCleaningImg.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                File cache = MyUIL.get(getActivity()).getDiskCache().getDirectory();
                File[] cached = cache.listFiles();

                long length = 0;
                for (File f : cached)
                {
                    length += f.length();
                }
                float cahceSizeInMB = (float) length / 1048576;

                String content = "Размер кэша на устройстве: \n" + String.format("%.2f", cahceSizeInMB) + " Мбайт"
                        + "\n\n" + "Расположение кэша: \n" + MyUIL.get(getActivity()).getDiskCache().getDirectory().getAbsolutePath();
                Log.i(LOG, content);
                MaterialDialog dialogWarning;
                MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(getActivity());

                dialogBuilder.title("Подтверждение удаления")
                        .content("Вы уверены, что хотите очистить кэш изображений?")
                        .positiveText("Да!")
                        .negativeText("Отмена")
                        .callback(new MaterialDialog.ButtonCallback()
                        {
                            @Override
                            public void onPositive(MaterialDialog dialog)
                            {
                                Log.i(LOG, "Image cache clear!");
                                MyUIL.get(getActivity()).getDiskCache().clear();
//                        dialogImgCache.cancel();
                            }
                        });
                dialogWarning = dialogBuilder.build();
                dialogWarning.show();
                return false;
            }
        });
        prefCleaningText.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                MaterialDialog dialogWarning;
                MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(getActivity());

                dialogBuilder.title("Подтверждение удаления")
                        .content("Вы уверены, что хотите очистить кэш текста?")
                        .positiveText("Да!")
                        .negativeText("Отмена")
                        .callback(new MaterialDialog.ButtonCallback()
                        {
                            @Override
                            public void onPositive(MaterialDialog dialog)
                            {
                                Log.i(LOG, "Text cache clear!");
                                OfflineUtils.clearAllData(getActivity());
                            }
                        });
                dialogWarning = dialogBuilder.build();
                dialogWarning.show();
                return false;
            }
        });
    }
}