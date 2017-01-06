package ru.kuchanov.scp2.api;

import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.RealmList;
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
import ru.kuchanov.scp2.db.model.RealmString;
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
                .doOnError(throwable -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                })
                .delay(2, TimeUnit.SECONDS)
                ;
    }

    public Observable<List<Article>> getRecentArticles(int offset) {
        return bindWithUtils(Observable.<List<Article>>create(subscriber -> {
            int page = offset / Constants.Api.NUM_OF_ARTICLES_ON_RECENT_PAGE + 1/*as pages are not zero based*/;

            Request request = new Request.Builder()
                    .url(BuildConfig.BASE_API_URL + Constants.Api.MOST_RECENT_URL + page)
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

    public Observable<List<Article>> getRatedArticles(int offset) {
        return bindWithUtils(Observable.<List<Article>>create(subscriber -> {
            int page = offset / Constants.Api.NUM_OF_ARTICLES_ON_RATED_PAGE + 1/*as pages are not zero based*/;

            Request request = new Request.Builder()
                    .url(BuildConfig.BASE_API_URL + Constants.Api.MOST_RATED_URL + page)
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
                Element pageContent = doc.getElementsByClass("list-pages-box").first();
                if (pageContent == null) {
                    subscriber.onError(new ScpParseException(MyApplication.getAppInstance().getString(R.string.error_parse)));
                    return;
                }

                List<Article> articles = new ArrayList<>();
                ArrayList<Element> listOfElements = pageContent.getElementsByClass("list-pages-item");
                for (int i = 0; i < listOfElements.size(); i++) {
                    Element tagP = listOfElements.get(i).getElementsByTag("p").first();
                    Element tagA = tagP.getElementsByTag("a").first();
                    String title = tagP.text();
                    String url = BuildConfig.BASE_API_URL + tagA.attr("href");
                    //remove a tag to leave only text with rating
                    tagA.remove();
                    tagP.text(tagP.text().replace(", рейтинг ", ""));
                    tagP.text(tagP.text().substring(0, tagP.text().length() - 1));
//                    Timber.d("tagP.text(): %s", tagP.text());
                    int rating = Integer.parseInt(tagP.text());
                    Article article = new Article();
                    article.title = title;
                    article.url = url;
                    article.rating = rating;
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

    public Observable<Article> getArticle(String url) {
        return bindWithUtils(Observable.<Article>create(subscriber -> {
            Request request = new Request.Builder()
                    .url(url)
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
                Element pageContent = doc.getElementById("page-content");
                if (pageContent == null) {
                    subscriber.onError(new ScpParseException(MyApplication.getAppInstance().getString(R.string.error_parse)));
                    return;
                }
                Element p404 = pageContent.getElementById("404-message");
                if (p404 != null) {
                    Article article = new Article();
                    article.url = url;
                    article.text = p404.outerHtml();
                    article.title = "404";

                    subscriber.onNext(article);
                    subscriber.onCompleted();
                    return;
                }
//            Log.d(LOG, pageContent.toString());
//            замена ссылок в сносках
                Elements footnoterefs = pageContent.getElementsByClass("footnoteref");
                for (Element snoska : footnoterefs) {
                    Element aTag = snoska.getElementsByTag("a").first();
                    String digits = "";
                    for (char c : aTag.id().toCharArray()) {
                        if (TextUtils.isDigitsOnly(String.valueOf(c))) {
                            digits += String.valueOf(c);
                        }
                    }
                    aTag.attr("href", digits);
                }
                Elements footnoterefsFooter = pageContent.getElementsByClass("footnote-footer");
                for (Element snoska : footnoterefsFooter) {
                    Element aTag = snoska.getElementsByTag("a").first();
                    snoska.prependText(aTag.text());
                    aTag.replaceWith(new Element(Tag.valueOf("pizda"), aTag.text()));
                }

                //замена ссылок в библиографии
                Elements bibliographi = pageContent.getElementsByClass("bibcite");
                for (Element snoska : bibliographi) {
                    Element aTag = snoska.getElementsByTag("a").first();
                    String onclickAttr = aTag.attr("onclick");

                    String id = onclickAttr.substring(onclickAttr.indexOf("bibitem-"), onclickAttr.lastIndexOf("'"));
                    aTag.attr("href", id);
                }
                Element rateDiv = pageContent.getElementsByClass("page-rate-widget-box").first();
                if (rateDiv != null) {
                    Element span1 = rateDiv.getElementsByClass("rateup").first();
                    span1.remove();
                    Element span2 = rateDiv.getElementsByClass("ratedown").first();
                    span2.remove();
                    Element span3 = rateDiv.getElementsByClass("cancel").first();
                    span3.remove();
                }
                Element svernut = pageContent.getElementById("toc-action-bar");
                if (svernut != null) {
                    svernut.remove();
                }
                Element titleEl = doc.getElementById("page-title");
                String title = "";
                if (titleEl != null) {
                    title = titleEl.text();
                }
                Element upperDivWithLink = doc.getElementById("breadcrumbs");
                if (upperDivWithLink != null) {
                    pageContent.prependChild(upperDivWithLink);
                }
                String rawText = pageContent.toString();

                //tabs
                boolean hasTabs = false;
                RealmList<RealmString> tabsText = null;
                RealmList<RealmString> tabsTitles = null;
                //articles textParts
                RealmList<RealmString> textParts = null;
                RealmList<RealmString> textPartsTypes = null;

                Document document = Jsoup.parse(rawText);
                Element yuiNavset = document.getElementsByAttributeValueStarting("class", "yui-navset").first();
                if (yuiNavset != null) {
                    hasTabs = true;

                    Element titles = yuiNavset.getElementsByClass("yui-nav").first();
                    Elements liElements = titles.getElementsByTag("li");
                    Element yuiContent = yuiNavset.getElementsByClass("yui-content").first();

                    tabsTitles = new RealmList<>();
                    for (Element li : liElements) {
                        RealmString realmString = new RealmString(li.text());
                        tabsTitles.add(realmString);
                    }
                    //TODO add supporting inner articles
                    tabsText = new RealmList<>();
                    for (Element tab : yuiContent.children()) {
                        RealmString realmString = new RealmString(tab.html());
                        tabsText.add(realmString);
                    }
                } else {
                    List<String> rawTextParts = ParseHtmlUtils.getArticlesTextParts(rawText);
                    textParts = new RealmList<>();
                    for (String textPart : rawTextParts) {
                        textParts.add(new RealmString(textPart));
                    }
                    textPartsTypes = new RealmList<>();
                    for (@ParseHtmlUtils.TextType String textPartType : ParseHtmlUtils.getListOfTextTypes(rawTextParts)) {
                        textPartsTypes.add(new RealmString(textPartType));
                    }
                }

                //finally fill article info
                Article article = new Article();

                article.url = url;
                article.text = rawText;
                article.title = title;
                //tabs
                article.hasTabs = hasTabs;
                article.tabsTitles = tabsTitles;
                article.tabsTexts = tabsText;
                //textParts
                article.textParts = textParts;
                article.textPartsTypes = textPartsTypes;

                subscriber.onNext(article);
                subscriber.onCompleted();
            } catch (Exception e) {
                Timber.e(e, "error while get arts list");
                subscriber.onError(e);
            }
        }));
    }
}