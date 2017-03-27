package ru.dante.scpfoundation.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.annotation.IdRes;
import android.view.View;

/**
 * Created by mohax on 06.03.2017.
 * <p>
 * for Vjux
 */
public class SecureUtils {
    private static final String ONE = "ru.";
    private static final String TWO = "dan";
    private static final String THREE = "te.";
    private static final String FOUR = "scpfoundation";

    /**
     * checks if view has child with given id
     */
    public static boolean checkIfBannerIsRemoved(View view, @IdRes int idOfBanner) {
        return view.findViewById(idOfBanner) == null;
    }

    public static boolean checkIfPackageChanged(Context context) {
        return !context.getPackageName().equals(ONE + TWO + THREE + FOUR);
    }

    public static boolean checkLuckyPatcher(Context context) {
        return packageExists(context, "com.dimonvideo.luckypatcher") ||
                packageExists(context, "com.android.protips") ||
                packageExists(context, "com.chelpus.lackypatch") ||
                packageExists(context, "com.android.vending.billing.InAppBillingService.LACK");
    }

    private static boolean packageExists(Context context, final String packageName) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, 0);

            if (info == null) {
                // No need really to test for null, if the package does not
                // exist it will really rise an exception. but in case Google
                // changes the API in the future lets be safe and test it
                return false;
            }

            return true;
        } catch (Exception ex) {
            // If we get here only means the Package does not exist
        }

        return false;
    }
}