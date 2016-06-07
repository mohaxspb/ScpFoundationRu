package ru.dante.scpfoundation.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import ru.dante.scpfoundation.R;

/**
 * Created for My Application by Dante on 28.02.2016  21:39.
 */
public class FavoriteUtils
{
    public static boolean hasFavoriteWithURL(Context ctx, String url)
    {
        final SharedPreferences sharedPreferencesFavorites = ctx.getSharedPreferences(ctx.getString(R.string.pref_favorites), Context.MODE_PRIVATE);
        return sharedPreferencesFavorites.contains(url);
    }

    @SuppressLint("CommitPrefEdits")
    public static void updateFavoritesOnDevice(Context ctx, String url, String title)
    {
        final SharedPreferences sharedPreferencesFavorites = ctx.getSharedPreferences(ctx.getString(R.string.pref_favorites), Context.MODE_PRIVATE);
        if (sharedPreferencesFavorites.contains(url))
        {
            sharedPreferencesFavorites.edit().remove(url).commit();
        } else
        {
            sharedPreferencesFavorites.edit().putString(url, title).commit();
        }
    }
}