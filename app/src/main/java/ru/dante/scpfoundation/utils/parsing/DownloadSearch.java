package ru.dante.scpfoundation.utils.parsing;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.dante.scpfoundation.Article;
import ru.dante.scpfoundation.R;

/**
 * Created by Dante on 09.01.2016.
 */
public class DownloadSearch extends AsyncTask<Void, Void, ArrayList<Article>>
{
    private static final String LOG = DownloadSearch.class.getSimpleName();
    private Context ctx;

    String url = "http://scpfoundation.ru/search:site/a/p/q/";
    UpdateArticlesList updateArticlesList;
    private int page=1;


    public DownloadSearch(String searchQuery, Context ctx, UpdateArticlesList updateArticlesList,int page)
    {
        this.ctx = ctx;
        this.page=page;
        this.url += searchQuery.replaceAll(" ", "%20")+"/p/"+page;
        this.updateArticlesList = updateArticlesList;
    }

    @Override
    protected ArrayList<Article> doInBackground(Void... params)
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
            Element searchResults = pageContent.getElementsByClass("search-results").first();
            Elements items = searchResults.children();
            if (items.size() == 0)
            {
                Article article = new Article();
                article.setTitle(ctx.getString(R.string.no_search_results));
//                article.setPreview("");
                articles.add(article);
            } else
            {
                for (Element item : items)
                {
                    Element titleA=item.getElementsByClass("title").first().getElementsByTag("a").first();
                    String title=titleA.html();
                    String url=titleA.attr("href");
                    Element previewDiv=item.getElementsByClass("preview").first();
                    String preview=previewDiv.html();
                    Article article=new Article();
                    article.setTitle(title);
                    article.setURL(url);
                    article.setPreview(preview);
                    articles.add(article);
                }
            }
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
            this.updateArticlesList.update(result,page);
        } else
        {
            this.updateArticlesList.update(result,page);
        }

    }

    public interface UpdateArticlesList
    {
        public void update(ArrayList<Article> articles,int page);
    }
}