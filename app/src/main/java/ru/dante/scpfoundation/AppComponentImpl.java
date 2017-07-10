package ru.dante.scpfoundation;

import javax.inject.Singleton;

import dagger.Component;
import ru.dante.scpfoundation.ui.activity.ArticleActivityImpl;
import ru.dante.scpfoundation.ui.activity.GalleryActivityImpl;
import ru.dante.scpfoundation.ui.activity.MainActivityImpl;
import ru.dante.scpfoundation.ui.activity.MaterialsActivityImpl;
import ru.dante.scpfoundation.ui.activity.TagSearchActivityImpl;
import ru.kuchanov.scpcore.di.AppComponent;
import ru.kuchanov.scpcore.di.module.AppModule;
import ru.kuchanov.scpcore.di.module.HelpersModule;
import ru.kuchanov.scpcore.di.module.NetModule;
import ru.kuchanov.scpcore.di.module.NotificationModule;
import ru.kuchanov.scpcore.di.module.PresentersModule;
import ru.kuchanov.scpcore.di.module.StorageModule;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
@Singleton
@Component(modules = {
        AppModule.class,
        StorageModule.class,
        PresentersModule.class,
        NetModule.class,
        NotificationModule.class,
        HelpersModuleImpl.class,
        HelpersModule.class
})
public interface AppComponentImpl extends AppComponent {

    void inject(ru.dante.scpfoundation.DownloadAllServiceImpl service);

    void inject(MainActivityImpl activity);

//    void inject(MainActivity activity);
//
    void inject(ArticleActivityImpl activity);

//    void inject(LicenceActivity activity);

    void inject(MaterialsActivityImpl activity);

    void inject(GalleryActivityImpl activity);

    void inject(TagSearchActivityImpl activity);

//    void inject(ArticleFragment fragment);
//
//    void inject(RecentArticlesFragment fragment);
//
//    void inject(RatedArticlesFragment fragment);
//
//    void inject(FavoriteArticlesFragment fragment);
//
//    void inject(OfflineArticlesFragment fragment);
//
//    void inject(Objects1ArticlesFragment fragment);
//
//    void inject(Objects2ArticlesFragment fragment);
//
//    void inject(Objects3ArticlesFragment fragment);
//
//    void inject(ObjectsRuArticlesFragment fragment);
//
//    void inject(SiteSearchArticlesFragment fragment);
//
//    void inject(MaterialsExperimentsFragment fragment);
//
//    void inject(MaterialsInterviewsFragment fragment);
//
//    void inject(MaterialsIncidentsFragment fragment);
//
//    void inject(MaterialsOtherFragment fragment);
//
//    void inject(MaterialsArchiveFragment fragment);
//
//    void inject(MaterialsJokesFragment fragment);
//
//    void inject(Objects4ArticlesFragment fragment);
//
//    void inject(TagsSearchFragment fragment);
//
//    void inject(TagsSearchResultsArticlesFragment fragment);
//
//    void inject(TextSizeDialogFragment dialogFragment);
//
//    void inject(NewVersionDialogFragment dialogFragment);
//
//    void inject(SubscriptionsFragmentDialog dialogFragment);
//
//    void inject(FreeAdsDisablingDialogFragment dialogFragment);
//
//    void inject(SetttingsBottomSheetDialogFragment dialogFragment);
//
//    void inject(ArticlesListRecyclerAdapter adapter);
//
//    void inject(ArticleRecyclerAdapter adapter);
//
//    void inject(SubscriptionsRecyclerAdapter adapter);
//
//    void inject(SettingsSpinnerAdapter adapter);
//
//    void inject(ArticleImageHolder holder);
//
//    void inject(ArticleTagsHolder holder);
//
//    void inject(ArticleTitleHolder holder);
//
//    void inject(ArticleTextHolder holder);
//
//    void inject(ArticleSpoilerHolder holder);
//
//    void inject(HolderSimple holder);
//
//    void inject(ReceiverTimer receiver);
//
//    void inject(ReceiverBoot receiver);
//
//    void inject(AppInstallReceiver receiver);
//
//    void inject(MyAdListener adListener);
//
//    void inject(MySkippableVideoCallbacks callbacks);
//
//    void inject(DownloadAllServiceImpl service);
}