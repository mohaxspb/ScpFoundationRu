package ru.dante.scpfoundation.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.dante.scpfoundation.R;

/**
 * Created for MyApplication by Dante on 10.04.2016  1:59.
 */
public class RandomPage {
    private static final String TAG = RandomPage.class.getSimpleName();

    public static void getRandomPage(final Context ctx) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        if (!pref.contains(ctx.getString(R.string.pref_key_random_url))) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        makeRequest(ctx);
                    } catch (IOException e) {
                        Log.e(TAG, "error make request", e);
                        final AppCompatActivity activity = (AppCompatActivity) ctx;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "Не удалось получить случайную статью", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            };
            thread.start();
        }
    }

    private static void makeRequest(Context ctx) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        String url = "https://beta.scpfoundation.net/wikidot_random_page";

        Request.Builder request = new Request.Builder();
        request.url(url);
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.addHeader("Accept-Encoding", "gzip, deflate, br");
        request.addHeader("Accept-Language", "en-US,en;q=0.8,de-DE;q=0.5,de;q=0.3");
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0");
        request.get();

        Response response = client.newCall(request.build()).execute();

        Request requestResult = response.request();
        Log.d(TAG, "requestResult:" + requestResult);
        Log.d(TAG, "requestResult.url().url():" + requestResult.url().url());

        String randomURL = requestResult.url().url().toString();
        Log.d(TAG, "randomUrl = " + randomURL);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        pref.edit().putString(ctx.getString(R.string.pref_key_random_url), randomURL).apply();
    }
}