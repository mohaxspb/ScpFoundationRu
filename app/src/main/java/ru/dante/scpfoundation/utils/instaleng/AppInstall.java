package ru.dante.scpfoundation.utils.instaleng;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;

import ru.dante.scpfoundation.otto.BusProvider;
import ru.dante.scpfoundation.otto.EventAppInstall;

/**
 * Created by o.leonov on 06.10.2014.
 */
public class AppInstall
{
    private static final String LOG = AppInstall.class.getSimpleName();
    private WeakReference<Context> cntxRef;
    private static AppInstall instance;

    private AppInstall()
    {
    }

    public static AppInstall init(Activity act)
    {
        if (instance == null)
        {
            instance = new AppInstall();
            TimeSettings.setFirstStartTime(act);
        }
        instance.cntxRef = new WeakReference<Context>(act);

        return instance;
    }


    /***
     * Эта команда как раз и запускает диалог когда необходимо
     */
    public void showIfNeed()
    {
        //Показываем если прошло время и есть интернет(без интернета пользователь не может проголосовать)
        if (TimeSettings.needShowPreRateDialog(cntxRef.get()))
        {

            if (!isPackageExisted(cntxRef.get(), "ru.dante.scpfoundation.eng"))
            {
                Log.i(LOG, "app not installed");
                showRateDialog();
            } else
            {
                Log.i(LOG, "app already instal");
            }
        } else
        {
            Log.i(LOG, "No time to explain");
        }
    }

    public void showRateDialog()
    {
        BusProvider.getInstance().post(new EventAppInstall());
        Log.i(LOG, "Rate dialog called");
        // Ставим флаг, что надо показать позже(если пользователь в самом диалоге не выберет другой вариант)
        TimeSettings.setShowMode(cntxRef.get(), TimeSettings.SHOW_LATER);
        TimeSettings.saveLastShowTime(cntxRef.get());
    }

    private static String appName;

    public static String getApplicationName(Context context)
    {
        if (appName == null)
        {
            int stringId = context.getApplicationInfo().labelRes;
            appName = context.getString(stringId);
        }
        return appName;
    }

    public static boolean isConnected(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public boolean isPackageExisted(Context ctx, String targetPackage)
    {
        List<ApplicationInfo> packages;
        PackageManager pm;

        pm = ctx.getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages)
        {
            if (packageInfo.packageName.equals(targetPackage))
                return true;
        }
        return false;
    }
}
