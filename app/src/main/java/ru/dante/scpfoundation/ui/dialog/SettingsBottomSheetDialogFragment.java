package ru.dante.scpfoundation.ui.dialog;

import android.Manifest;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.manager.MyNotificationManager;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.ui.adapter.SettingsSpinnerAdapter;
import ru.dante.scpfoundation.ui.base.BaseBottomSheetDialogFragment;
import ru.dante.scpfoundation.util.AttributeGetter;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

/**
 * Created by mohax on 14.01.2017.
 * <p>
 * for scp_ru
 */
public class SettingsBottomSheetDialogFragment
        extends BaseBottomSheetDialogFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener, FolderChooserDialog.FolderCallback {

    public static final String TAG = SettingsBottomSheetDialogFragment.class.getSimpleName();
    private static final int REQUEST_CODE_MEMORY_PERMISIION = 4242;

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

    @BindView(R.id.buy)
    TextView mActivateAutoSync;

    @BindView(R.id.dbLocationCurrent)
    TextView dbLocationCurrent;
    @BindView(R.id.dbSizeCurrent)
    TextView dbSizeCurrent;

    @Inject
    protected MyPreferenceManager mMyPreferenceManager;
    @Inject
    protected MyNotificationManager mMyNotificationManager;
    @Inject
    DbProviderFactory mDbProviderFactory;

    public static BottomSheetDialogFragment newInstance() {
        return new SettingsBottomSheetDialogFragment();
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
                new SettingsSpinnerAdapter(getActivity(), R.layout.design_list_spinner_item_font, fontsList, fontsPathsList);
        adapter.setDropDownViewResource(R.layout.design_list_spinner_item_font);

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
                    //close all except 2 for unsubscribed
//                    if (position > 1 && !getBaseActivity().getOwnedItems().isEmpty()) {
                    if (position > 1 && !mMyPreferenceManager.isHasSubscription()) {
                        Timber.d("show subs dialog");

                        fontPreferedSpinner.setSelection(fontsPathsList.indexOf(mMyPreferenceManager.getFontPath()));
                        showSnackBarWithAction(Constants.Firebase.CallToActionReason.ENABLE_FONTS);
                    } else {
                        mMyPreferenceManager.setFontPath(fontsPathsList.get(position));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Timber.d("onNothingSelected");
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

        //hide activate subs for good users
        mActivateAutoSync.setVisibility(getBaseActivity().getOwnedItems().isEmpty() ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.buy)
    void onActivateAutoSyncClicked() {
        Timber.d("onActivateAutoSyncClicked");
        dismiss();
        BottomSheetDialogFragment subsDF = SubscriptionsFragmentDialog.newInstance();
        subsDF.show(getActivity().getSupportFragmentManager(), subsDF.getTag());

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, Constants.Firebase.Analitics.StartScreen.AUTO_SYNC_FROM_SETTINGS);
        FirebaseAnalytics.getInstance(getActivity()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @OnClick(R.id.sync)
    void onSyncClicked() {
        Timber.d("onSyncClicked");
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            showSnackBarWithAction(Constants.Firebase.CallToActionReason.SYNC_NEED_AUTH);
            return;
        }
        getBaseActivity().createPresenter().syncData(true);
        dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialog1 -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog1;

            FrameLayout bottomSheet = (FrameLayout) d.findViewById(android.support.design.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        // Do something with your dialog like setContentView() or whatever
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
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

    @OnClick(R.id.dbLocationChange)
    void onDbLocationChangeClick() {
        Timber.d("onDbLocationChangeClicked");

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_MEMORY_PERMISIION
            );
            return;
        }

        new FolderChooserDialog.Builder(getBaseActivity())
                .chooseButton(R.string.choose)  // changes label of the choose button
                .allowNewFolder(true, R.string.new_folder_button_title)
                .show();
    }

    @OnClick(R.id.deleteAllDbData)
    void onDeleteAllDbDataClick() {
        Timber.d("onDeleteAllDbDataClick");
        //TODO
    }

    @Override
    public void onFolderSelection(@NonNull FolderChooserDialog dialog, @NonNull File folder) {
        Timber.d("onFolderSelection: %s", folder.getAbsolutePath());
        mMyPreferenceManager.setDbPath(folder.getAbsolutePath());

        mDbProviderFactory.getDbProvider().getRealm().writeCopyTo(new File(folder.getAbsolutePath() + "/default.realm"));
    }

    @Override
    public void onFolderChooserDismissed(@NonNull FolderChooserDialog dialog) {
        Timber.d("onFolderChooserDismissed");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_MEMORY_PERMISIION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        ) {
                    Timber.d("Permission memory granted!");
                } else {
                    Timber.d("Permission memory denied!");
                }
                break;
            default:
                Timber.wtf("unexpected request code: %s", requestCode);
                break;
        }
    }
}