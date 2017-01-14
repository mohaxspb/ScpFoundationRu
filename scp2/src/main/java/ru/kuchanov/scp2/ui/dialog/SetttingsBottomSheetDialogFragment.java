package ru.kuchanov.scp2.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.SwitchCompat;
import android.view.View;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.manager.MyNotificationManager;
import ru.kuchanov.scp2.manager.MyPreferenceManager;
import timber.log.Timber;

/**
 * Created by mohax on 14.01.2017.
 * <p>
 * for scp_ru
 */
public class SetttingsBottomSheetDialogFragment extends BottomSheetDialogFragment {

    @BindView(R.id.root)
    View root;
    @BindView(R.id.notifIsOnSwitch)
    SwitchCompat notifIsOnSwitch;
    @BindView(R.id.notifLedisOnSwitch)
    SwitchCompat notifLedIsOnSwitch;
    @BindView(R.id.notifSoundIsOnSwitch)
    SwitchCompat notifSoundIsOnSwitch;
    @BindView(R.id.notifVibrateIsOnSwitch)
    SwitchCompat notifVibrateIsOnSwitch;

    private Unbinder unbinder;

    @Inject
    protected MyPreferenceManager mMyPreferenceManager;
    @Inject
    protected MyNotificationManager mMyNotificationManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet, null);
        dialog.setContentView(contentView);

        unbinder = ButterKnife.bind(this, dialog);

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

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }
}