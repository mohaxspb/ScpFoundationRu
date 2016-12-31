package ru.kuchanov.scp2.di.module;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.kuchanov.scp2.api.ApiClient;
import ru.kuchanov.scp2.db.DbProviderFactory;
import ru.kuchanov.scp2.manager.MyPreferenceManager;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
@Module
public class PresentersModule {

    @Provides
    @Singleton
    @NonNull
    Main.Presenter providesMainPresenter(
            @NonNull MyPreferenceManager myPreferencesManager,
            @NonNull DbProviderFactory dbProviderFactory,
            @NonNull ApiClient apiClient
            ) {
        return new MainPresenter(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Provides
    @Singleton
    @NonNull
    Nominees.Presenter providesNomineesPresenter(
            @NonNull MyPreferenceManager myPreferencesManager,
            @NonNull DbProviderFactory dbProviderFactory,
            @NonNull ApiClient apiClient
    ) {
        return new NomineesPresenter(myPreferencesManager, dbProviderFactory, apiClient);
    }
}