package ru.dante.scpfoundation;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.kuchanov.scpcore.api.ApiClient;
import ru.kuchanov.scpcore.db.DbProviderFactory;
import ru.kuchanov.scpcore.db.model.Article;
import ru.kuchanov.scpcore.di.module.HelpersModule;
import ru.kuchanov.scpcore.manager.MyPreferenceManager;

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
    ru.kuchanov.scp.downloads.DialogUtils<Article> providesDialogUtilsTest(
            @NonNull MyPreferenceManager preferenceManager,
            @NonNull DbProviderFactory dbProviderFactory,
            @NonNull ApiClient apiClient
    ) {
        return new ru.dante.scpfoundation.DownloadAllChooser(
                preferenceManager,
                dbProviderFactory,
                apiClient,
                ru.kuchanov.scpcore.service.DownloadAllServiceImpl.class
        );
    }
}