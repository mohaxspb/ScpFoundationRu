package ru.dante.scpfoundation.di.module;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.contract.ArticleMvp;
import ru.dante.scpfoundation.mvp.contract.ArticleScreenMvp;
import ru.dante.scpfoundation.mvp.contract.FavoriteArticlesMvp;
import ru.dante.scpfoundation.mvp.contract.MainMvp;
import ru.dante.scpfoundation.mvp.contract.Objects1Articles;
import ru.dante.scpfoundation.mvp.contract.Objects2Articles;
import ru.dante.scpfoundation.mvp.contract.Objects3Articles;
import ru.dante.scpfoundation.mvp.contract.ObjectsRuArticles;
import ru.dante.scpfoundation.mvp.contract.OfflineArticles;
import ru.dante.scpfoundation.mvp.contract.RatedArticlesMvp;
import ru.dante.scpfoundation.mvp.contract.RecentArticlesMvp;
import ru.dante.scpfoundation.mvp.contract.SiteSearchArticlesMvp;
import ru.dante.scpfoundation.mvp.presenter.ArticlePresenter;
import ru.dante.scpfoundation.mvp.presenter.ArticleScreenPresenter;
import ru.dante.scpfoundation.mvp.presenter.FavoriteArticlesPresenter;
import ru.dante.scpfoundation.mvp.presenter.MainPresenter;
import ru.dante.scpfoundation.mvp.presenter.MostRatedArticlesPresenter;
import ru.dante.scpfoundation.mvp.presenter.MostRecentArticlesPresenter;
import ru.dante.scpfoundation.mvp.presenter.Objects1ArticlesPresenter;
import ru.dante.scpfoundation.mvp.presenter.Objects2ArticlesPresenter;
import ru.dante.scpfoundation.mvp.presenter.Objects3ArticlesPresenter;
import ru.dante.scpfoundation.mvp.presenter.ObjectsRuArticlesPresenter;
import ru.dante.scpfoundation.mvp.presenter.OfflineArticlesPresenter;
import ru.dante.scpfoundation.mvp.presenter.SiteSearchArticlesPresenter;

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