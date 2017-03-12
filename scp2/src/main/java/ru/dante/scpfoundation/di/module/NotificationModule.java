package ru.dante.scpfoundation.di.module;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.manager.MyNotificationManager;

/**
 * Created by y.kuchanov on 22.12.16.
 *
 * for scp_ru
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