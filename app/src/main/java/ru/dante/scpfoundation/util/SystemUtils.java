package ru.dante.scpfoundation.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;

import com.vk.sdk.util.VKUtil;

import java.security.MessageDigest;

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
        Timber.d("sha fingerprints");
        for (String sha1 : fingerprints) {
//            System.out.println("sha1: " + sha1);
            Timber.d("sha1: %s", sha1);
        }

        try {
            Context context = MyApplication.getAppInstance();
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Timber.i("printHashKey() Hash Key: %s", hashKey);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            Timber.e(e);
        }
        return null;
    }

    public static void killApp(FragmentActivity activity) {
        //эмулируем нажатие на HOME, сворачивая приложение
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(i);

        //в зависимости от версии оси намертво убиваем приложение
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.finishAndRemoveTask();
        } else {
            ActivityCompat.finishAffinity(activity);
        }

        //и контрольный в голову
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}