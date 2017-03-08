package ru.dante.scpfoundation.util;

import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;

import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;

/**
 * Created by mohax on 08.12.2016.
 * <p>
 * for scp_ru
 */
public class DimensionUtils {

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static int getDefaultMargin() {
        return MyApplication.getAppInstance().getResources().getDimensionPixelSize(R.dimen.defaultMargin);
    }

    public static boolean isLandscapeMode() {
        return DimensionUtils.getScreenWidth() > DimensionUtils.getScreenHeight();
    }

    public static int getActionBarHeight(FragmentActivity activity) {
        TypedValue tv = new TypedValue();
        if (activity.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
        } else {
            return 0;
        }
    }
}