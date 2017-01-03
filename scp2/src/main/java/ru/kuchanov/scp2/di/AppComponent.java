package ru.kuchanov.scp2.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.kuchanov.scp2.di.module.AppModule;
import ru.kuchanov.scp2.di.module.NetModule;
import ru.kuchanov.scp2.di.module.PresentersModule;
import ru.kuchanov.scp2.di.module.StorageModule;
import ru.kuchanov.scp2.ui.fragment.AboutFragment;
import ru.kuchanov.scp2.ui.activity.MainActivity;

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
        NetModule.class
})
public interface AppComponent {
    void inject(MainActivity activity);

    void inject(AboutFragment fragment);
}