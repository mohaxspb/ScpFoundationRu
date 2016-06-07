package ru.dante.scpfoundation.utils.parsing;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
public class DownloadAllArticlesAuthorOriginalInfo extends AsyncTask<Void, Void, ArrayList<Article>>
{
    private static final String LOG = DownloadAllArticlesAuthorOriginalInfo.class.getSimpleName();
    private Context ctx;

    public static final String DOMAIN_NAME = "http://scpfoundation.ru";

    public DownloadAllArticlesAuthorOriginalInfo(Context ctx)
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
                .url("http://scpfoundation.ru/licensing")
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
//                String authorName = listOfTR.get(i).getElementsByAttributeValueContaining("class", "printuser").first().text();

            Element mainDiv = pageContent.getElementsByClass("yui-content").first();
            for (Element innerDiv : mainDiv.children())
            {
                if (innerDiv.getElementsByTag("blockquote").first() != null)
                {
                    innerDiv.getElementsByTag("blockquote").first().remove();

                }

                Elements pWithA = innerDiv.select("p:has(a)").remove();

                for (Element p : innerDiv.getElementsByTag("p"))
                {
                    String pText = p.html();
                    Log.i(LOG, pText);
                    String[] articlesInfo = pText.split("<br>");
                    for (String artileInfo : articlesInfo)
                    {
                        Article article = new Article();
                        String title;
                        String author;
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
                        article.setTitle(title.replaceAll("(\\&nbsp\\;)", " "));
                        article.setAuthorName(author);
                        articles.add(article);
                    }
                }
            }


            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < articles.size(); i++)
            {
                stringBuilder.append("<item>");
                stringBuilder.append(articles.get(i).getTitle());
                stringBuilder.append(Const.DIVIDER);
                stringBuilder.append(articles.get(i).getAuthorName());
                stringBuilder.append("</item>");
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
            String writenedFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/authorsENG.txt";
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