package ru.dante.scpfoundation.api;

import com.google.gson.Gson;

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
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import ru.dante.scpfoundation.MyApplicationImpl;
import ru.dante.scpfoundation.R;
import ru.kuchanov.scp.downloads.ScpParseException;
import ru.kuchanov.scpcore.BaseApplication;
import ru.kuchanov.scpcore.ConstantValues;
import ru.kuchanov.scpcore.Constants;
import ru.kuchanov.scpcore.api.ApiClient;
import ru.kuchanov.scpcore.db.model.Article;
import ru.kuchanov.scpcore.manager.MyPreferenceManager;
import rx.Observable;
import timber.log.Timber;

/**
 * Created by mohax on 13.07.2017.
 * <p>
 * for ScpFoundationRu
 */
public class ApiClientImpl extends ApiClient {

    public ApiClientImpl(
            OkHttpClient okHttpClient,
            Retrofit vpsRetrofit,
            Retrofit scpRetrofit,
            MyPreferenceManager preferencesManager,
            Gson gson,
            ConstantValues constantValues
    ) {
        super(okHttpClient, vpsRetrofit, scpRetrofit, preferencesManager, gson, constantValues);
    }

    public Observable<String> getRandomUrl() {
        Timber.d("getRandomUrl");
        return bindWithUtils(Observable.unsafeCreate(subscriber -> {
            Request.Builder request = new Request.Builder();
            request.url(Constants.Api.RANDOM_PAGE_SCRIPT_URL);
            request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            request.addHeader("Accept-Encoding", "gzip, deflate, br");
            request.addHeader("Accept-Language", "en-US,en;q=0.8,de-DE;q=0.5,de;q=0.3");
            request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0");
            request.get();

            try {
                Response response = mOkHttpClient.newCall(request.build()).execute();

                Request requestResult = response.request();
                Timber.d("requestResult:" + requestResult);
                Timber.d("requestResult.url().url():" + requestResult.url().url());

                String randomURL = requestResult.url().url().toString();
                Timber.d("randomUrl = " + randomURL);

                subscriber.onNext(randomURL);
                subscriber.onCompleted();
            } catch (IOException e) {
                Timber.e(e);
                subscriber.onError(e);
            }
        }));
    }

    @Override
    public Observable<Integer> getRecentArticlesPageCountObservable() {
        return bindWithUtils(Observable.<Integer>unsafeCreate(subscriber -> {
            Request request = new Request.Builder()
                    .url(mConstantValues.getUrlsValues().getBaseApiUrl() + Constants.Api.MOST_RECENT_URL + 1)
                    .build();

            String responseBody = null;
            try {
                Response response = mOkHttpClient.newCall(request).execute();
                ResponseBody body = response.body();
                if (body != null) {
                    responseBody = body.string();
                } else {
                    subscriber.onError(new IOException(BaseApplication.getAppInstance().getString(ru.kuchanov.scpcore.R.string.error_parse)));
                    return;
                }
            } catch (IOException e) {
                subscriber.onError(new IOException(BaseApplication.getAppInstance().getString(ru.kuchanov.scpcore.R.string.error_connection)));
                return;
            }
            try {
                Document doc = Jsoup.parse(responseBody);

                //get num of pages
                Element spanWithNumber = doc.getElementsByClass("pager-no").first();
                String text = spanWithNumber.text();
                Integer numOfPages = Integer.valueOf(text.substring(text.lastIndexOf(" ") + 1));

                subscriber.onNext(numOfPages);
                subscriber.onCompleted();
            } catch (Exception e) {
                Timber.e(e, "error while get arts list");
                subscriber.onError(e);
            }
        }));
    }

    @Override
    protected List<Article> parseForRecentArticles(Document doc) throws ScpParseException {
        Element contentTypeDescription = doc.getElementsByClass("content-type-description").first();
        Element pageContent = contentTypeDescription.getElementsByTag("table").first();
        if (pageContent == null) {
            throw new ScpParseException(MyApplicationImpl.getAppInstance().getString(R.string.error_parse));
        }

        List<Article> articles = new ArrayList<>();
        Elements listOfElements = pageContent.getElementsByTag("tr");
        for (int i = 1/*start from 1 as first row is tables header*/; i < listOfElements.size(); i++) {
            Elements listOfTd = listOfElements.get(i).getElementsByTag("td");
            Element firstTd = listOfTd.first();
            Element tagA = firstTd.getElementsByTag("a").first();

            String title = tagA.text();
            String url = mConstantValues.getUrlsValues().getBaseApiUrl() + tagA.attr("href");
            //4 Jun 2017, 22:25
            //createdDate
            Element createdDateNode = listOfTd.get(1);
            String createdDate = createdDateNode.text().trim();

            Article article = new Article();
            article.title = title;
            article.url = url.trim();
            article.createdDate = createdDate;
            articles.add(article);
        }

        return articles;
    }

    @Override
    protected List<Article> parseForRatedArticles(Document doc) throws ScpParseException {
        Element pageContent = doc.getElementById("page-content");
        if (pageContent == null) {
            throw new ScpParseException(MyApplicationImpl.getAppInstance().getString(R.string.error_parse));
        }
        Element listPagesBox = pageContent.getElementsByClass("list-pages-box").first();
        if (listPagesBox == null) {
            throw new ScpParseException(MyApplicationImpl.getAppInstance().getString(R.string.error_parse));
        }

        String allArticles = listPagesBox.getElementsByTag("p").first().html();
        String[] arrayOfArticles = allArticles.split("<br>");
        List<Article> articles = new ArrayList<>();
        for (String arrayItem : arrayOfArticles) {
            doc = Jsoup.parse(arrayItem);
            Element aTag = doc.getElementsByTag("a").first();
            String url = mConstantValues.getUrlsValues().getBaseApiUrl() + aTag.attr("href");
            String title = aTag.text();

            String rating = arrayItem.substring(arrayItem.indexOf("rating: ") + "rating: ".length());
            rating = rating.substring(0, rating.indexOf(", "));

            Article article = new Article();
            article.url = url;
            article.rating = Integer.parseInt(rating);
            article.title = title;
            articles.add(article);
        }

        return articles;
    }
}