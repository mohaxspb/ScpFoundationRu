package ru.dante.scpfoundation.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.dante.scpfoundation.di.module.HelpersModuleImpl;
import ru.dante.scpfoundation.di.module.NetModuleImpl;
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
        NetModuleImpl.class,
        NetModule.class,
        NotificationModule.class,
        HelpersModuleImpl.class,
        HelpersModule.class
})
public interface AppComponentImpl extends AppComponent {

    void inject(ru.dante.scpfoundation.DownloadAllServiceImpl service);
}
