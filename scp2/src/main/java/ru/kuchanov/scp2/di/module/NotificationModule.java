package ru.kuchanov.scp2.di.module;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.kuchanov.scp2.manager.MyPreferenceManager;
import ru.kuchanov.scp2.manager.MyNotificationManager;

/**
 * Created by y.kuchanov on 22.12.16.
 * <p>
 * for TappAwards
 */
@Module
public class NotificationModule {

    @Provides
    @NonNull
    @Singleton
    MyNotificationManager providesNotificationManager(@NonNull Context context, @NonNull MyPreferenceManager preferenceManager) {
        return new MyNotificationManager(context, preferenceManager);
    }
}