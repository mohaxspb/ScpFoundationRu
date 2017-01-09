package ru.dante.scpfoundation.utils.parsing;

import android.content.Context;
import android.os.AsyncTask;
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
import ru.dante.scpfoundation.utils.CacheUtils;

/**
 * Created by Dante on 09.01.2016 2:08.
 * For MyApplication.
 */
public class DownloadObjects extends AsyncTask<Void, Void, ArrayList<Article>> {
    private static final String LOG = DownloadObjects.class.getSimpleName();

    private String url;
    private UpdateArticlesList updateArticlesList;
    private Context ctx;

    public DownloadObjects(String url, UpdateArticlesList updateArticlesList, Context ctx) {
        this.url = url;
        this.updateArticlesList = updateArticlesList;
        this.ctx = ctx;
    }

    @Override
    protected ArrayList<Article> doInBackground(Void... params) {
        Log.d(LOG, "doInBackground started");
        return getAllArticles(url, ctx);
    }

    public static ArrayList<Article> getAllArticles(String urlToObjects, Context ctx) {
        ArrayList<Article> articles = new ArrayList<>();
        final OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlToObjects)
                .build();

        Response response;
        try {
            response = client.newCall(request).execute();

            Document doc = Jsoup.parse(response.body().string());
            Element pageContent = doc.getElementById("page-content");
            if (pageContent == null) {
                return null;
            }

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
                String imageURL = doc.getElementsByTag("img").first().attr("src");
                String url = Const.DOMAIN_NAME + doc.getElementsByTag("a").first().attr("href");
                String title = doc.text();
                Article article = new Article();
                article.setURL(url);
                article.setImageUrl(imageURL);
                article.setTitle(title);
                articles.add(article);
            }
            CacheUtils.writeObjectsToCache(ctx, articles, CacheUtils.getTypeByUrl(urlToObjects));
            return articles;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(final ArrayList<Article> result) {
        super.onPostExecute(result);
        this.updateArticlesList.update(result);
    }

    public interface UpdateArticlesList {
        void update(ArrayList<Article> articles);
    }
}