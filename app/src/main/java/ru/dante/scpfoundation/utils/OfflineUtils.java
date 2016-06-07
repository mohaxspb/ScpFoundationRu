package ru.dante.scpfoundation.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import ru.dante.scpfoundation.R;

/**
 * Created for My Application by Dante on 28.02.2016  21:39.
 */
public class OfflineUtils
{
    public static void clearAllData(Context ctx)
    {
        SharedPreferences pref=ctx.getSharedPreferences(ctx.getString(R.string.pref_offline), Context.MODE_PRIVATE);
        pref.edit().clear().commit();
    }

    public static String getTextByUrl(Context ctx, String url)
    {
        final SharedPreferences sharedPreferencesFavorites = ctx.getSharedPreferences(ctx.getString(R.string.pref_offline), Context.MODE_PRIVATE);
        return sharedPreferencesFavorites.getString(url + "text", "");
    }

    public static boolean hasOfflineWithURL(Context ctx, String url)
    {
        final SharedPreferences sharedPreferencesFavorites = ctx.getSharedPreferences(ctx.getString(R.string.pref_offline), Context.MODE_PRIVATE);
        return sharedPreferencesFavorites.contains(url);
    }

    @SuppressLint("CommitPrefEdits")
    public static void updateOfflineOnDevice(Context ctx, String url, String title, String text, boolean showInOfflineFragment)
    {
        final SharedPreferences sharedPreferencesFavorites = ctx.getSharedPreferences(ctx.getString(R.string.pref_offline), Context.MODE_PRIVATE);
        if (sharedPreferencesFavorites.contains(url))
        {
            sharedPreferencesFavorites.edit().remove(url).commit();
            sharedPreferencesFavorites.edit().remove(url + "text").commit();
            sharedPreferencesFavorites.edit().remove(url + "show").commit();
        } else
        {
//            if (VKSdk.isLoggedIn())
//            {
//                sharedPreferencesFavorites.edit().putString(url, title).commit();
//                sharedPreferencesFavorites.edit().putString(url+"text",text).commit();
//                sharedPreferencesFavorites.edit().putBoolean(url + "show", showInOfflineFragment).commit();
//            } else
//            {
//                if (sharedPreferencesFavorites.getAll().keySet().size() >= 1)
//                {
//                    VKUtils.showLoginDialog(ctx, "Функция неактивна\nАвторизуцтесь");
//                }
//                else {
            sharedPreferencesFavorites.edit().putString(url, title).commit();
            sharedPreferencesFavorites.edit().putString(url + "text", text).commit();
            sharedPreferencesFavorites.edit().putBoolean(url + "show", showInOfflineFragment).commit();
//                }
//            }
        }
    }
}