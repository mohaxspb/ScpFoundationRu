package ru.dante.scpfoundation.util;

import android.content.Context;

import com.vk.sdk.util.VKUtil;

import ru.dante.scpfoundation.MyApplication;
import timber.log.Timber;

/**
 * Created by mohax on 01.01.2017.
 * <p>
 * for scp_ru
 */
public class SystemUtils {
    private static String[] getCertificateFingerprints(Context context) {
        return VKUtil.getCertificateFingerprint(context, context.getPackageName());
    }

    public static void printCertificateFingerprints() {
        String[] fingerprints = getCertificateFingerprints(MyApplication.getAppInstance());
        for (String sha1 : fingerprints) {
            Timber.d("sha1: %s", sha1);
        }
    }
}