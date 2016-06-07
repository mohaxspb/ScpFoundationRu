package ru.dante.scpfoundation.utils.parsing;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.dante.scpfoundation.utils.SetTextViewHTML;

/**
 * Created by Dante on 09.01.2016.
 */
public class Download extends AsyncTask<Void, Void, String> {
    private static final String LOG=Download.class.getSimpleName();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(Void... params) {

        Log.d(LOG, "doInBackground started");
        final OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url("http://scpfoundation.ru/news")
//                .url("http://www.scp-wiki.net/news")
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
//            System.out.println(response.body().string());
            Document doc = Jsoup.parse(response.body().string());
            org.jsoup.nodes.Element pageContent = doc.getElementById("page-content");
            if (pageContent==null){
                return null;
            }
            String cuttedPageContent = pageContent.toString();
            int firsthrelementposition = cuttedPageContent.indexOf("<hr>");
            cuttedPageContent = cuttedPageContent.substring(0, firsthrelementposition);
            cuttedPageContent += "</div>";
//            System.out.println(cuttedPageContent);
            return cuttedPageContent;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    TextView textView;

    public Download(TextView textView) {
        this.textView = textView;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if(result==null){
            Snackbar.make(textView,"Connection lost", Snackbar.LENGTH_LONG).show();
        }else {
            textView.setLinksClickable(true);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
//            textView.setText(Html.fromHtml(result,new UILImageGetter(textView,textView.getContext()),null));
            new SetTextViewHTML(textView.getContext()).setText(textView, result);

        }

    }
}