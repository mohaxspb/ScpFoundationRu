package ru.dante.scpfoundation.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;

import ru.dante.scpfoundation.BuildConfig;
import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.monetization.model.OurApplication;
import ru.dante.scpfoundation.monetization.model.OurApplicationsResponse;
import ru.dante.scpfoundation.ui.activity.MainActivity;
import timber.log.Timber;

/**
 * Created by mohax on 05.03.2017.
 * <p>
 * for Vjux
 */
public class AppInstallReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 100;
    @Inject
    Gson mGson;
    @Inject
    MyPreferenceManager mMyPreferenceManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        MyApplication.getAppComponent().inject(this);
        String packageName = intent.getData().getEncodedSchemeSpecificPart();
        Timber.d("intent data: %s", packageName);

        initRemoteConfig();
        List<OurApplication> applications;
        try {
            applications = mGson.fromJson(FirebaseRemoteConfig.getInstance().
                    getString(Constants.Firebase.RemoteConfigKeys.APPS_TO_INSTALL_JSON), OurApplicationsResponse.class)
                    .items;
        } catch (Exception e) {
            Timber.e(e);
            return;
        }

        if (!mMyPreferenceManager.isAppInstalledForPackage(packageName) && applications.contains(new OurApplication(packageName))) {
            mMyPreferenceManager.setAppInstalledForPackage(packageName);
            mMyPreferenceManager.applyAwardForAppInstall();

            long numOfMillis = FirebaseRemoteConfig.getInstance()
                    .getLong(Constants.Firebase.RemoteConfigKeys.APP_INSTALL_REWARD_IN_MILLIS);
            long hours = numOfMillis / 1000 / 60 / 60;

            showNotificationSimple(context, context.getString(R.string.ads_reward_gained, hours), context.getString(R.string.thanks_for_supporting_us));

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, packageName);
            FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }
    }

    private void showNotificationSimple(Context context, String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT), 0);
        builder.setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setContentText(content)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    private void initRemoteConfig() {
        //remote config
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Create Remote Config Setting to enable developer mode.
        // Fetching configs from the server is normally limited to 5 requests per hour.
        // Enabling developer mode allows many more requests to be made per hour, so developers
        // can test different config values during development.
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        // Set default Remote Config values. In general you should have in app defaults for all
        // values that you may configure using Remote Config later on. The idea is that you
        // use the in app defaults and when you need to adjust those defaults, you set an updated
        // value in the App Manager console. Then the next time you application fetches from the
        // server, the updated value will be used. You can set defaults via an xml file like done
        // here or you can set defaults inline by using one of the other setDefaults methods.S
        // [START set_default_values]
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
    }
}