package ru.kuchanov.rateapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;

import java.lang.ref.WeakReference;

import io.techery.properratingbar.ProperRatingBar;

public class PreRate {

    private static String appName;

    private WeakReference<Context> cntxRef;
    private static PreRate instance;
    private MaterialDialog lastDialog;

    private String emailAddress;
    private String emailSubject;
    private String firstDialogText;
    private int titleColor;
    @SuppressLint("unused")
    private int lineColor;

    private PreRate() {
    }

    public static PreRate init(Activity act, String feedbackEmailTo, String feedbackSubj) {
        if (instance == null) {
            instance = new PreRate();
            instance.titleColor = ContextCompat.getColor(act, R.color.pre_rate_main_color);
            instance.lineColor = instance.titleColor;
            TimeSettings.setFirstStartTime(act);
        }
        instance.cntxRef = new WeakReference<>(act);

        instance.emailAddress = feedbackEmailTo;
        instance.emailSubject = feedbackSubj;
        instance.firstDialogText = act.getResources().getString(R.string.main_dialog_text);
        return instance;
    }

    @SuppressLint("unused")
    public PreRate configureColors(int titleColor, int lineColor) {
        this.titleColor = titleColor;
        this.lineColor = lineColor;
        return this;
    }

    @SuppressLint("unused")
    public PreRate configureText(String firstDialogText) {
        this.firstDialogText = firstDialogText;
        return this;
    }

    /***
     * Эта команда как раз и запускает диалог когда необходимо
     */
    public void showIfNeed() {
        //Показываем если прошло время и есть интернет(без интернета пользователь не может проголосовать)
        if (TimeSettings.needShowPreRateDialog(cntxRef.get()) &&
                (lastDialog == null || !lastDialog.isShowing()) &&
                isConnected(cntxRef.get()))
            showRateDialog();
    }

    /***
     * Вызвать в onDestroy
     */
    public static void clearDialogIfOpen() {
        if (instance != null && instance.lastDialog != null &&
                instance.lastDialog.isShowing())
            instance.lastDialog.dismiss();
    }

    private void showRateDialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(cntxRef.get())
                .cancelable(false)
                .content(firstDialogText)
                .title(cntxRef.get().getString(R.string.rate_app_title, getApplicationName(cntxRef.get())))
                .positiveText(R.string.yes)
                .onPositive((dialog, which) -> showPreStarsDialog())
                .neutralText(R.string.not_now)
                .onNeutral((dialog, which) -> lastDialog.dismiss())
                .negativeText(R.string.not_notify)
                .onNegative((dialog, which) -> {
                    TimeSettings.setShowMode(cntxRef.get(), TimeSettings.NOT_SHOW);
                    lastDialog.dismiss();
                });

        lastDialog = builder.build();
        lastDialog.show();
        // Ставим флаг, что надо показать позже(если пользователь в самом диалоге не выберет другой вариант)
        TimeSettings.setShowMode(cntxRef.get(), TimeSettings.SHOW_LATER);
        TimeSettings.saveLastShowTime(cntxRef.get());
    }

    private void showPreStarsDialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(cntxRef.get())
                .cancelable(false)
                .title(getApplicationName(cntxRef.get()));

        LayoutInflater inflater = LayoutInflater.from(cntxRef.get().getApplicationContext());
        @SuppressLint("InflateParams")
        View customView = inflater.inflate(R.layout.pre_rate_stars_dialog, null, false);

        final ProperRatingBar rating_bar_0 = (ProperRatingBar) customView.findViewById(R.id.rating_bar_0);

        builder.customView(customView, false)
                .positiveText(R.string.yes)
                .onPositive((dialog, which) -> {
                    //Отображаем пред диалог
                    if (rating_bar_0.getRating() == 5) {
                        //Отправляем пользователя на Google Play и помечаем, что больше не надо показывать
                        TimeSettings.setShowMode(cntxRef.get(), TimeSettings.NOT_SHOW);
                        final String appPackageName = cntxRef.get().getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            cntxRef.get().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            //Для случая запуска на симуляторе без Google Play
                            cntxRef.get().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    } else {
                        showFeedbackDialog();
                    }
                })
                .negativeText(android.R.string.cancel)
                .onNegative((dialog, which) -> lastDialog.dismiss());
        lastDialog = builder.build();
        lastDialog.show();
    }

    private void showFeedbackDialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(cntxRef.get())
                .cancelable(false)
                .title(R.string.help_us);

        LayoutInflater inflater = LayoutInflater.from(cntxRef.get().getApplicationContext());
        @SuppressLint("InflateParams")
        View customView = inflater.inflate(R.layout.pre_rate_feedback_dialog, null, false);

        final EditText etEmailText = (EditText) customView.findViewById(R.id.etMessage);

        builder.customView(customView, false)
                .positiveText(R.string.yes)
                .onPositive((dialog, which) -> {
                    String text = etEmailText.getText().toString();
                    if (!TextUtils.isEmpty(text)) {
                        //TODO доделать отправку данных и сообщение о том, что письмо отправлено
                        Intent intentEmail = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + emailAddress));
                        intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
                        intentEmail.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
                        intentEmail.putExtra(Intent.EXTRA_TEXT, text);
                        cntxRef.get().startActivity(Intent.createChooser(intentEmail, cntxRef.get().getString(R.string.choose_email_provider)));
                    }
                })
                .negativeText(android.R.string.cancel)
                .onNegative((dialog, which) -> lastDialog.dismiss());

        lastDialog = builder.build();
        lastDialog.show();
        //Если пользователь попал сюда, то ему что-то не понравилось, больше не показываем диалог
        TimeSettings.setShowMode(cntxRef.get(), TimeSettings.NOT_SHOW);
    }

    private static String getApplicationName(Context context) {
        if (appName == null) {
            int stringId = context.getApplicationInfo().labelRes;
            appName = context.getString(stringId);
        }
        return appName;
    }

    private static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}