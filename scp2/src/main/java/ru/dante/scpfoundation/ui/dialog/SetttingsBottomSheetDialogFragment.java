package ru.dante.scpfoundation.ui.dialog;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.manager.MyNotificationManager;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.ui.base.BaseBottomSheetDialogFragment;
import ru.dante.scpfoundation.util.AttributeGetter;
import ru.dante.scpfoundation.util.DimensionUtils;
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
        return R.layout.fragment_bottom_sheet_settings;
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
        listItemStyle.setOnClickListener(view -> listItemSpinner.performClick());
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

        listItemSpinner.post(() -> {
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
        });
        //font
        CalligraphyUtils.applyFontToTextView(getActivity(), fontPreferedTitle, mMyPreferenceManager.getFontPath());
        fontPrefered.setOnClickListener(view -> fontPreferedSpinner.performClick());

        List<String> fontsPathsList = Arrays.asList(getResources().getStringArray(R.array.fonts));
        @ListItemType
        List<String> fontsList = Arrays.asList(getResources().getStringArray(R.array.fonts_names));

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(), R.layout.design_list_spinner_item_font, fontsList) {
                    @Override
                    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View v = convertView;

                        if (v == null) {
                            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                            v = inflater.inflate(R.layout.design_list_spinner_item_font, parent, false);
                        }

                        String fontPath = fontsPathsList.get(position);
                        TextView textView = (TextView) v;
                        textView.setText(fontsList.get(position));
                        int padding = DimensionUtils.getDefaultMargin();
                        textView.setPadding(padding, padding, padding, padding);
                        CalligraphyUtils.applyFontToTextView(parent.getContext(), textView, fontPath);

                        boolean isNightMode = mMyPreferenceManager.isNightMode();
                        int backgroundColorSelected = isNightMode ? Color.parseColor("#33ECEFF1") : Color.parseColor("#33724646");
//                        int backgroundColorUnselected = AttributeGetter.getColor(parent.getContext(), R.attr.windowbackgroundOverrided);
                        int backgroundColorUnselected = isNightMode ? Color.parseColor("#3337474F") : Color.parseColor("#33ECEFF1");

                        boolean isSelected = position == fontsPathsList.indexOf(mMyPreferenceManager.getFontPath());
//                        Timber.d("isSelected: %s", isSelected);

                        v.setBackgroundColor(isSelected ? backgroundColorSelected : backgroundColorUnselected);

                        return v;
                    }

                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View v = convertView;

                        if (v == null) {
                            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                            v = inflater.inflate(R.layout.design_list_spinner_item_font, parent, false);
                        }

                        String fontPath = fontsPathsList.get(position);
                        TextView textView = (TextView) v;
                        textView.setText(fontsList.get(position));
                        int padding = DimensionUtils.getDefaultMarginSmall();
                        textView.setPadding(padding, padding, padding, padding);
                        CalligraphyUtils.applyFontToTextView(parent.getContext(), textView, fontPath);

                        boolean isNightMode = mMyPreferenceManager.isNightMode();
                        int backgroundColorSelected = isNightMode ? Color.parseColor("#ECEFF1") : Color.parseColor("#724646");
                        int backgroundColorUnselected = AttributeGetter.getColor(parent.getContext(), R.attr.windowbackgroundOverrided);

                        boolean isSelected = position == fontPath.indexOf(mMyPreferenceManager.getFontPath());

                        v.setBackgroundColor(isSelected ? backgroundColorSelected : backgroundColorUnselected);

                        return v;
                    }
                };

        Drawable.ConstantState fontsSpinnerDrawableConstantState = fontPreferedSpinner.getBackground().getConstantState();
        if (fontsSpinnerDrawableConstantState != null) {
            Drawable spinnerDrawable = fontsSpinnerDrawableConstantState.newDrawable();
            spinnerDrawable.setColorFilter(AttributeGetter.getColor(getActivity(), R.attr.newArticlesTextColor), PorterDuff.Mode.SRC_ATOP);
            fontPreferedSpinner.setBackground(spinnerDrawable);
        }

        fontPreferedSpinner.setAdapter(adapter);

        fontPreferedSpinner.setSelection(fontsPathsList.indexOf(mMyPreferenceManager.getFontPath()));

        fontPreferedSpinner.post(() -> {
            fontPreferedSpinner.setSelection(fontsList.indexOf(mMyPreferenceManager.getFontPath()));
            fontPreferedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    Timber.d("onItemSelected position: %s, font: %s", position, fontsList.get(position));
                    //TODO close all except 2 for unsubscribed
                    if (position > 1 && getBaseActivity().getOwnedItems().isEmpty()) {
                        Timber.d("show subs dialog");

                        fontPreferedSpinner.setSelection(fontsPathsList.indexOf(mMyPreferenceManager.getFontPath()));

                        Snackbar.make(fontPrefered, R.string.only_premium, Snackbar.LENGTH_LONG)
                                .setAction(R.string.activate, action -> {
                                    BottomSheetDialogFragment subsDF = SubscriptionsFragmentDialog.newInstance();
                                    subsDF.show(getChildFragmentManager(), subsDF.getTag());

                                    Bundle bundle = new Bundle();
                                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, Constants.Analitics.StartScreen.FONT);
                                    FirebaseAnalytics.getInstance(getActivity()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                })
                                .show();
                    } else {
                        mMyPreferenceManager.setFontPath(fontsPathsList.get(position));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
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