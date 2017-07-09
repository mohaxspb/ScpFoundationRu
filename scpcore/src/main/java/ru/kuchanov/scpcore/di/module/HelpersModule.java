package ru.kuchanov.scpcore.di.module;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.kuchanov.scpcore.api.ApiClient;
import ru.kuchanov.scpcore.db.DbProviderFactory;
import ru.kuchanov.scpcore.manager.MyPreferenceManager;
import ru.kuchanov.scpcore.service.DownloadAllServiceImpl;
import ru.kuchanov.scpcore.ui.util.DialogUtils;
import ru.kuchanov.scpcore.ui.util.DownloadAllChooser;

/**
 * Created by y.kuchanov on 22.12.16.
 *
 * for scp_ru
 */
@Module
public class HelpersModule {

    @Provides
    @NonNull
    @Singleton
    DialogUtils providesDialogUtils(
            @NonNull MyPreferenceManager preferenceManager,
            @NonNull DbProviderFactory dbProviderFactory,
            @NonNull ApiClient apiClient
    ) {
        return new DialogUtils(preferenceManager, dbProviderFactory, apiClient);
    }

    @Provides
    @NonNull
    @Singleton
    DownloadAllChooser providesDialogUtilsTest(
            @NonNull MyPreferenceManager preferenceManager,
            @NonNull DbProviderFactory dbProviderFactory,
            @NonNull ApiClient apiClient
    ) {
        return new DownloadAllChooser(preferenceManager, dbProviderFactory, apiClient, DownloadAllServiceImpl.class);
    }
}