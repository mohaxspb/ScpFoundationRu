package ru.kuchanov.scp2.api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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


    public ApiClient(OkHttpClient okHttpClient, Retrofit retrofit, MyPreferenceManager preferencesManager) {
        mPreferencesManager = preferencesManager;
        mOkHttpClient = okHttpClient;
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
                ;
    }

    public Observable<List<Article>> getRecentArticles(int offset) {
        return bindWithUtils(Observable.<List<Article>>create(subscriber -> {
            int page = offset / Constants.Api.NUM_OF_ARTICLES_ON_RECENT_PAGE + 1/*as pages are not zero based*/;

            Request request = new Request.Builder()
                    .url("http://scpfoundation.ru/most-recently-created/p/" + page)
                    .build();

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
                    Element tableRow = listOfElements.get(i);
                    Elements listOfTd = tableRow.getElementsByTag("td");
                    //title and url
                    Element firstTd = listOfTd.first();
                    Element tagA = firstTd.getElementsByTag("a").first();
                    String title = tagA.text();
                    String url = BuildConfig.BASE_API_URL + tagA.attr("href");
                    //rating
                    Element ratingNode = listOfTd.get(1);
                    int rating = Integer.parseInt(ratingNode.text());
                    //author
                    Element spanWithAuthor = listOfTd.get(2)
                            .getElementsByAttributeValueContaining("class", "printuser").first();
                    String authorName = spanWithAuthor.text();
                    Element authorUrlNode = spanWithAuthor.getElementsByTag("a").first();
                    String authorUrl = authorUrlNode != null ? authorUrlNode.attr("href") : null;

                    //createdDate
                    Element createdDateNode = listOfTd.get(3);
                    String createdDate = createdDateNode.text().trim();
                    //updatedDate
                    Element updatedDateNode = listOfTd.get(4);
                    String updatedDate = updatedDateNode.text().trim();

                    Article article = new Article();
                    article.title = title;
                    article.url = url;
                    article.rating = rating;
                    article.authorName = authorName;
                    article.authorUrl = authorUrl;
                    article.createdDate = createdDate;
                    article.updatedDate = updatedDate;
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