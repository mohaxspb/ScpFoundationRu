package ru.dante.scpfoundation.mvp.base;

import com.google.android.gms.ads.AdListener;

/**
 * Created by mohax on 15.01.2017.
 * <p>
 * for scp_ru
 */
public interface AdsActions {

    void initAds();

    void show();

    void show(AdListener adListener);

    boolean isTimeToShowAds();
}