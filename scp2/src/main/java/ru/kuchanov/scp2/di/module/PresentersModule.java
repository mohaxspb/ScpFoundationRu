package ru.kuchanov.scp2.di.module;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.kuchanov.scp2.api.ApiClient;
import ru.kuchanov.scp2.db.DbProviderFactory;
import ru.kuchanov.scp2.manager.MyPreferenceManager;
import ru.kuchanov.scp2.mvp.contract.ArticleMvp;
import ru.kuchanov.scp2.mvp.contract.ArticleScreenMvp;
import ru.kuchanov.scp2.mvp.contract.FavoriteArticlesMvp;
import ru.kuchanov.scp2.mvp.contract.MainMvp;
import ru.kuchanov.scp2.mvp.contract.Objects1Articles;
import ru.kuchanov.scp2.mvp.contract.Objects2Articles;
import ru.kuchanov.scp2.mvp.contract.Objects3Articles;
import ru.kuchanov.scp2.mvp.contract.ObjectsRuArticles;
import ru.kuchanov.scp2.mvp.contract.OfflineArticles;
import ru.kuchanov.scp2.mvp.contract.RatedArticlesMvp;
import ru.kuchanov.scp2.mvp.contract.RecentArticlesMvp;
import ru.kuchanov.scp2.mvp.contract.SiteSearchArticlesMvp;
import ru.kuchanov.scp2.mvp.presenter.ArticlePresenter;
import ru.kuchanov.scp2.mvp.presenter.ArticleScreenPresenter;
import ru.kuchanov.scp2.mvp.presenter.FavoriteArticlesPresenter;
import ru.kuchanov.scp2.mvp.presenter.MainPresenter;
import ru.kuchanov.scp2.mvp.presenter.MostRatedArticlesPresenter;
import ru.kuchanov.scp2.mvp.presenter.MostRecentArticlesPresenter;
import ru.kuchanov.scp2.mvp.presenter.Objects1ArticlesPresenter;
import ru.kuchanov.scp2.mvp.presenter.Objects2ArticlesPresenter;
import ru.kuchanov.scp2.mvp.presenter.Objects3ArticlesPresenter;
import ru.kuchanov.scp2.mvp.presenter.ObjectsRuArticlesPresenter;
import ru.kuchanov.scp2.mvp.presenter.OfflineArticlesPresenter;
import ru.kuchanov.scp2.mvp.presenter.SiteSearchArticlesPresenter;

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
    MainMvp.Presenter providesMainPresenter(
            @NonNull MyPreferenceManager myPreferencesManager,
            @NonNull DbProviderFactory dbProviderFactory,
            @NonNull ApiClient apiClient
    ) {
        return new MainPresenter(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Provides
    @NonNull
    ArticleScreenMvp.Presenter providesArticleScreenPresenter(
            @NonNull MyPreferenceManager myPreferencesManager,
            @NonNull DbProviderFactory dbProviderFactory,
            @NonNull ApiClient apiClient
    ) {
        return new ArticleScreenPresenter(myPreferencesManager, dbProviderFactory, apiClient);
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
    RecentArticlesMvp.Presenter providesRecentArticlesPresenter(
            @NonNull MyPreferenceManager myPreferencesManager,
            @NonNull DbProviderFactory dbProviderFactory,
            @NonNull ApiClient apiClient
    ) {
        return new MostRecentArticlesPresenter(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Provides
    @Singleton
    @NonNull
    RatedArticlesMvp.Presenter providesRatedArticlesPresenter(
            @NonNull MyPreferenceManager myPreferencesManager,
            @NonNull DbProviderFactory dbProviderFactory,
            @NonNull ApiClient apiClient
    ) {
        return new MostRatedArticlesPresenter(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Provides
    @Singleton
    @NonNull
    SiteSearchArticlesMvp.Presenter providesSiteSearchArticlesPresenter(
            @NonNull MyPreferenceManager myPreferencesManager,
            @NonNull DbProviderFactory dbProviderFactory,
            @NonNull ApiClient apiClient
    ) {
        return new SiteSearchArticlesPresenter(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Provides
    @Singleton
    @NonNull
    FavoriteArticlesMvp.Presenter providesFavoriteArticlesPresenter(
            @NonNull MyPreferenceManager myPreferencesManager,
            @NonNull DbProviderFactory dbProviderFactory,
            @NonNull ApiClient apiClient
    ) {
        return new FavoriteArticlesPresenter(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Provides
    @Singleton
    @NonNull
    OfflineArticles.Presenter providesOfflineArticlesPresenter(
            @NonNull MyPreferenceManager myPreferencesManager,
            @NonNull DbProviderFactory dbProviderFactory,
            @NonNull ApiClient apiClient
    ) {
        return new OfflineArticlesPresenter(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Provides
    @Singleton
    @NonNull
    Objects1Articles.Presenter providesObjects1ArticlesPresenter(
            @NonNull MyPreferenceManager myPreferencesManager,
            @NonNull DbProviderFactory dbProviderFactory,
            @NonNull ApiClient apiClient
    ) {
        return new Objects1ArticlesPresenter(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Provides
    @Singleton
    @NonNull
    Objects2Articles.Presenter providesObjects2ArticlesPresenter(
            @NonNull MyPreferenceManager myPreferencesManager,
            @NonNull DbProviderFactory dbProviderFactory,
            @NonNull ApiClient apiClient
    ) {
        return new Objects2ArticlesPresenter(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Provides
    @Singleton
    @NonNull
    Objects3Articles.Presenter providesObjects3ArticlesPresenter(
            @NonNull MyPreferenceManager myPreferencesManager,
            @NonNull DbProviderFactory dbProviderFactory,
            @NonNull ApiClient apiClient
    ) {
        return new Objects3ArticlesPresenter(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Provides
    @Singleton
    @NonNull
    ObjectsRuArticles.Presenter providesObjectsRuArticlesPresenter(
            @NonNull MyPreferenceManager myPreferencesManager,
            @NonNull DbProviderFactory dbProviderFactory,
            @NonNull ApiClient apiClient
    ) {
        return new ObjectsRuArticlesPresenter(myPreferencesManager, dbProviderFactory, apiClient);
    }
}