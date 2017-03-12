package ru.dante.scpfoundation.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;

import java.util.List;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;

/**
 * Created by mohax on 08.01.2017.
 * <p>
 * for scp_ru
 */
public class IntentUtils {

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 987;

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

    public static void shareViewWithText(AppCompatActivity activity, String text, View viewToShare) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Bitmap bitmap = Bitmap.createBitmap(viewToShare.getWidth(), viewToShare.getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            viewToShare.draw(canvas);

            String pathofBmp = MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, "Вжух и " + text, null);
            Uri bmpUri = Uri.parse(pathofBmp);
            final Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            String appPackageName = activity.getPackageName();
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Отправлено из приложения \"Вжух!\"\n"
                            + "https://play.google.com/store/apps/details?id=" + appPackageName);
            shareIntent.setType("image/png");

            activity.startActivity(shareIntent);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    public static void firebaseInvite(FragmentActivity activity) {
        Intent intent = new AppInviteInvitation.IntentBuilder(activity.getString(R.string.invitation_title))
                .setMessage(activity.getString(R.string.invitation_message))
                .setCallToActionText(activity.getString(R.string.invitation_cta))
                .build();
        activity.startActivityForResult(intent, Constants.Firebase.REQUEST_INVITE);
    }

    public static void tryOpenPlayMarket(Context context) {
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.market_url, context.getPackageName())));
        marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        checkAndStart(context, marketIntent, R.string.start_market_error);
    }

    public static void tryOpenPlayMarket(Context context, String appId) {
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.market_url, appId)));
        marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        checkAndStart(context, marketIntent, R.string.start_market_error);
    }

    private static boolean checkIntent(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        return activities != null && activities.size() > 0;
    }

    private static void checkAndStart(Context context, Intent intent, int errorRes) {
        if (checkIntent(context, intent)) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, context.getString(errorRes), Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isPackageInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent == null) {
            return false;
        }
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}