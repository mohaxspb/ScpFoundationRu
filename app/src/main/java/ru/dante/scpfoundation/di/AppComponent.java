package ru.dante.scpfoundation.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.dante.scpfoundation.di.module.AppModule;
import ru.dante.scpfoundation.di.module.HelpersModule;
import ru.dante.scpfoundation.di.module.NetModule;
import ru.dante.scpfoundation.di.module.NotificationModule;
import ru.dante.scpfoundation.di.module.PresentersModule;
import ru.dante.scpfoundation.di.module.StorageModule;
import ru.dante.scpfoundation.monetization.util.MyAdListener;
import ru.dante.scpfoundation.monetization.util.MySkippableVideoCallbacks;
import ru.dante.scpfoundation.receivers.AppInstallReceiver;
import ru.dante.scpfoundation.receivers.ReceiverBoot;
import ru.dante.scpfoundation.receivers.ReceiverTimer;
import ru.dante.scpfoundation.service.DownloadAllService;
import ru.dante.scpfoundation.ui.activity.ArticleActivity;
import ru.dante.scpfoundation.ui.activity.GalleryActivity;
import ru.dante.scpfoundation.ui.activity.LicenceActivity;
import ru.dante.scpfoundation.ui.activity.MainActivity;
import ru.dante.scpfoundation.ui.activity.MaterialsActivity;
import ru.dante.scpfoundation.ui.activity.TagSearchActivity;
import ru.dante.scpfoundation.ui.adapter.ArticleRecyclerAdapter;
import ru.dante.scpfoundation.ui.adapter.ArticlesListRecyclerAdapter;
import ru.dante.scpfoundation.ui.adapter.SettingsSpinnerAdapter;
import ru.dante.scpfoundation.ui.adapter.SubscriptionsRecyclerAdapter;
import ru.dante.scpfoundation.ui.dialog.FreeAdsDisablingDialogFragment;
import ru.dante.scpfoundation.ui.dialog.NewVersionDialogFragment;
import ru.dante.scpfoundation.ui.dialog.SetttingsBottomSheetDialogFragment;
import ru.dante.scpfoundation.ui.dialog.SubscriptionsFragmentDialog;
import ru.dante.scpfoundation.ui.dialog.TextSizeDialogFragment;
import ru.dante.scpfoundation.ui.fragment.ArticleFragment;
import ru.dante.scpfoundation.ui.fragment.FavoriteArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.MaterialsArchiveFragment;
import ru.dante.scpfoundation.ui.fragment.MaterialsExperimentsFragment;
import ru.dante.scpfoundation.ui.fragment.MaterialsIncidentsFragment;
import ru.dante.scpfoundation.ui.fragment.MaterialsInterviewsFragment;
import ru.dante.scpfoundation.ui.fragment.MaterialsJokesFragment;
import ru.dante.scpfoundation.ui.fragment.MaterialsOtherFragment;
import ru.dante.scpfoundation.ui.fragment.Objects1ArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.Objects2ArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.Objects3ArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.Objects4ArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.ObjectsRuArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.OfflineArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.RatedArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.RecentArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.SiteSearchArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.TagsSearchFragment;
import ru.dante.scpfoundation.ui.fragment.TagsSearchResultsArticlesFragment;
import ru.dante.scpfoundation.ui.holder.ArticleImageHolder;
import ru.dante.scpfoundation.ui.holder.ArticleSpoilerHolder;
import ru.dante.scpfoundation.ui.holder.ArticleTagsHolder;
import ru.dante.scpfoundation.ui.holder.ArticleTextHolder;
import ru.dante.scpfoundation.ui.holder.ArticleTitleHolder;
import ru.dante.scpfoundation.ui.holder.HolderSimple;

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
        HelpersModule.class
})
public interface AppComponent {
    void inject(MainActivity activity);

    void inject(ArticleActivity activity);

    void inject(LicenceActivity activity);

    void inject(MaterialsActivity activity);

    void inject(GalleryActivity activity);

    void inject(TagSearchActivity activity);

    void inject(ArticleFragment fragment);

    void inject(RecentArticlesFragment fragment);

    void inject(RatedArticlesFragment fragment);

    void inject(FavoriteArticlesFragment fragment);

    void inject(OfflineArticlesFragment fragment);

    void inject(Objects1ArticlesFragment fragment);

    void inject(Objects2ArticlesFragment fragment);

    void inject(Objects3ArticlesFragment fragment);

    void inject(ObjectsRuArticlesFragment fragment);

    void inject(SiteSearchArticlesFragment fragment);

    void inject(MaterialsExperimentsFragment fragment);

    void inject(MaterialsInterviewsFragment fragment);

    void inject(MaterialsIncidentsFragment fragment);

    void inject(MaterialsOtherFragment fragment);

    void inject(MaterialsArchiveFragment fragment);

    void inject(MaterialsJokesFragment fragment);

    void inject(Objects4ArticlesFragment fragment);

    void inject(TagsSearchFragment fragment);

    void inject(TagsSearchResultsArticlesFragment fragment);

    void inject(TextSizeDialogFragment dialogFragment);

    void inject(NewVersionDialogFragment dialogFragment);

    void inject(SubscriptionsFragmentDialog dialogFragment);

    void inject(FreeAdsDisablingDialogFragment dialogFragment);

    void inject(SetttingsBottomSheetDialogFragment dialogFragment);

    void inject(ArticlesListRecyclerAdapter adapter);

    void inject(ArticleRecyclerAdapter adapter);

    void inject(SubscriptionsRecyclerAdapter adapter);

    void inject(SettingsSpinnerAdapter adapter);

    void inject(ArticleImageHolder holder);

    void inject(ArticleTagsHolder holder);

    void inject(ArticleTitleHolder holder);

    void inject(ArticleTextHolder holder);

    void inject(ArticleSpoilerHolder holder);

    void inject(HolderSimple holder);

    void inject(DownloadAllService service);

    void inject(ReceiverTimer receiver);

    void inject(ReceiverBoot receiver);

    void inject(AppInstallReceiver receiver);

    void inject(MyAdListener adListener);

    void inject(MySkippableVideoCallbacks callbacks);
}