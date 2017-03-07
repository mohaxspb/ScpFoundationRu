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
import ru.dante.scpfoundation.Const;

/**
 * Created by Dante on 09.01.2016.
 *
 * for scp_ru
 */
public class DownloadImg extends AsyncTask<Void, Void, ArrayList<String>> {
    private SetImagInfo setImagInfo;

    public interface SetImagInfo {
        void setImgInfo(ArrayList<String> description);
    }

    private static final String LOG = DownloadImg.class.getSimpleName();

    public static final String DOMAIN_NAME = "http://artscp.com";
    private String urlCalendar = "http://artscp.com/en/calendar";
    private String urlArt = "http://artscp.com/en/artbook";

    public DownloadImg(SetImagInfo imgInfo, Context ctx) {
        this.setImagInfo = imgInfo;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        Log.d(LOG, "doInBackground started: ");
        ArrayList<String> imgs = new ArrayList<>();

        try {
            final OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(urlCalendar)
                    .build();

            Response response;
            response = client.newCall(request).execute();
            Document doc = Jsoup.parse(response.body().string());
            Element gallery = doc.getElementById("sigProId5e2e1e130d");
            for (Element li : gallery.children()) {
                Element a = li.getElementsByTag("a").first();
                if (a == null) {
                    continue;
                }
                if (a.attr("title").isEmpty()) {
                    continue;
                }
                String imgUrl = DOMAIN_NAME + a.attr("href");
                String description = a.attr("title");
                imgs.add(imgUrl + Const.DIVIDER + description);
            }
            request = new Request.Builder()
                    .url(urlArt)
                    .build();

            response = client.newCall(request).execute();
            doc = Jsoup.parse(response.body().string());
            gallery = doc.getElementById("sigProIdff703f164a");
            for (Element li : gallery.children()) {
                Element a = li.getElementsByTag("a").first();
                if (a == null) {
                    continue;
                }
                if (a.attr("title").isEmpty()) {
                    continue;
                }
                String imgUrl = DOMAIN_NAME + a.attr("href");
                String description = a.attr("title");
                imgs.add(imgUrl + Const.DIVIDER + description);
            }
            return imgs;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(final ArrayList<String> result) {
        super.onPostExecute(result);

        if (result == null) {
            Log.e(LOG, "Connection lost");

        } else {
            setImagInfo.setImgInfo(result);
        }
    }
}