package ru.dante.scpfoundation;

import ru.kuchanov.scpcore.BaseApplication;
import ru.kuchanov.scpcore.di.AppComponent;
import ru.kuchanov.scpcore.di.module.AppModule;
import ru.kuchanov.scpcore.di.module.HelpersModule;
import ru.kuchanov.scpcore.di.module.PresentersModule;
import ru.kuchanov.scpcore.di.module.StorageModule;

/**
 * Created by mohax on 01.01.2017.
 * <p>
 * for scp_ru
 */
public class MyApplicationTest extends BaseApplication {

    @Override
    protected AppComponent buildAppComponentImpl() {
        return DaggerAppComponentImpl.builder()
                .storageModule(new StorageModule())
                .appModule(new AppModule(this))
                .presentersModule(new PresentersModule())
                .helpersModule(getHelpersModule())
                .build();
    }

    @Override
    protected HelpersModule getHelpersModule() {
        return new HelpersModuleImpl();
    }
}