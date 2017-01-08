package ru.kuchanov.scp2.util;

import android.content.Intent;
import android.net.Uri;

import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;

/**
 * Created by mohax on 08.01.2017.
 * <p>
 * for scp_ru
 */
public class IntentUtils {
    public static void shareUrl(String url) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_SEND);
        String fullMessage = MyApplication.getAppInstance().getString(
                R.string.share_link_text,
                url,
                MyApplication.getAppInstance().getPackageName()
        );
        intent.putExtra(Intent.EXTRA_TEXT, fullMessage);
        intent.setType("text/plain");
        MyApplication.getAppInstance().startActivity(
                Intent.createChooser(intent, MyApplication.getAppInstance().getResources().getText(R.string.share_choser_text))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        );
    }

    public static void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getAppInstance().startActivity(
                Intent.createChooser(intent, MyApplication.getAppInstance().getResources().getText(R.string.browser_choser_text))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        );
    }
}