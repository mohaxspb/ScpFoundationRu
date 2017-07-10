package ru.dante.scpfoundation;

import javax.inject.Singleton;

import dagger.Component;
import ru.kuchanov.scpcore.di.AppComponent;
import ru.kuchanov.scpcore.di.module.AppModule;
import ru.kuchanov.scpcore.di.module.HelpersModule;
import ru.kuchanov.scpcore.di.module.NetModule;
import ru.kuchanov.scpcore.di.module.NotificationModule;
import ru.kuchanov.scpcore.di.module.PresentersModule;
import ru.kuchanov.scpcore.di.module.StorageModule;
import ru.kuchanov.scpcore.monetization.util.MyAdListener;
import ru.kuchanov.scpcore.monetization.util.MySkippableVideoCallbacks;
import ru.kuchanov.scpcore.receivers.AppInstallReceiver;
import ru.kuchanov.scpcore.receivers.ReceiverBoot;
import ru.kuchanov.scpcore.receivers.ReceiverTimer;
import ru.kuchanov.scpcore.service.DownloadAllServiceImpl;
import ru.kuchanov.scpcore.ui.activity.ArticleActivity;
import ru.kuchanov.scpcore.ui.activity.GalleryActivity;
import ru.kuchanov.scpcore.ui.activity.LicenceActivity;
import ru.kuchanov.scpcore.ui.activity.MainActivity;
import ru.kuchanov.scpcore.ui.activity.MaterialsActivity;
import ru.kuchanov.scpcore.ui.activity.TagSearchActivity;
import ru.kuchanov.scpcore.ui.adapter.ArticleRecyclerAdapter;
import ru.kuchanov.scpcore.ui.adapter.ArticlesListRecyclerAdapter;
import ru.kuchanov.scpcore.ui.adapter.SettingsSpinnerAdapter;
import ru.kuchanov.scpcore.ui.adapter.SubscriptionsRecyclerAdapter;
import ru.kuchanov.scpcore.ui.dialog.FreeAdsDisablingDialogFragment;
import ru.kuchanov.scpcore.ui.dialog.NewVersionDialogFragment;
import ru.kuchanov.scpcore.ui.dialog.SetttingsBottomSheetDialogFragment;
import ru.kuchanov.scpcore.ui.dialog.SubscriptionsFragmentDialog;
import ru.kuchanov.scpcore.ui.dialog.TextSizeDialogFragment;
import ru.kuchanov.scpcore.ui.fragment.ArticleFragment;
import ru.kuchanov.scpcore.ui.fragment.FavoriteArticlesFragment;
import ru.kuchanov.scpcore.ui.fragment.MaterialsArchiveFragment;
import ru.kuchanov.scpcore.ui.fragment.MaterialsExperimentsFragment;
import ru.kuchanov.scpcore.ui.fragment.MaterialsIncidentsFragment;
import ru.kuchanov.scpcore.ui.fragment.MaterialsInterviewsFragment;
import ru.kuchanov.scpcore.ui.fragment.MaterialsJokesFragment;
import ru.kuchanov.scpcore.ui.fragment.MaterialsOtherFragment;
import ru.kuchanov.scpcore.ui.fragment.Objects1ArticlesFragment;
import ru.kuchanov.scpcore.ui.fragment.Objects2ArticlesFragment;
import ru.kuchanov.scpcore.ui.fragment.Objects3ArticlesFragment;
import ru.kuchanov.scpcore.ui.fragment.Objects4ArticlesFragment;
import ru.kuchanov.scpcore.ui.fragment.ObjectsRuArticlesFragment;
import ru.kuchanov.scpcore.ui.fragment.OfflineArticlesFragment;
import ru.kuchanov.scpcore.ui.fragment.RatedArticlesFragment;
import ru.kuchanov.scpcore.ui.fragment.RecentArticlesFragment;
import ru.kuchanov.scpcore.ui.fragment.SiteSearchArticlesFragment;
import ru.kuchanov.scpcore.ui.fragment.TagsSearchFragment;
import ru.kuchanov.scpcore.ui.fragment.TagsSearchResultsArticlesFragment;
import ru.kuchanov.scpcore.ui.holder.ArticleImageHolder;
import ru.kuchanov.scpcore.ui.holder.ArticleSpoilerHolder;
import ru.kuchanov.scpcore.ui.holder.ArticleTagsHolder;
import ru.kuchanov.scpcore.ui.holder.ArticleTextHolder;
import ru.kuchanov.scpcore.ui.holder.ArticleTitleHolder;
import ru.kuchanov.scpcore.ui.holder.HolderSimple;

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
        HelpersModuleImpl.class
})
public interface AppComponentImpl extends AppComponent {

    //    void inject(DownloadAllServiceImpl service);
//    void inject(MainActivity activity);


//    void inject(MainActivity activity);
//
//    void inject(ArticleActivity activity);
//
//    void inject(LicenceActivity activity);
//
//    void inject(MaterialsActivity activity);
//
//    void inject(GalleryActivity activity);
//
//    void inject(TagSearchActivity activity);

    void inject(ru.dante.scpfoundation.DownloadAllServiceImpl service);

//    void inject(DownloadAllChooser chooser);
}