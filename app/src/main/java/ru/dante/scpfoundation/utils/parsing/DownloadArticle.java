package ru.dante.scpfoundation.utils.parsing;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.dante.scpfoundation.Article;

/**
 * Created by Dante on 09.01.2016 3:58.
 * For MyApplication.
 */
public class DownloadArticle extends AsyncTask<Void, Void, Article> {
    private static final String LOG = DownloadArticle.class.getSimpleName();
    private String url;
    private SetArticlesText interfaceSetText;

    public DownloadArticle(String url, SetArticlesText interfaceSetText) {
        this.url = url;
        this.interfaceSetText = interfaceSetText;
    }

    public static ArrayList<String> getArticlesTextParts(String html) {
        ArrayList<String> articlesTextParts = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Element contentPage = document.getElementById("page-content");
        if (contentPage == null) {
            contentPage = document.body();
        }
        for (Element element : contentPage.children()) {
            articlesTextParts.add(element.outerHtml());
        }
        return articlesTextParts;
    }

    public static ArrayList<TextType> getListOfTextTypes(ArrayList<String> articlesTextParts) {
        ArrayList<TextType> listOfTextTypes = new ArrayList<>();
        for (String textPart : articlesTextParts) {

            Element element = Jsoup.parse(textPart);
            Element ourElement = element.getElementsByTag("body").first().children().first();
            if (ourElement == null) {
                listOfTextTypes.add(TextType.Text);
                continue;
            }
            if (ourElement.tagName().equals("p")) {
                listOfTextTypes.add(TextType.Text);
                continue;
            }
            if (ourElement.className().equals("collapsible-block")) {
                listOfTextTypes.add(TextType.Spoiler);
                continue;
            }
            if (ourElement.tagName().equals("table")) {
                listOfTextTypes.add(TextType.Table);
                continue;
            }

            if (ourElement.className().equals("rimg")) {
                listOfTextTypes.add(TextType.Image);
                continue;
            }
            listOfTextTypes.add(TextType.Text);
        }

        return listOfTextTypes;
    }

    public static ArrayList<String> getSpoilerParts(String html) {
        ArrayList<String> spoilerParts = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Element element = document.getElementsByClass("collapsible-block-folded").first();
        Element elementA = element.getElementsByTag("a").first();
        spoilerParts.add(elementA.text());

        Element elementUnfolded = document.getElementsByClass("collapsible-block-unfolded").first();
        Element elementContent = elementUnfolded.getElementsByClass("collapsible-block-content").first();
        spoilerParts.add(elementContent.html());
        return spoilerParts;
    }

    public static Article getArticle(String url) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
//            System.out.println(response.body().string());
            Document doc = Jsoup.parse(response.body().string());
            org.jsoup.nodes.Element pageContent = doc.getElementById("page-content");
            if (pageContent == null) {
                return null;
            }
            Element p404 = pageContent.getElementById("404-message");
            if (p404 != null) {
                Article article = new Article();
                article.setURL(url);
                article.setArticlesText(p404.outerHtml());
                article.setTitle("404");

                return article;
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
//                aTag.attr("href",snoska.id());
            }

            //            замена ссылок в библиографии
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
            Element upperDivWithhLink = doc.getElementById("breadcrumbs");
            if (upperDivWithhLink != null) {
                pageContent.prependChild(upperDivWithhLink);
            }
            String articlesText = pageContent.toString();
            Article article = new Article();
            article.setURL(url);
            article.setArticlesText(articlesText);
            article.setTitle(title);

            return article;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected Article doInBackground(Void... params) {
        Log.d(LOG, "doInBackground started");

        return getArticle(url);
    }

    @Override
    protected void onPostExecute(Article result) {
        super.onPostExecute(result);
        this.interfaceSetText.setArticle(result);
    }

    public enum TextType {
        Text, Spoiler, Image, Table
    }

    public interface SetArticlesText {
        void setArticle(Article article);
    }
}