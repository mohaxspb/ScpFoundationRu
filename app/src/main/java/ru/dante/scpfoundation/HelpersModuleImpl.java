package ru.dante.scpfoundation;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.kuchanov.scpcore.api.ApiClient;
import ru.kuchanov.scpcore.db.DbProviderFactory;
import ru.kuchanov.scpcore.di.module.HelpersModule;
import ru.kuchanov.scpcore.manager.MyPreferenceManager;
import ru.kuchanov.scpcore.ui.util.DialogUtils;
import timber.log.Timber;

/**
 * Created by mohax on 10.07.2017.
 * <p>
 * for ScpFoundationRu
 */
@Module
class HelpersModuleImpl extends HelpersModule {

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
    ru.kuchanov.scp.downloads.DialogUtils providesDialogUtilsTest(
            @NonNull MyPreferenceManager preferenceManager,
            @NonNull DbProviderFactory dbProviderFactory,
            @NonNull ApiClient apiClient
    ) {
        Timber.d("Inject ru.dante.scpfoundation.DownloadAllChooser");
        return new ru.dante.scpfoundation.DownloadAllChooser(preferenceManager, dbProviderFactory, apiClient, ru.kuchanov.scpcore.service.DownloadAllServiceImpl.class);
    }
}