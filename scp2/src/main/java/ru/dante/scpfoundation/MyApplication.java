package ru.dante.scpfoundation;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.vk.sdk.VKSdk;
import com.yandex.metrica.YandexMetrica;

import io.realm.Realm;
import ru.dante.scpfoundation.di.AppComponent;
import ru.dante.scpfoundation.di.DaggerAppComponent;
import ru.dante.scpfoundation.di.module.AppModule;
import ru.dante.scpfoundation.di.module.PresentersModule;
import ru.dante.scpfoundation.di.module.StorageModule;
import ru.dante.scpfoundation.util.SystemUtils;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by mohax on 01.01.2017.
 * <p>
 * for scp_ru
 */
public class MyApplication extends MultiDexApplication {

    private static AppComponent sAppComponent;
    private static MyApplication sAppInstance;

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }

    public static MyApplication getAppInstance() {
        return sAppInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Инициализация AppMetrica SDK
        YandexMetrica.activate(getApplicationContext(), getString(R.string.yandex_metrica_api_key));
        // Отслеживание активности пользователей
        YandexMetrica.enableActivityAutoTracking(this);

        sAppInstance = this;
        sAppComponent = DaggerAppComponent.builder()
                .storageModule(new StorageModule())
                .appModule(new AppModule(this))
                .presentersModule(new PresentersModule())
                .build();

        if (BuildConfig.TIMBER_ENABLE) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected void log(int priority, String tag, String message, Throwable t) {
                    if (priority == Log.ERROR) {
                        //maybe send error via some service, i.e. firebase or googleAnalitics
                        super.log(priority, tag, message, t);
                    }
                }
            });
        }

        VKSdk.initialize(this);
        SystemUtils.printCertificateFingerprints();

        Realm.init(this);

//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
//                .setFontAttrId(R.attr.fontPath)
//                .build()
//        );

        //print versionCode
        Timber.d("VERSION_CODE: %s", BuildConfig.VERSION_CODE);
    }
}