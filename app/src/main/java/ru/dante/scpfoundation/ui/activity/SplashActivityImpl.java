package ru.dante.scpfoundation.ui.activity;

import ru.kuchanov.scpcore.ui.activity.SplashActivity;

/**
 * Created by mohax on 10.07.2017.
 * <p>
 * for ScpFoundationRu
 */
public class SplashActivityImpl extends SplashActivity {

    @Override
    protected Class getLaunchActivityClass() {
        return LicenceActivityImpl.class;
    }
}