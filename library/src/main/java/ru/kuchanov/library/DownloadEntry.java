package ru.kuchanov.library;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * Created by mohax on 27.06.2017.
 * <p>
 * for ScpDownloads
 */
public class DownloadEntry {

    @StringRes
    public int resId;
    public String name;
    public String url;

    public DownloadEntry(@StringRes int resId, @NonNull String name, @NonNull String url) {
        this.resId = resId;
        this.name = name;
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DownloadEntry that = (DownloadEntry) o;

        return resId == that.resId && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = resId;
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}