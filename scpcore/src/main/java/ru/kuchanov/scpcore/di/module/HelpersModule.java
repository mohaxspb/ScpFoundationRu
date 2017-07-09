package ru.kuchanov.scpcore.di.module;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.service.DownloadAllServiceImpl;
import ru.dante.scpfoundation.ui.util.DialogUtils;
import ru.dante.scpfoundation.ui.util.DownloadAllChooser;

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