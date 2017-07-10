package ru.dante.scpfoundation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import ru.kuchanov.scpcore.ui.activity.LicenceActivity;
import ru.kuchanov.scpcore.ui.activity.SplashActivity;

/**
 * Created by mohax on 10.07.2017.
 * <p>
 * for ScpFoundationRu
 */
public class SplashActivityImpl extends SplashActivity {

//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        startActivity(new Intent(this, LicenceActivityImpl.class));
//        finishAffinity();
//    }

    @Override
    protected Class getLaunchActivityClass() {
        return LicenceActivityImpl.class;
    }
}