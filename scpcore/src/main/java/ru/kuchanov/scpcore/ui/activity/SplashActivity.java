package ru.kuchanov.scpcore.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import ru.kuchanov.scpcore.util.StorageUtils;

/**
 * Created by Ivan Semkin on 4/23/2017.
 * <p>
 * for ScpFoundationRu
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, getLaunchActivityClass()));
        finishAffinity();
    }

    protected Class getLaunchActivityClass() {
        if (StorageUtils.fileExistsInAssets("licence.txt")) {
            return LicenceActivity.class;
        } else {
            return MainActivity.class;
        }
    }
}