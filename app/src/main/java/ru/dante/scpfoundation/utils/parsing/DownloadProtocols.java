package ru.dante.scpfoundation.utils.parsing;

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
 */
public class DownloadProtocols extends AsyncTask<Void, Void, ArrayList<String>>
{
    private String url;

    public interface UpdateProtocol
    {
        void update(ArrayList<String> protocols);
    }

    private static final String LOG = DownloadProtocols.class.getSimpleName();
    //    RecyclerView recyclerView;
    UpdateProtocol updateProtocol;


    public DownloadProtocols(String url/*, RecyclerView recyclerView*/, UpdateProtocol updateProtocol)
    {
        this.url = url;
//        this.recyclerView = recyclerView;
        this.updateProtocol = updateProtocol;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params)
    {
        Log.d(LOG, "doInBackground started");
        ArrayList<String> articles = new ArrayList<>();

        final OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();


        try
        {
            Response response;
            response = client.newCall(request).execute();
//            System.out.println(response.body().string());
            Document doc = Jsoup.parse(response.body().string());
            Element pageContent = doc.getElementById("page-content");
            if (pageContent == null)
            {
                return null;
            }

            ArrayList<Element> listOfElements = pageContent.getElementsByTag("ul");
            for (int i = 0; i < listOfElements.size(); i++)
            {
                ArrayList<Element> listOfLi = listOfElements.get(i).getElementsByTag("li");
                for (int u = 0; u < listOfLi.size(); u++)
                {
                    String url = listOfLi.get(u).getElementsByTag("a").first().attr("href");
                    if (!url.startsWith("http"))
                    {
                        url = Const.DOMAIN_NAME + url;
                    }
                    String text = listOfLi.get(u).text();
                    articles.add(url + "BBPE" + text);
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
    protected void onPostExecute(final ArrayList<String> result)
    {
        super.onPostExecute(result);

//        if (result == null)
//        {
////            Log.e(LOG, "Connection lost");
////            Snackbar.make(recyclerView, "Connection lost", Snackbar.LENGTH_LONG).show();
//            updateProtocol.update(null);
//        } else
//        {
//
////            ArrayList<String> urls = new ArrayList<>();
////            ArrayList<String> titles = new ArrayList<>();
////            for (String resultItem : result)
////            {
////                String[] urlAndTitle = resultItem.split("BBPE");
////                urls.add(urlAndTitle[0]);
////                titles.add(urlAndTitle[1]);
////            }
////            recyclerView.setAdapter(new RecyclerAdapterProtocols(result));
//            updateProtocol.update(result);
//        }
        updateProtocol.update(result);
    }
}