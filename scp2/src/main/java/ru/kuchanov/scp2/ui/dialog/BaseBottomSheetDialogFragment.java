package ru.kuchanov.scp2.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.ui.base.BaseActivity;

/**
 * Created by mohax on 14.01.2017.
 * <p>
 * for scp_ru
 */
public abstract class BaseBottomSheetDialogFragment extends BottomSheetDialogFragment {

    @BindView(R.id.root)
    protected View root;

    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callInjection();
    }

    protected abstract void callInjection();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    protected BaseActivity getBaseActivity() {
        if (!(getActivity() instanceof BaseActivity)) {
            throw new RuntimeException("Activity must implement BaseActivity");
        }
        return (BaseActivity) getActivity();
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback;

    protected BottomSheetBehavior.BottomSheetCallback getBottomSheetBehaviorCallback() {
        if (mBottomSheetBehaviorCallback == null) {
            mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

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
        }
        return mBottomSheetBehaviorCallback;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), getLayoutResId(), null);
        dialog.setContentView(contentView);

        unbinder = ButterKnife.bind(this, dialog);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(getBottomSheetBehaviorCallback());
        }
    }

    protected abstract int getLayoutResId();
}