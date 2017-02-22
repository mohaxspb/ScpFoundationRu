package ru.dante.scpfoundation.mvp.base;

import ru.dante.scpfoundation.monetization.util.MyAdListener;

/**
 * Created by mohax on 15.01.2017.
 * <p>
 * for scp_ru
 */
public interface AdsActions {

    void initAds();

    void showInterstitial();

    void showInterstitial(MyAdListener adListener);

    boolean isTimeToShowAds();

    boolean isAdsLoaded();

    void requestNewInterstitial();

    void updateOwnedMarketItems();

    void showRewardedVideo();

    void startRewardedVideoFlow();
}