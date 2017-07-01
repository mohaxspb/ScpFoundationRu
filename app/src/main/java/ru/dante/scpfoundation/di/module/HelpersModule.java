package ru.dante.scpfoundation.di.module;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.manager.MyNotificationManager;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.service.DownloadAllServiceTest;
import ru.dante.scpfoundation.ui.util.DialogUtils;
import ru.dante.scpfoundation.ui.util.DialogUtilsTest;

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
    DialogUtilsTest providesDialogUtilsTest(
            @NonNull MyPreferenceManager preferenceManager,
            @NonNull DbProviderFactory dbProviderFactory,
            @NonNull ApiClient apiClient
    ) {
        return new DialogUtilsTest(preferenceManager, dbProviderFactory, apiClient, DownloadAllServiceTest.class);
    }
}