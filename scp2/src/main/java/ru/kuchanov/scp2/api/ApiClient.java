package ru.kuchanov.scp2.api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import ru.kuchanov.scp2.BuildConfig;
import ru.kuchanov.scp2.Constants;
import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.api.error.ScpParseException;
import ru.kuchanov.scp2.db.model.Article;
import ru.kuchanov.scp2.manager.MyPreferenceManager;
import rx.Observable;
import timber.log.Timber;

/**
 * Created by y.kuchanov on 22.12.16.
 * <p>
 * for scp_ru
 */
public class ApiClient {
    private final MyPreferenceManager mPreferencesManager;
    private final OkHttpClient mOkHttpClient;

//    private final ApplicationsService mApplicationsService;
//    private final UserService mUserService;

    private final Converter<ResponseBody, Error> mConverter;

    public ApiClient(OkHttpClient okHttpClient, Retrofit retrofit, MyPreferenceManager preferencesManager) {
        mPreferencesManager = preferencesManager;
        mOkHttpClient = okHttpClient;

        mConverter = retrofit.responseBodyConverter(Error.class, new Annotation[0]);

//        mApplicationsService = retrofit.create(ApplicationsService.class);
//        mUserService = retrofit.create(UserService.class);
    }

    private <T> Observable<T> bindWithUtils(Observable<T> observable) {
        return observable
//                .doOnError(throwable -> {
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                })
//                .delay(2, TimeUnit.SECONDS)
                .onErrorResumeNext(throwable -> {
                    Timber.e("error catched: %s", throwable.getMessage());
                    //TODO create own exception
                    return Observable.error(throwable);
                });
    }

    public Observable<List<Article>> getRecentArticles(int offset) {
        return bindWithUtils(Observable.<List<Article>>create(subscriber -> {
            int page = offset / Constants.Api.NUM_OF_ARTICLES_ON_RECENT_PAGE + 1/*as pages are not zero based*/;

            Request request = new Request.Builder()
                    .url("http://scpfoundation.ru/most-recently-created/p/" + page)
                    .build();

//            Response response = null;
            String responseBody = null;
            try {
                Response response = mOkHttpClient.newCall(request).execute();
                responseBody = response.body().string();
            } catch (IOException e) {
                subscriber.onError(new IOException(MyApplication.getAppInstance().getString(R.string.error_connection)));
                return;
            }
            try {
                Document doc = Jsoup.parse(responseBody);
                Element pageContent = doc.getElementsByClass("wiki-content-table").first();
                if (pageContent == null) {
                    subscriber.onError(new ScpParseException(MyApplication.getAppInstance().getString(R.string.error_parse)));
                    return;
                }

                List<Article> articles = new ArrayList<>();
                Elements listOfElements = pageContent.getElementsByTag("tr");
                for (int i = 1/*start from 1 as first row is tables geader*/; i < listOfElements.size(); i++) {
                    Elements listOfTd = listOfElements.get(i).getElementsByTag("td");
                    Element firstTd = listOfTd.first();
                    Element tagA = firstTd.getElementsByTag("a").first();
                    String title = tagA.text();
                    String url = BuildConfig.BASE_API_URL + tagA.attr("href");
                    String authorName = listOfElements.get(i)
                            .getElementsByAttributeValueContaining("class", "printuser").first().text();
                    Article article = new Article();
                    article.title = title;
                    article.url = url;
                    article.authorName = authorName;
                    articles.add(article);
                }
                subscriber.onNext(articles);
                subscriber.onCompleted();
            } catch (Exception e) {
                Timber.e(e, "error while get arts list");
                subscriber.onError(e);
            }
        }));
    }
}