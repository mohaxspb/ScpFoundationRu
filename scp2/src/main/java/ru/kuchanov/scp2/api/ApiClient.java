package ru.kuchanov.scp2.api;

import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import ru.kuchanov.scp2.manager.MyPreferenceManager;
import rx.Observable;
import timber.log.Timber;

/**
 * Created by y.kuchanov on 22.12.16.
 *
 * for scp_ru
 */
public class ApiClient {
    private final MyPreferenceManager mPreferencesManager;

//    private final ApplicationsService mApplicationsService;
//    private final UserService mUserService;

    private final Converter<ResponseBody, Error> mConverter;

    public ApiClient(Retrofit retrofit, MyPreferenceManager preferencesManager) {
        mPreferencesManager = preferencesManager;

        mConverter = retrofit.responseBodyConverter(Error.class, new Annotation[0]);

//        mApplicationsService = retrofit.create(ApplicationsService.class);
//        mUserService = retrofit.create(UserService.class);
    }

    private <T> Observable<T> bindWithError(Observable<T> observable) {
        return observable
                .doOnError(throwable -> {
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                })
                .delay(2, TimeUnit.SECONDS).onErrorResumeNext(throwable -> {
            Timber.e("error catched: %s", throwable.getMessage());
            //TODO
            return Observable.error(throwable);
        });
    }
}