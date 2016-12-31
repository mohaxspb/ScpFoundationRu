package ru.kuchanov.scp2.di.module;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
@Module
public class AppModule {
    private Application mApplication;

    public AppModule(@NonNull Application application) {
        mApplication = application;
    }

    @Provides
    @NonNull
    @Singleton
    Application providesApplication() {
        return mApplication;
    }

    @Provides
    @NonNull
    @Singleton
    Context providesContext() {
        return mApplication;
    }
}