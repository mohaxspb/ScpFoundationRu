package ru.dante.scpfoundation.di.module;

import android.support.annotation.NonNull;

import dagger.Module;
import ru.kuchanov.scpcore.api.ApiClient;
import ru.kuchanov.scpcore.db.DbProviderFactory;
import ru.kuchanov.scpcore.db.model.Article;
import ru.kuchanov.scpcore.di.module.HelpersModule;
import ru.kuchanov.scpcore.manager.MyPreferenceManager;
import ru.kuchanov.scpcore.service.DownloadAllServiceDefault;

/**
 * Created by mohax on 10.07.2017.
 * <p>
 * for ScpFoundationRu
 */
@Module
public class HelpersModuleImpl extends HelpersModule {

//    @Provides
//    @NonNull
//    @Singleton
//    public ru.kuchanov.scp.downloads.DialogUtils<Article> providesDialogUtilsTest(
//            @NonNull MyPreferenceManager preferenceManager,
//            @NonNull DbProviderFactory dbProviderFactory,
//            @NonNull ApiClient apiClient
//    ) {
//        return new ru.dante.scpfoundation.DownloadAllChooserDefault(
//                preferenceManager,
//                dbProviderFactory,
//                apiClient,
//                ru.kuchanov.scpcore.service.DownloadAllServiceDefault.class
//        );
//    }


    @Override
    protected ru.kuchanov.scp.downloads.DialogUtils<Article> getDialogUtilsTest(@NonNull MyPreferenceManager preferenceManager, @NonNull DbProviderFactory dbProviderFactory, @NonNull ApiClient apiClient) {
        return new ru.dante.scpfoundation.DownloadAllChooser(
                preferenceManager,
                dbProviderFactory,
                apiClient,
                DownloadAllServiceDefault.class
        );
    }
}