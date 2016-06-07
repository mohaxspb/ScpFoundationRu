package ru.dante.scpfoundation.utils.parsing;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
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
import ru.dante.scpfoundation.Const;
import ru.dante.scpfoundation.fragments.FragmentJoke;

/**
 * Created for MyApplication by Dante on 11.04.2016  22:54.
 */
public class DownloadJoke extends AsyncTaskLoader<ArrayList<Article>>
{
    private static final String LOG = DownloadJoke.class.getSimpleName();
    private String url;

    public DownloadJoke(Context context, Bundle args)
    {
        super(context);
        this.url = args.getString(FragmentJoke.KEY_URL);
        onContentChanged();
    }

    @Override
    protected void onReset()
    {
        super.onReset();
        if (takeContentChanged())forceLoad();
    }

    @Override
    protected void onStartLoading()
    {
        Log.i(LOG,"onStartLoading called");
        super.onStartLoading();
        if (takeContentChanged())forceLoad();
    }

    @Override
    public ArrayList<Article> loadInBackground()
    {
        Log.d(LOG, "doInBackground started");
        ArrayList<Article> articles = new ArrayList<>();

        final OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = null;
        try
        {
            response = client.newCall(request).execute();

            Document doc = Jsoup.parse(response.body().string());
            Element pageContent = doc.getElementById("page-content");
            if (pageContent == null)
            {
                return null;
            }
            //now we will remove all html code before tag h2,with id toc1
            String allHtml = pageContent.html();
            int indexOfh2WithIdToc1 = allHtml.indexOf("<h2 id=\"toc1\">");
            allHtml = allHtml.substring(indexOfh2WithIdToc1);

            doc = Jsoup.parse(allHtml);

//            Log.d(LOG, doc.toString());
            Element h2withIdToc1 = doc.getElementById("toc1");
            h2withIdToc1.remove();

            Elements allh2Tags = doc.getElementsByTag("h2");
            for (Element h2Tag : allh2Tags)
            {
                Element brTag = new Element(Tag.valueOf("br"), "");
                h2Tag.replaceWith(brTag);
            }

            String allArticles = doc.getElementsByTag("body").first().html();
            String[] arrayOfArticles = allArticles.split("<br>");
            for (int i = 0; i < arrayOfArticles.length; i++)
            {
//                Log.d(LOG,arrayOfArticles[i]);
                String arrayItem = arrayOfArticles[i];
                doc = Jsoup.parse(arrayItem);
                String imageURL = doc.getElementsByTag("img").first().attr("src");
                String url = Const.DOMAIN_NAME + doc.getElementsByTag("a").first().attr("href");
                String title = doc.text();
                Article article = new Article();
                article.setURL(url);
                article.setImageUrl(imageURL);
                article.setTitle(title);
                articles.add(article);
            }

            return articles;
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}