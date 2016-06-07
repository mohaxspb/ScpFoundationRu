package ru.dante.scpfoundation.utils.parsing;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

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
import ru.dante.scpfoundation.Const;
import ru.dante.scpfoundation.otto.BusProvider;
import ru.dante.scpfoundation.otto.EventArticleDownloaded;
import ru.dante.scpfoundation.utils.MyUIL;
import ru.dante.scpfoundation.utils.OfflineUtils;

/**
 * Created by Dante on 09.01.2016.
 */
public class DownloadArticleForOffline extends AsyncTask<Void, Void, Article>
{
    private static final String LOG = DownloadArticleForOffline.class.getSimpleName();
    Context ctx;
    String url;
    String title = "";
   /* private int levelLimit;*/
    private int currentLevel;
    ArrayList<String> listOfLiks = new ArrayList<>();
    ArrayList<String> listOfImgs = new ArrayList<>();

    public DownloadArticleForOffline(Context ctx, String url, int currentLevel/*, int levelLimit*/)
    {
        this.ctx = ctx;
        this.url = url;
       /* this.levelLimit = levelLimit;*/
        this.currentLevel = currentLevel;
    }

    @Override
    protected Article doInBackground(Void... params)
    {

        Log.d(LOG, "doInBackground started: " + url);
        final OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = null;
        try
        {
            response = client.newCall(request).execute();
//            System.out.println(response.body().string());
            Document doc = Jsoup.parse(response.body().string());
            Element pageContent = doc.getElementById("page-content");
            if (pageContent == null)
            {
                return null;
            }
            Element p404 = pageContent.getElementById("404-message");
            if (p404 != null)
            {
                Article article = new Article();
                article.setURL(url);
                article.setArticlesText(p404.outerHtml());
                article.setTitle("404");

                return article;
            }
//            Log.d(LOG, pageContent.toString());
//            замена ссылок в сносках
            Elements footnoterefs = pageContent.getElementsByClass("footnoteref");
            for (Element snoska : footnoterefs)
            {
                Element aTag = snoska.getElementsByTag("a").first();
                String digits = "";
                for (char c : aTag.id().toCharArray())
                {
                    if (TextUtils.isDigitsOnly(String.valueOf(c)))
                    {
                        digits += String.valueOf(c);
                    }
                }
                aTag.attr("href", digits);
            }
            Elements footnoterefsFooter = pageContent.getElementsByClass("footnote-footer");
            for (Element snoska : footnoterefsFooter)
            {
                Element aTag = snoska.getElementsByTag("a").first();
                snoska.prependText(aTag.text());
                aTag.replaceWith(new Element(Tag.valueOf("pizda"), aTag.text()));
//                aTag.attr("href",snoska.id());
            }
            Element rateDiv = pageContent.getElementsByClass("page-rate-widget-box").first();
            if (rateDiv != null)
            {
                Element span1 = rateDiv.getElementsByClass("rateup").first();
                span1.remove();
                Element span2 = rateDiv.getElementsByClass("ratedown").first();
                span2.remove();
                Element span3 = rateDiv.getElementsByClass("cancel").first();
                span3.remove();
            }
            Element svernut = pageContent.getElementById("toc-action-bar");
            if (svernut != null)
            {
                svernut.remove();
            }
            Element titleEl = doc.getElementById("page-title");
            if (titleEl != null)
            {
                String title = titleEl.text();
                this.title = title;
            }
            Element upperDivWithhLink = doc.getElementById("breadcrumbs");
            if (upperDivWithhLink != null)
            {
                pageContent.prependChild(upperDivWithhLink);
            }
            String articlesText = pageContent.toString();
            Article article = new Article();
            article.setURL(url);
            article.setArticlesText(articlesText);
            article.setTitle(title);
/*Вытаскиваем внутренние ссылки*/
            Elements allLinks = pageContent.getElementsByTag("a");
            Log.i(LOG, "allLinks.size(): " + allLinks.size());
            for (Element a : allLinks)
            {
                String currentUrl = a.attr("href");
                Log.i(LOG, "currentUrl: " + currentUrl);
                if (currentUrl.startsWith("#"))
                {
                    continue;
                }
                if (currentUrl.startsWith("javascript"))
                {
                    continue;
                }
                if (currentUrl.contains("system:page-tags"))
                {
                    continue;
                }

                if (currentUrl.startsWith("/") || currentUrl.startsWith("http://scpfoundation.ru/"))
                {
                    if (currentUrl.startsWith("/"))
                    {
                        currentUrl = "http://scpfoundation.ru" + currentUrl;
                    }
                    if (!OfflineUtils.hasOfflineWithURL(ctx, currentUrl))
                    {
                        listOfLiks.add(currentUrl);
                    }
                }
            }
            Elements allImgs=pageContent.getElementsByTag("img");
            for (Element img:allImgs){
                String imgUrl=img.attr("src");
                if (imgUrl.startsWith("/")){
                    imgUrl= Const.DOMAIN_NAME+imgUrl;
                }
                listOfImgs.add(imgUrl);
            }
            return article;
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Article result)
    {
        super.onPostExecute(result);

        if (result == null)
        {
           if (currentLevel==0){
               Toast.makeText(ctx, "Connection lost", Toast.LENGTH_SHORT).show();
           }
        } else
        {
            boolean showOfflineFragment = (currentLevel == 0);
            OfflineUtils.updateOfflineOnDevice(ctx, url, title, result.getArticlesText(), showOfflineFragment);
            BusProvider.getInstance().post(new EventArticleDownloaded(result.getURL()));
            Log.i(LOG, "listOfLiks.size(): " + listOfLiks.size());
            if (currentLevel < Const.LEVEL_LIMIT - 1)
            {
                currentLevel++;
                for (String urlToDownloaded : listOfLiks)
                {
                    Log.i(LOG, urlToDownloaded);
                    DownloadArticleForOffline downloadArticleForOffline = new DownloadArticleForOffline(ctx, urlToDownloaded, currentLevel);
                    downloadArticleForOffline.execute();
                }
            }
            for (String currentImgUrl:listOfImgs){
                MyUIL.get(ctx).loadImage(currentImgUrl, MyUIL.getSimple(),null);
            }
        }
    }
}