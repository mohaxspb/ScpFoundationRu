package ru.kuchanov.scp2.di.module;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.kuchanov.scp2.api.ApiClient;
import ru.kuchanov.scp2.db.DbProviderFactory;
import ru.kuchanov.scp2.manager.MyPreferenceManager;
import ru.kuchanov.scp2.mvp.contract.ArticleMvp;
import ru.kuchanov.scp2.mvp.contract.Main;
import ru.kuchanov.scp2.mvp.contract.RatedArticles;
import ru.kuchanov.scp2.mvp.contract.RecentArticles;
import ru.kuchanov.scp2.mvp.presenter.ArticlePresenter;
import ru.kuchanov.scp2.mvp.presenter.MainPresenter;
import ru.kuchanov.scp2.mvp.presenter.MostRatedArticlesPresenter;
import ru.kuchanov.scp2.mvp.presenter.MostRecentArticlesPresenter;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for scp_ru
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
//    @Singleton
    @NonNull
    ArticleMvp.Presenter providesArticlePresenter(
            @NonNull MyPreferenceManager myPreferencesManager,
            @NonNull DbProviderFactory dbProviderFactory,
            @NonNull ApiClient apiClient
    ) {
        return new ArticlePresenter(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Provides
    @Singleton
    @NonNull
    RecentArticles.Presenter providesRecentArticlesPresenter(
            @NonNull MyPreferenceManager myPreferencesManager,
            @NonNull DbProviderFactory dbProviderFactory,
            @NonNull ApiClient apiClient
    ) {
        return new MostRecentArticlesPresenter(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Provides
    @Singleton
    @NonNull
    RatedArticles.Presenter providesRatedArticlesPresenter(
            @NonNull MyPreferenceManager myPreferencesManager,
            @NonNull DbProviderFactory dbProviderFactory,
            @NonNull ApiClient apiClient
    ) {
        return new MostRatedArticlesPresenter(myPreferencesManager, dbProviderFactory, apiClient);
    }
}