package ru.dante.scpfoundation.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.dante.scpfoundation.di.module.AppModule;
import ru.dante.scpfoundation.di.module.NetModule;
import ru.dante.scpfoundation.di.module.NotificationModule;
import ru.dante.scpfoundation.di.module.PresentersModule;
import ru.dante.scpfoundation.di.module.StorageModule;
import ru.dante.scpfoundation.monetization.util.MyAdListener;
import ru.dante.scpfoundation.receivers.ReceiverBoot;
import ru.dante.scpfoundation.receivers.ReceiverTimer;
import ru.dante.scpfoundation.service.DownloadAllService;
import ru.dante.scpfoundation.ui.activity.ArticleActivity;
import ru.dante.scpfoundation.ui.activity.LicenceActivity;
import ru.dante.scpfoundation.ui.adapter.RecyclerAdapterArticle;
import ru.dante.scpfoundation.ui.adapter.RecyclerAdapterListArticles;
import ru.dante.scpfoundation.ui.adapter.RecyclerAdapterSubscriptions;
import ru.dante.scpfoundation.ui.dialog.SetttingsBottomSheetDialogFragment;
import ru.dante.scpfoundation.ui.dialog.ShowSubscriptionsFragmentDialog;
import ru.dante.scpfoundation.ui.dialog.TextSizeDialogFragment;
import ru.dante.scpfoundation.ui.fragment.ArticleFragment;
import ru.dante.scpfoundation.ui.activity.MainActivity;
import ru.dante.scpfoundation.ui.fragment.FavoriteArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.Objects1ArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.Objects2ArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.Objects3ArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.ObjectsRuArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.OfflineArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.RatedArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.RecentArticlesFragment;
import ru.dante.scpfoundation.ui.fragment.SiteSearchArticlesFragment;

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
        NotificationModule.class
})
public interface AppComponent {
    void inject(MainActivity activity);

    void inject(ArticleActivity activity);

    void inject(LicenceActivity activity);

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

    void inject(TextSizeDialogFragment dialogFragment);

    void inject(RecyclerAdapterListArticles adapterNewArticles);

    void inject(RecyclerAdapterArticle adapterNewArticles);

    void inject(DownloadAllService service);

    void inject(ReceiverTimer receiverTimer);

    void inject(ReceiverBoot receiverBoot);

    void inject(SetttingsBottomSheetDialogFragment bottomSheetDialogFragment);

    void inject(ShowSubscriptionsFragmentDialog fragmentDialog);

    void inject(RecyclerAdapterSubscriptions recyclerAdapterSubscriptions);

    void inject(MyAdListener adListener);
}