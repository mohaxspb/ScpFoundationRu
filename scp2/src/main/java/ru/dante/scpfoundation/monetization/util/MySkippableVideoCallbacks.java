package ru.dante.scpfoundation.monetization.util;

import com.appodeal.ads.SkippableVideoCallbacks;

import timber.log.Timber;

/**
 * Created by mohax on 12.03.2017.
 * <p>
 * for scp_ru
 */
public class MySkippableVideoCallbacks implements SkippableVideoCallbacks {

    @Override
    public void onSkippableVideoLoaded() {
        Timber.d("onSkippableVideoLoaded");
    }

    @Override
    public void onSkippableVideoFailedToLoad() {
        Timber.d("onSkippableVideoFailedToLoad");
    }

    @Override
    public void onSkippableVideoShown() {
        Timber.d("onSkippableVideoShown");
    }

    @Override
    public void onSkippableVideoFinished() {
        Timber.d("onSkippableVideoFinished");
    }

    @Override
    public void onSkippableVideoClosed(boolean finished) {
        Timber.d("onSkippableVideoClosed: %s", finished);
    }
}