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
 * Created by Dante on 09.01.2016 22:00.
 * For MyApplication.
 */
public class DownloadAllArticlesInfo extends AsyncTask<Void, Void, ArrayList<Article>> {
    private static final String LOG = DownloadAllArticlesInfo.class.getSimpleName();
    private static String writenedFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/authorsRUtranslate.txt";
    private int pageNumber;
    private Context ctx;

    public DownloadAllArticlesInfo(int pageNumber, Context ctx) {
        this.pageNumber = pageNumber;
        this.ctx = ctx;
    }

    public static Integer getNumOfPages() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://scpfoundation.ru/most-recently-created/p/" + 1)
                .build();

        try {
            Response response = client.newCall(request).execute();
            Document doc = Jsoup.parse(response.body().string());
            Element spanWithNumber = doc.getElementsByClass("pager-no").first();
            String text = spanWithNumber.text();
            Integer numOfPages = Integer.valueOf(text.substring(text.lastIndexOf(" ") + 1));
            Log.i(LOG, "numOfPages: " + numOfPages);
            return numOfPages;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Article> getArticlesByPage(int pageNumber) {
        ArrayList<Article> articles = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://scpfoundation.ru/most-recently-created/p/" + pageNumber)
                .build();

        try {
            Response response = client.newCall(request).execute();
            Document doc = Jsoup.parse(response.body().string());
            Element pageContent = doc.getElementsByClass("wiki-content-table").first();
            if (pageContent == null) {
                return null;
            }

            ArrayList<Element> listOfTR = pageContent.getElementsByTag("tr");
            for (int i = 1; i < listOfTR.size(); i++) {
                ArrayList<Element> listOfTd = listOfTR.get(i).getElementsByTag("td");
                Element firstTd = listOfTd.get(0);
                Element tagA = firstTd.getElementsByTag("a").first();
                String title = tagA.text();
                String url = Const.DOMAIN_NAME + tagA.attr("href");
                String authorName = listOfTR.get(i).getElementsByAttributeValueContaining("class", "printuser").first().text();
                Article article = new Article();
                article.setAuthorName(authorName);
                article.setTitle(title);
                article.setURL(url);
                articles.add(article);
            }
            return articles;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected ArrayList<Article> doInBackground(Void... params) {
        Log.d(LOG, "doInBackground started: " + pageNumber);

        //clear file data if it's first page
        if (pageNumber == 1) {
            try {
                File file = new File(writenedFile);
                if (!file.createNewFile()) {
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(writenedFile, false), "UTF8"));
                    bw.write("");
                    bw.close();
                }
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }

        ArrayList<Article> articles = getArticlesByPage(pageNumber);

        if (articles != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < articles.size(); i++) {
                stringBuilder.append("<item>");
                stringBuilder.append(articles.get(i).getURL());
                stringBuilder.append(Const.DIVIDER);
                stringBuilder.append(articles.get(i).getAuthorName());
                stringBuilder.append("</item>");
            }
            writeToFile(stringBuilder.toString());
        }

        return articles;
    }

    @Override
    protected void onPostExecute(final ArrayList<Article> result) {
        super.onPostExecute(result);

        if (result == null) {
            Log.e(LOG, "Connection lost");
        } else {
            if (result.size() == Const.DEFAULT_NUM_OF_ARTICLE_OF_PAGE) {
                pageNumber++;
                DownloadAllArticlesInfo downloadAllArticlesInfo = new DownloadAllArticlesInfo(pageNumber, ctx);
                downloadAllArticlesInfo.execute();
            }
        }
    }

    private void writeToFile(String data) {
        try {
            Log.i(LOG, writenedFile);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(writenedFile, true), "UTF8"));
            bw.append(data);
            bw.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}