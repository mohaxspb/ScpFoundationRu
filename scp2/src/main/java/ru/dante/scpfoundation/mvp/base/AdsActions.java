package ru.dante.scpfoundation.mvp.base;

import com.google.android.gms.ads.AdListener;

import ru.dante.scpfoundation.monetization.util.MyAdListener;

/**
 * Created by mohax on 15.01.2017.
 * <p>
 * for scp_ru
 */
public interface AdsActions {

    void initAds();

    void showAds();

    void showAds(MyAdListener adListener);

    boolean isTimeToShowAds();

    boolean isAdsLoaded();
}