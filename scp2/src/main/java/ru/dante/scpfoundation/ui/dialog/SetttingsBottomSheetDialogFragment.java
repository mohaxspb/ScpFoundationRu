package ru.dante.scpfoundation.ui.dialog;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.StringDef;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.manager.MyNotificationManager;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.util.AttributeGetter;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

/**
 * Created by mohax on 14.01.2017.
 * <p>
 * for scp_ru
 */
public class SetttingsBottomSheetDialogFragment
        extends BaseBottomSheetDialogFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @StringDef({
            ListItemType.MIN,
            ListItemType.MIDDLE,
            ListItemType.MAX
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ListItemType {
        String MIN = "MIN";
        String MIDDLE = "MIDDLE";
        String MAX = "MAX";
    }

    //design
    @BindView(R.id.listItemStyle)
    View listItemStyle;
    @BindView(R.id.listItemSpinner)
    Spinner listItemSpinner;
    @BindView(R.id.fontPreferedTitle)
    TextView fontPreferedTitle;
    @BindView(R.id.fontPrefered)
    View fontPrefered;
    @BindView(R.id.fontPreferedSpinner)
    Spinner fontPreferedSpinner;
    //notif
    @BindView(R.id.notifIsOnSwitch)
    SwitchCompat notifIsOnSwitch;
    @BindView(R.id.notifLedisOnSwitch)
    SwitchCompat notifLedIsOnSwitch;
    @BindView(R.id.notifSoundIsOnSwitch)
    SwitchCompat notifSoundIsOnSwitch;
    @BindView(R.id.notifVibrateIsOnSwitch)
    SwitchCompat notifVibrateIsOnSwitch;

    @Inject
    protected MyPreferenceManager mMyPreferenceManager;
    @Inject
    protected MyNotificationManager mMyNotificationManager;

    public static BottomSheetDialogFragment newInstance() {
        return new SetttingsBottomSheetDialogFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_bottom_sheet_notif_settings;
    }

    @Override
    protected void callInjection() {
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        //design
        //card style
        listItemStyle.setOnClickListener(view -> {
            listItemSpinner.performClick();
        });
        String[] types = new String[]{ListItemType.MIN, ListItemType.MIDDLE, ListItemType.MAX};
        @ListItemType
        List<String> typesList = Arrays.asList(types);

        ArrayAdapter<String> adapterCard =
                new ArrayAdapter<>(getActivity(), R.layout.design_list_spinner_item, typesList);
        adapterCard.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Drawable.ConstantState spinnerDrawableConstantState = listItemSpinner.getBackground().getConstantState();
        if (spinnerDrawableConstantState != null) {
            Drawable spinnerDrawable = spinnerDrawableConstantState.newDrawable();
            spinnerDrawable.setColorFilter(AttributeGetter.getColor(getActivity(), R.attr.newArticlesTextColor), PorterDuff.Mode.SRC_ATOP);
            listItemSpinner.setBackground(spinnerDrawable);
        }

        listItemSpinner.setAdapter(adapterCard);
        listItemSpinner.setSelection(typesList.indexOf(mMyPreferenceManager.getListDesignType()));

        listItemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mMyPreferenceManager.setListDesignType(types[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //font
        CalligraphyUtils.applyFontToTextView(getActivity(), fontPreferedTitle, mMyPreferenceManager.getFontPath());
        fontPrefered.setOnClickListener(view -> {
            fontPreferedSpinner.performClick();
        });
        String[] fonts = getResources().getStringArray(R.array.fonts);
        @ListItemType
        List<String> fontsList = Arrays.asList(getResources().getStringArray(R.array.fonts_names));

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(), R.layout.design_list_spinner_item, fontsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Drawable.ConstantState fontsSpinnerDrawableConstantState = listItemSpinner.getBackground().getConstantState();
        if (fontsSpinnerDrawableConstantState != null) {
            Drawable spinnerDrawable = fontsSpinnerDrawableConstantState.newDrawable();
            spinnerDrawable.setColorFilter(AttributeGetter.getColor(getActivity(), R.attr.newArticlesTextColor), PorterDuff.Mode.SRC_ATOP);
            listItemSpinner.setBackground(spinnerDrawable);
        }

        fontPreferedSpinner.setAdapter(adapter);
        fontPreferedSpinner.setSelection(fontsList.indexOf(mMyPreferenceManager.getFontPath()));

        fontPreferedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mMyPreferenceManager.setFontPath(fonts[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //notif
        notifIsOnSwitch.setChecked(mMyPreferenceManager.isNotificationEnabled());
        notifLedIsOnSwitch.setChecked(mMyPreferenceManager.isNotificationLedEnabled());
        notifSoundIsOnSwitch.setChecked(mMyPreferenceManager.isNotificationSoundEnabled());
        notifVibrateIsOnSwitch.setChecked(mMyPreferenceManager.isNotificationVibrationEnabled());

        notifIsOnSwitch.setOnCheckedChangeListener((compoundButton, checked) -> {
            Timber.d("notifOnCheckChanged checked: %s", checked);
            mMyPreferenceManager.setNotificationEnabled(checked);
            mMyNotificationManager.checkAlarm();
        });

        notifLedIsOnSwitch.setOnCheckedChangeListener((compoundButton, checked) -> {
            Timber.d("notifOnCheckChanged checked: %s", checked);
            mMyPreferenceManager.setNotificationLedEnabled(checked);
            mMyNotificationManager.checkAlarm();
        });

        notifSoundIsOnSwitch.setOnCheckedChangeListener((compoundButton, checked) -> {
            Timber.d("notifOnCheckChanged checked: %s", checked);
            mMyPreferenceManager.setNotificationSoundEnabled(checked);
            mMyNotificationManager.checkAlarm();
        });

        notifVibrateIsOnSwitch.setOnCheckedChangeListener((compoundButton, checked) -> {
            Timber.d("notifOnCheckChanged checked: %s", checked);
            mMyPreferenceManager.setNotificationVibrationEnabled(checked);
            mMyNotificationManager.checkAlarm();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (!isAdded()) {
            return;
        }
        switch (key) {
            case MyPreferenceManager.Keys.DESIGN_FONT_PATH:
                CalligraphyUtils.applyFontToTextView(getActivity(), fontPreferedTitle, mMyPreferenceManager.getFontPath());
                break;
            default:
                //do nothing
                break;
        }
    }
}