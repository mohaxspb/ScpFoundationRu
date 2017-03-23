package ru.dante.scpfoundation.api;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.RealmList;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import ru.dante.scpfoundation.BuildConfig;
import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.api.error.ScpException;
import ru.dante.scpfoundation.api.error.ScpNoSearchResultsException;
import ru.dante.scpfoundation.api.error.ScpParseException;
import ru.dante.scpfoundation.api.model.response.VkGalleryResponse;
import ru.dante.scpfoundation.api.model.response.VkGroupJoinResponse;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.db.model.RealmString;
import ru.dante.scpfoundation.db.model.VkImage;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
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
    private Gson mGson;

    public ApiClient(OkHttpClient okHttpClient, Retrofit retrofit, MyPreferenceManager preferencesManager, Gson gson) {
        mPreferencesManager = preferencesManager;
        mOkHttpClient = okHttpClient;
        mGson = gson;
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

    public Observable<String> getRandomUrl() {
        return bindWithUtils(Observable.create(subscriber -> {
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

    public Observable<Integer> getRecentArticlesPageCount() {
        return bindWithUtils(Observable.<Integer>create(subscriber -> {
            Request request = new Request.Builder()
                    .url(BuildConfig.BASE_API_URL + Constants.Api.MOST_RECENT_URL + 1)
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

    public Observable<List<Article>> getRecentArticlesForOffset(int offset) {
        int page = offset / Constants.Api.NUM_OF_ARTICLES_ON_RECENT_PAGE + 1/*as pages are not zero based*/;
        return getRecentArticlesForPage(page);
    }

    public Observable<List<Article>> getRecentArticlesForPage(int page) {
        return bindWithUtils(Observable.<List<Article>>create(subscriber -> {
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
                    String title = tagP.text().substring(0, tagP.text().indexOf(", рейтинг"));
                    String url = BuildConfig.BASE_API_URL + tagA.attr("href");
                    //remove a tag to leave only text with rating
                    tagA.remove();
                    tagP.text(tagP.text().replace(", рейтинг ", ""));
                    tagP.text(tagP.text().substring(0, tagP.text().length() - 1));
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

    public Observable<List<Article>> getSearchArticles(int offset, String searchQuery) {
        return bindWithUtils(Observable.<List<Article>>create(subscriber -> {
            int page = offset / Constants.Api.NUM_OF_ARTICLES_ON_SEARCH_PAGE + 1/*as pages are not zero based*/;

            Request request = new Request.Builder()
                    .url(BuildConfig.BASE_API_URL + String.format(Locale.ENGLISH, Constants.Api.SEARCH_URL, searchQuery, page))
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

                List<Article> articles = new ArrayList<>();

                Element searchResults = pageContent.getElementsByClass("search-results").first();
                Elements items = searchResults.children();
                if (items.size() == 0) {
                    subscriber.onError(new ScpNoSearchResultsException(
                            MyApplication.getAppInstance().getString(R.string.error_no_search_results)));
                } else {
                    for (Element item : items) {
                        Element titleA = item.getElementsByClass("title").first().getElementsByTag("a").first();
                        String title = titleA.html();
                        String url = titleA.attr("href");
                        Element previewDiv = item.getElementsByClass("preview").first();
                        String preview = previewDiv.html();

                        Article article = new Article();

                        article.title = title;
                        article.url = url;
                        article.preview = preview;

                        articles.add(article);
                    }
                    subscriber.onNext(articles);
                    subscriber.onCompleted();
                }
            } catch (Exception e) {
                Timber.e(e, "error while get arts list");
                subscriber.onError(e);
            }
        }));
    }

    public Observable<List<Article>> getObjectsArticles(String sObjectsLink) {
        return bindWithUtils(Observable.<List<Article>>create(subscriber -> {
            Request request = new Request.Builder()
                    .url(sObjectsLink)
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

                List<Article> articles = new ArrayList<>();
                //parse
                Element listPagesBox = pageContent.getElementsByClass("list-pages-box").first();
                listPagesBox.remove();
                Element collapsibleBlock = pageContent.getElementsByClass("collapsible-block").first();
                collapsibleBlock.remove();
                Element table = pageContent.getElementsByTag("table").first();
                table.remove();
                Element h2 = doc.getElementById("toc0");
                h2.remove();

                //now we will remove all html code before tag h2,with id toc1
                String allHtml = pageContent.html();
                int indexOfh2WithIdToc1 = allHtml.indexOf("<h2 id=\"toc1\">");
                int indexOfhr = allHtml.indexOf("<hr>");
                allHtml = allHtml.substring(indexOfh2WithIdToc1, indexOfhr);

                doc = Jsoup.parse(allHtml);

                Element h2withIdToc1 = doc.getElementById("toc1");
                h2withIdToc1.remove();

                Elements allh2Tags = doc.getElementsByTag("h2");
                for (Element h2Tag : allh2Tags) {
                    Element brTag = new Element(Tag.valueOf("br"), "");
                    h2Tag.replaceWith(brTag);
                }

                String allArticles = doc.getElementsByTag("body").first().html();
                String[] arrayOfArticles = allArticles.split("<br>");
                for (String arrayItem : arrayOfArticles) {
                    doc = Jsoup.parse(arrayItem);
                    //type of object
                    String imageURL = doc.getElementsByTag("img").first().attr("src");
                    @Article.ObjectType
                    String type = getObjectTypeByImageUrl(imageURL);

                    String url = BuildConfig.BASE_API_URL + doc.getElementsByTag("a").first().attr("href");
                    String title = doc.text();

                    Article article = new Article();

                    article.url = url;
                    article.title = title;
                    article.type = type;
                    articles.add(article);
                }
                //parse end
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
                //remove rating bar
                Element rateDiv = pageContent.getElementsByClass("page-rate-widget-box").first();
                if (rateDiv != null) {
                    Element span1 = rateDiv.getElementsByClass("rateup").first();
                    span1.remove();
                    Element span2 = rateDiv.getElementsByClass("ratedown").first();
                    span2.remove();
                    Element span3 = rateDiv.getElementsByClass("cancel").first();
                    span3.remove();
                }
                //remove something more
                Element svernut = pageContent.getElementById("toc-action-bar");
                if (svernut != null) {
                    svernut.remove();
                }
                //get title
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
                        tabsTitles.add(new RealmString(li.text()));
                    }
                    //TODO add supporting inner articles
                    tabsText = new RealmList<>();
                    for (Element tab : yuiContent.children()) {
                        tabsText.add(new RealmString(tab.html()));
                    }
                } else {
                    List<String> rawTextParts = ParseHtmlUtils.getArticlesTextParts(rawText);
                    textParts = new RealmList<>();
                    for (String value : rawTextParts) {
                        textParts.add(new RealmString(value));
                    }
                    textPartsTypes = new RealmList<>();
                    for (@ParseHtmlUtils.TextType String value : ParseHtmlUtils.getListOfTextTypes(rawTextParts)) {
                        textPartsTypes.add(new RealmString(value));
                    }
                }

                //search for images and add it to separate field to be able to show it in arts lists
                RealmList<RealmString> imgsUrls = null;
                Elements imgs = pageContent.getElementsByTag("img");
                if (!imgs.isEmpty()) {
                    imgsUrls = new RealmList<>();
                    for (Element img : imgs) {
                        imgsUrls.add(new RealmString(img.attr("src")));
                    }
                }

                //type TODO fucking unformatted info!

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
                //images
                article.imagesUrls = imgsUrls;

                subscriber.onNext(article);
                subscriber.onCompleted();
            } catch (Exception e) {
                Timber.e(e, "error while get arts list");
                subscriber.onError(e);
            }
        }))
                .onErrorResumeNext(throwable -> Observable.error(new ScpException(throwable, url)));
    }

    public Observable<List<Article>> getMaterialsArticles(String objectsLink) {
        return bindWithUtils(Observable.<List<Article>>create(subscriber -> {
            Request request = new Request.Builder()
                    .url(objectsLink)
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

                List<Article> articles = new ArrayList<>();
                //parse
                List<Element> listOfElements = pageContent.getElementsByTag("ul");
                for (int i = 0; i < listOfElements.size(); i++) {
                    ArrayList<Element> listOfLi = listOfElements.get(i).getElementsByTag("li");
                    for (int u = 0; u < listOfLi.size(); u++) {
                        String url = listOfLi.get(u).getElementsByTag("a").first().attr("href");
                        if (!url.startsWith("http")) {
                            url = BuildConfig.BASE_API_URL + url;
                        }
                        String text = listOfLi.get(u).text();
                        Article article = new Article();
                        article.title = text;
                        article.url = url;
                        articles.add(article);
                    }
                }
                //parse end
                subscriber.onNext(articles);
                subscriber.onCompleted();
            } catch (Exception e) {
                Timber.e(e, "error while get arts list");
                subscriber.onError(e);
            }
        }));
    }

    public Observable<List<Article>> getMaterialsArchiveArticles() {
        return bindWithUtils(Observable.<List<Article>>create(subscriber -> {
            Request request = new Request.Builder()
                    .url(Constants.Urls.ARCHIVE)
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

                //now we will remove all html code before tag h2,with id toc1
                String allHtml = pageContent.html();
                int indexOfh2WithIdToc1 = allHtml.indexOf("<h2 id=\"toc1\">");
                int indexOfh2WithIdToc5 = allHtml.indexOf("<h2 id=\"toc5\">");
                allHtml = allHtml.substring(indexOfh2WithIdToc1, indexOfh2WithIdToc5);

                doc = Jsoup.parse(allHtml);

                Element h2withIdToc1 = doc.getElementById("toc1");
                h2withIdToc1.remove();

                Elements allh2Tags = doc.getElementsByTag("h2");
                for (Element h2Tag : allh2Tags) {
                    Element brTag = new Element(Tag.valueOf("br"), "");
                    h2Tag.replaceWith(brTag);
                }
                Elements allP = doc.getElementsByTag("p");
                allP.remove();
                Elements allUl = doc.getElementsByTag("ul");
                allUl.remove();

                List<Article> articles = new ArrayList<>();

                String allArticles = doc.getElementsByTag("body").first().html();
                String[] arrayOfArticles = allArticles.split("<br>");
                for (String arrayItem : arrayOfArticles) {
                    doc = Jsoup.parse(arrayItem);
                    String imageURL = doc.getElementsByTag("img").first().attr("src");
                    String url = BuildConfig.BASE_API_URL + doc.getElementsByTag("a").first().attr("href");
                    String title = doc.text();

                    @Article.ObjectType
                    String type = getObjectTypeByImageUrl(imageURL);

                    Article article = new Article();
                    article.url = url;
                    article.type = type;
                    article.title = title;
                    articles.add(article);
                }
                //parse end
                subscriber.onNext(articles);
                subscriber.onCompleted();
            } catch (Exception e) {
                Timber.e(e, "error while get arts list");
                subscriber.onError(e);
            }
        }));
    }

    public Observable<List<Article>> getMaterialsJokesArticles() {
        return bindWithUtils(Observable.<List<Article>>create(subscriber -> {
            Request request = new Request.Builder()
                    .url(Constants.Urls.JOKES)
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

                //now we will remove all html code before tag h2,with id toc1
                String allHtml = pageContent.html();
                int indexOfh2WithIdToc1 = allHtml.indexOf("<h2 id=\"toc1\">");
                allHtml = allHtml.substring(indexOfh2WithIdToc1);

                doc = Jsoup.parse(allHtml);

                Element h2withIdToc1 = doc.getElementById("toc1");
                h2withIdToc1.remove();

                Elements allh2Tags = doc.getElementsByTag("h2");
                for (Element h2Tag : allh2Tags) {
                    Element brTag = new Element(Tag.valueOf("br"), "");
                    h2Tag.replaceWith(brTag);
                }

                List<Article> articles = new ArrayList<>();

                String allArticles = doc.getElementsByTag("body").first().html();
                String[] arrayOfArticles = allArticles.split("<br>");
                for (String arrayItem : arrayOfArticles) {
//                    Timber.d("arrayItem: %s", arrayItem);
                    doc = Jsoup.parse(arrayItem);
                    String imageURL = doc.getElementsByTag("img").first().attr("src");
                    String url = BuildConfig.BASE_API_URL + doc.getElementsByTag("a").first().attr("href");
                    String title = doc.text();

                    @Article.ObjectType
                    String type = getObjectTypeByImageUrl(imageURL);

                    Article article = new Article();
                    article.url = url;
                    article.type = type;
                    article.title = title;
                    articles.add(article);
                }
                //parse end
                subscriber.onNext(articles);
                subscriber.onCompleted();
            } catch (Exception e) {
                Timber.e(e, "error while get arts list");
                subscriber.onError(e);
            }
        }));
    }

    public Observable<Boolean> joinVkGroup(String groupId) {
        Timber.d("joinVkGroup with groupId: %s", groupId);
        return bindWithUtils(Observable.<Boolean>create(subscriber -> {
                    VKParameters parameters = VKParameters.from(
                            VKApiConst.GROUP_ID, groupId,
                            VKApiConst.ACCESS_TOKEN, VKAccessToken.currentToken(),
                            VKApiConst.VERSION, BuildConfig.VK_API_VERSION
                    );

                    VKRequest vkRequest = VKApi.groups().join(parameters);
                    vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            Timber.d("onComplete: %s", response.responseString);
                            VkGroupJoinResponse vkGroupJoinResponse = mGson
                                    .fromJson(response.responseString, VkGroupJoinResponse.class);
                            Timber.d("vkGroupJoinResponse: %s", vkGroupJoinResponse);
                            subscriber.onNext(vkGroupJoinResponse.response == 1);
                            subscriber.onCompleted();
                        }

                        @Override
                        public void onError(VKError error) {
                            Timber.d("onError: %s", error);
                            subscriber.onError(new Throwable(error.toString()));
                        }
                    });
                })
        );
    }

    @Article.ObjectType
    private String getObjectTypeByImageUrl(String imageURL) {
        @Article.ObjectType
        String type;

        switch (imageURL) {
            case "http://scp-ru.wdfiles.com/local--files/scp-list-3/na.png":
            case "http://scp-ru.wdfiles.com/local--files/scp-list-2/na(1).png":
            case "http://scp-ru.wdfiles.com/local--files/scp-list-ru/na(1).png":
            case "http://scp-ru.wdfiles.com/local--files/scp-list/na.png":
            case "http://scp-ru.wdfiles.com/local--files/archive/na.png":
            case "http://scp-ru.wdfiles.com/local--files/scp-list-j/na(1).png":
                type = Article.ObjectType.NEUTRAL_OR_NOT_ADDED;
                break;
            case "http://scp-ru.wdfiles.com/local--files/scp-list-3/safe.png":
            case "http://scp-ru.wdfiles.com/local--files/scp-list-2/safe(1).png":
            case "http://scp-ru.wdfiles.com/local--files/scp-list-ru/safe(1).png":
            case "http://scp-ru.wdfiles.com/local--files/scp-list/safe.png":
            case "http://scp-ru.wdfiles.com/local--files/archive/safe.png":
            case "http://scp-ru.wdfiles.com/local--files/scp-list-j/safe(1).png":
                type = Article.ObjectType.SAFE;
                break;
            case "http://scp-ru.wdfiles.com/local--files/scp-list-3/euclid.png":
            case "http://scp-ru.wdfiles.com/local--files/scp-list-2/euclid(1).png":
            case "http://scp-ru.wdfiles.com/local--files/scp-list-ru/euclid(1).png":
            case "http://scp-ru.wdfiles.com/local--files/scp-list/euclid.png":
            case "http://scp-ru.wdfiles.com/local--files/archive/euclid.png":
            case "http://scp-ru.wdfiles.com/local--files/scp-list-j/euclid(1).png":
                type = Article.ObjectType.EUCLID;
                break;
            case "http://scp-ru.wdfiles.com/local--files/scp-list-3/keter.png":
            case "http://scp-ru.wdfiles.com/local--files/scp-list-2/keter(1).png":
            case "http://scp-ru.wdfiles.com/local--files/scp-list-ru/keter(1).png":
            case "http://scp-ru.wdfiles.com/local--files/scp-list/keter.png":
            case "http://scp-ru.wdfiles.com/local--files/archive/keter.png":
            case "http://scp-ru.wdfiles.com/local--files/scp-list-j/keter(1).png":
                type = Article.ObjectType.KETER;
                break;
            case "http://scp-ru.wdfiles.com/local--files/scp-list-3/thaumiel.png":
            case "http://scp-ru.wdfiles.com/local--files/scp-list-2/thaumiel(1).png":
            case "http://scp-ru.wdfiles.com/local--files/scp-list-ru/thaumiel(1).png":
            case "http://scp-ru.wdfiles.com/local--files/scp-list/thaumiel.png":
            case "http://scp-ru.wdfiles.com/local--files/archive/thaumiel.png":
            case "http://scp-ru.wdfiles.com/local--files/scp-list-j/thaumiel(1).png":
                type = Article.ObjectType.THAUMIEL;
                break;
            default:
                type = Article.ObjectType.NONE;
                break;
        }
        return type;
    }

    public Observable<List<VkImage>> getGallery() {
        Timber.d("getGallery");
        return bindWithUtils(Observable.create(subscriber -> {
                    VKParameters parameters = VKParameters.from(
                            VKApiConst.OWNER_ID, Constants.Api.GALLERY_VK_GROUP_ID,
                            VKApiConst.ALBUM_ID, Constants.Api.GALLERY_VK_ALBUM_ID,
                            VKApiConst.VERSION, BuildConfig.VK_API_VERSION
                    );

                    VKRequest vkRequest = new VKRequest("photos.get", parameters);
                    vkRequest.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            Timber.d("onComplete");
//                            Timber.d("onComplete: %s", response.responseString);
                            VkGalleryResponse attachments = mGson
                                    .fromJson(response.responseString, VkGalleryResponse.class);
//                            Timber.d("attachments: %s", attachments);
                            List<VkImage> images = convertAttachmentsToImage(attachments.response.items);
                            subscriber.onNext(images);
                            subscriber.onCompleted();
                        }

                        @Override
                        public void onError(VKError error) {
                            Timber.d("onError: %s", error);
                            subscriber.onError(new Throwable(error.toString()));
                        }
                    });
                })
        );
    }

    private List<VkImage> convertAttachmentsToImage(List<VKApiPhoto> attachments) {
        List<VkImage> images = new ArrayList<>();
        for (VKAttachments.VKApiAttachment attachment : attachments) {
            if (attachment.getId() == 456239049) {
                continue;
            }
            VKApiPhoto vkApiPhoto = (VKApiPhoto) attachment;

            VkImage image = new VkImage();
            image.id = vkApiPhoto.id;
            image.ownerId = vkApiPhoto.owner_id;
            image.date = vkApiPhoto.date;
            //size
            image.width = vkApiPhoto.width;
            image.height = vkApiPhoto.height;
            //urls
            image.photo75 = vkApiPhoto.photo_75;
            image.photo130 = vkApiPhoto.photo_130;
            image.photo604 = vkApiPhoto.photo_604;
            image.photo807 = vkApiPhoto.photo_807;
            image.photo1280 = vkApiPhoto.photo_1280;
            image.photo2560 = vkApiPhoto.photo_2560;

            image.allUrls = new RealmList<>();

            if (image.photo75 != null) {
                image.allUrls.add(new RealmString(image.photo75));
            }
            if (image.photo130 != null) {
                image.allUrls.add(new RealmString(image.photo130));
            }
            if (image.photo604 != null) {
                image.allUrls.add(new RealmString(image.photo604));
            }
            if (image.photo807 != null) {
                image.allUrls.add(new RealmString(image.photo807));
            }
            if (image.photo1280 != null) {
                image.allUrls.add(new RealmString(image.photo1280));
            }
            if (image.photo2560 != null) {
                image.allUrls.add(new RealmString(image.photo2560));
            }

            image.description = vkApiPhoto.text;

            images.add(image);
        }
        return images;
    }

    public Observable<VKApiUser> getUserDataFromVk() {
        return Observable.create(subscriber -> {
            VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_200")).executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    //noinspection unchecked
                    VKApiUser vkApiUser = ((VKList<VKApiUser>) response.parsedModel).get(0);
                    Timber.d("User name %s %s", vkApiUser.first_name, vkApiUser.last_name);

//                    User user = new User();
//                    user.network = User.NetworkType.VK;
//                    user.fullName = vkApiUser.first_name + " " + vkApiUser.last_name;
//                    user.firstName = vkApiUser.first_name;
//                    user.lastName = vkApiUser.last_name;
//                    user.avatar = vkApiUser.photo_200;

                    subscriber.onNext(vkApiUser);
                    subscriber.onCompleted();
                }

                @Override
                public void onError(VKError error) {
                    subscriber.onError(error.httpError);
                }
            });
        });
    }
}