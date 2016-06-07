package ru.dante.scpfoundation.utils.parsing;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.dante.scpfoundation.Article;
import ru.dante.scpfoundation.Const;

/**
 * Created by Dante on 09.01.2016.
 */
public class DownloadAllArticlesAuthorRUInfo extends AsyncTask<Void, Void, ArrayList<Article>>
{
    private static final String LOG = DownloadAllArticlesAuthorRUInfo.class.getSimpleName();
    private Context ctx;

    public static final String DOMAIN_NAME = "http://scpfoundation.ru";

    public DownloadAllArticlesAuthorRUInfo(Context ctx)
    {
        this.ctx = ctx;
    }

    @Override
    protected ArrayList<Article> doInBackground(Void... params)
    {

        Log.d(LOG, "doInBackground started: ");
        ArrayList<Article> articles = new ArrayList<>();

        final OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url("http://scpfoundation.ru/licensing-ru")
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

            pageContent.getElementsByTag("table").first().remove();

            for (Element p : pageContent.getElementsByTag("p"))
            {
                String pText = p.html();
                Log.i(LOG, pText);
                String[] articlesInfo = pText.split("<br>");
                for (String artileInfo : articlesInfo)
                {
                    Article article = new Article();
                    String title;
                    String author = "";
                    Element currentArticleHtml = Jsoup.parse(artileInfo);
                    String url = currentArticleHtml.getElementsByTag("a").first().attr("href");
                    if (currentArticleHtml.getElementsByTag("span").size() != 0)
                    {
                        if (currentArticleHtml.getElementsByTag("span").size() > 1)
                        {
                            for (Element authorSpan : currentArticleHtml.getElementsByTag("span"))
                            {
                                author += authorSpan.text() + ", ";
                            }
                            author = author.substring(0, author.length() - 2);
                        } else
                        {
                            author = currentArticleHtml.getElementsByTag("span").first().text();
                        }
                        title = artileInfo.substring(artileInfo.indexOf(" - ") + 3, artileInfo.indexOf(" ("));
                    } else
                    {
                        if (artileInfo.contains(" (автор:"))
                        {
                            title = artileInfo.substring(artileInfo.indexOf(" - ") + 3, artileInfo.indexOf(" (автор:"));
                            Log.i(LOG, title);
                            author = artileInfo.substring(artileInfo.indexOf(" (автор:") + 8, artileInfo.lastIndexOf(")"));
                            Log.i(LOG, author);
                        } else if (artileInfo.contains(" (автор: "))
                        {
                            title = artileInfo.substring(artileInfo.indexOf(" - ") + 3, artileInfo.indexOf(" (автор: "));
                            Log.i(LOG, title);
                            author = artileInfo.substring(artileInfo.indexOf(" (автор: ") + 9, artileInfo.lastIndexOf(")"));
                            Log.i(LOG, author);
                        } else if (artileInfo.contains(" (авторы: "))
                        {
                            title = artileInfo.substring(artileInfo.indexOf(" - ") + 3, artileInfo.indexOf(" (авторы: "));
                            Log.i(LOG, title);
                            author = artileInfo.substring(artileInfo.indexOf(" (авторы: ") + 10, artileInfo.lastIndexOf(")"));
                            Log.i(LOG, author);
                        } else if (artileInfo.contains(" ( автор: "))
                        {
                            title = artileInfo.substring(artileInfo.indexOf(" - ") + 3, artileInfo.indexOf(" ( автор: "));
                            Log.i(LOG, title);
                            author = artileInfo.substring(artileInfo.indexOf(" ( автор: ") + 10, artileInfo.lastIndexOf(")"));
                            Log.i(LOG, author);
                        } else
                        {
                            title = artileInfo.substring(artileInfo.indexOf(" - ") + 3, artileInfo.indexOf(" (автор "));
                            Log.i(LOG, title);
                            author = artileInfo.substring(artileInfo.indexOf(" (автор ") + 8, artileInfo.lastIndexOf(")"));
                            Log.i(LOG, author);
                        }
                    }
                    article.setTitle(title);
                    article.setAuthorName(author);
                    article.setURL(url);
                    articles.add(article);
                }
            }


            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < articles.size(); i++)
            {
                stringBuilder.append("<item>");
                stringBuilder.append(articles.get(i).getURL());
                stringBuilder.append(Const.DIVIDER);
                stringBuilder.append(articles.get(i).getTitle());
                stringBuilder.append(Const.DIVIDER);
                stringBuilder.append(articles.get(i).getAuthorName());
                stringBuilder.append("</item>\n");
            }
            writeToFile(stringBuilder.toString());
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

        } else
        {

        }

    }

    private void writeToFile(/*Context ctx,*/ String data)
    {
        try
        {
//            OutputStreamWriter outputStreamWriter =
//                    new OutputStreamWriter(
//                            ctx.openFileOutput(Environment.DIRECTORY_DOWNLOADS + "authorsRUtranslate.txt", Context.MODE_APPEND));
//            outputStreamWriter.write(data);
//            outputStreamWriter.close();
            String writenedFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/authorsRU.txt";
            File file = new File(writenedFile);
            file.createNewFile();
            Log.i(LOG, writenedFile);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(writenedFile, true), "UTF8"));
            bw.append(data);
            bw.close();
        } catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}