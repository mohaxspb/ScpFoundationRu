package ru.dante.scpfoundation.utils.parsing;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.dante.scpfoundation.Article;

/**
 * Created by Dante on 09.01.2016.
 * <p>
 * for scp_ru
 */
public class DownloadRateArticles extends AsyncTask<Void, Void, ArrayList<Article>> {
    private static final String LOG = DownloadRateArticles.class.getSimpleName();
    int pageNumber;
    public static final String DOMAIN_NAME = "http://scpfoundation.ru";
    DownloadNewArticles.UpdateArticlesList updateArticlesList;
    Context ctx;


    public DownloadRateArticles(int pageNumber, DownloadNewArticles.UpdateArticlesList updateArticlesList, Context ctx) {
        this.pageNumber = pageNumber;
        this.updateArticlesList = updateArticlesList;
        this.ctx = ctx;
    }

    @Override
    protected ArrayList<Article> doInBackground(Void... params) {
        Log.d(LOG, "doInBackground started");
        ArrayList<Article> articles = new ArrayList<>();
        final OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://scpfoundation.ru/top-rated-pages/p/" + pageNumber)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
//            System.out.println(response.body().string());
            Document doc = Jsoup.parse(response.body().string());
            Element pageContent = doc.getElementsByClass("list-pages-box").first();
            if (pageContent == null) {
                return null;
            }

            ArrayList<Element> listOfElements = pageContent.getElementsByClass("list-pages-item");
            Log.d(LOG, "listOfElements size: " + listOfElements.size());
            for (int i = 0; i < listOfElements.size(); i++) {
                Element tagP = listOfElements.get(i).getElementsByTag("p").first();
                Element tagA = tagP.getElementsByTag("a").first();
                String title = tagP.text();
                String url = DOMAIN_NAME + tagA.attr("href");
                Article article = new Article();
                article.setTitle(title);
                article.setURL(url);
                articles.add(article);
            }

//            for (int i = 0; i < articles.size(); i++) {
//                Log.d(LOG, articles.get(i).getTitle());
//            }
            return articles;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(final ArrayList<Article> result) {
        super.onPostExecute(result);

        if (result == null) {
            Log.e(LOG, "Connection lost");
            updateArticlesList.update(null);
        } else {
            updateArticlesList.update(result);
        }
    }
}