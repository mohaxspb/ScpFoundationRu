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
import ru.dante.scpfoundation.utils.licensingfuckup.LicenseUtils;

/**
 * Created by Dante on 09.01.2016.
 */
public class DownloadNewArticles extends AsyncTask<Void, Void, ArrayList<Article>>
{
    private static final String LOG = DownloadNewArticles.class.getSimpleName();
    int pageNumber;
//    RecyclerView recyclerView;
    public static final String DOMAIN_NAME = "http://scpfoundation.ru";
    UpdateArticlesList updateArticlesList;
    Context ctx;

    public interface UpdateArticlesList
    {
        public void update(ArrayList<Article> listArticles);
    }


    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

    }

    public DownloadNewArticles(int pageNumber/* RecyclerView recyclerView*/, UpdateArticlesList updateArticlesList,Context ctx)
    {
        this.pageNumber = pageNumber;
//        this.recyclerView = recyclerView;
        this.updateArticlesList = updateArticlesList;
        this.ctx=ctx;
    }

    @Override
    protected ArrayList<Article> doInBackground(Void... params)
    {

        Log.d(LOG, "doInBackground started");
        ArrayList<Article> articles = new ArrayList<>();

        final OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url("http://scpfoundation.ru/most-recently-created/p/" + pageNumber)
//                .url("http://www.scp-wiki.net/most-recently-created/p/"+pageNumber)
                .build();

        Response response = null;
        try
        {
            response = client.newCall(request).execute();
//            System.out.println(response.body().string());
            Document doc = Jsoup.parse(response.body().string());
            org.jsoup.nodes.Element pageContent = doc.getElementsByClass("wiki-content-table").first();
            if (pageContent == null)
            {
                return null;
            }

            ArrayList<Element> listOfElements = pageContent.getElementsByTag("tr");
            for (int i = 1; i < listOfElements.size(); i++)
            {
                ArrayList<Element> listOfTd = listOfElements.get(i).getElementsByTag("td");
                Element firstTd = listOfTd.get(0);
                Element tagA = firstTd.getElementsByTag("a").first();
                String title = tagA.text();
                String url = DOMAIN_NAME + tagA.attr("href");
                String authorName = listOfElements.get(i).getElementsByAttributeValueContaining("class", "printuser").first().text();
                Article article = new Article();
                article.setTitle(title);
                article.setURL(url);
                article.setAuthorName(authorName);
                articles.add(article);
            }
            LicenseUtils.addTranslateAuthors(ctx, articles);

//            for (int i = 0; i < listOfArts.size(); i++) {
//                Log.d(LOG, listOfArts.get(i));
//            }
            return articles;
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(final ArrayList<Article> result)
    {
        super.onPostExecute(result);

        if (result == null)
        {
            Log.e(LOG, "Connection lost");
//            if (recyclerView != null)
//            {
//                Snackbar.make(recyclerView, "Connection lost", Snackbar.LENGTH_LONG).show();
//            }
            updateArticlesList.update(null);
        } else
        {
            updateArticlesList.update(result);
        }

    }
}