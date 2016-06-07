package ru.dante.scpfoundation.utils.prerate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import ru.dante.scpfoundation.R;

/**
 * Created by o.leonov on 06.10.2014.
 */
public class PreRate
{
    private WeakReference<Context> cntxRef;
    private static PreRate instance;
    private Dialog lastDialog;

    private String emailAddress;
    private String emailSubject;
    private String firstDialogText;
    private int titleColor;
    private int lineColor;

    private PreRate()
    {
    }
    public static PreRate init(Activity act, String feedbackEmailTo, String feedbackSubj)
    {
        if(instance==null)
        {
            instance=new PreRate();
            instance.titleColor=act.getResources().getColor(R.color.pre_rate_main_color);
            instance.lineColor=instance.titleColor;
            TimeSettings.setFirstStartTime(act);
        }
        instance.cntxRef=new WeakReference<Context>(act);

        instance.emailAddress=feedbackEmailTo;
        instance.emailSubject=feedbackSubj;
        instance.firstDialogText=act.getResources().getString(R.string.main_dialog_text);
        return instance;
    }
    public PreRate configureColors(int titleColor, int lineColor)
    {
        this.titleColor=titleColor;
        this.lineColor=lineColor;
        return this;
    }
    public PreRate configureText(String firstDialogText)
    {
        this.firstDialogText=firstDialogText;
        return this;
    }
    /*** Эта команда как раз и запускает диалог когда необходимо */
    public void showIfNeed()
    {
        //Показываем если прошло время и есть интернет(без интернета пользователь не может проголосовать)
        if(TimeSettings.needShowPreRateDialog(cntxRef.get())&&
                (lastDialog==null||!lastDialog.isShowing())&&
                isConnected(cntxRef.get()))
            showRateDialog();
    }
    /*** Вызвать в onDestroy */
    public static void clearDialogIfOpen()
    {
        if(instance!=null&&instance.lastDialog!=null&&
                instance.lastDialog.isShowing())
            instance.lastDialog.dismiss();
    }
    public void showRateDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(cntxRef.get());
        builder.setCancelable(false);

        builder.setTitle(cntxRef.get().getString(R.string.rate_app_title, getApplicationName(cntxRef.get())));
        builder.setMessage(firstDialogText);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Отображаем пред диалог
                        showPreStarsDialog();
                    }
                })
                .setNeutralButton(R.string.not_now,  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //У нас и так уже поставилось показать позже(при запуске диалога)
                        lastDialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.not_notify, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TimeSettings.setShowMode(cntxRef.get(), TimeSettings.NOT_SHOW);
                        lastDialog.dismiss();
                    }
                });
        lastDialog = builder.create();
        lastDialog.show();
        // Ставим флаг, что надо показать позже(если пользователь в самом диалоге не выберет другой вариант)
        TimeSettings.setShowMode(cntxRef.get(), TimeSettings.SHOW_LATER);
        TimeSettings.saveLastShowTime(cntxRef.get());
    }
    private void showPreStarsDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(cntxRef.get());
        builder.setCancelable(false);

        LayoutInflater inflater = LayoutInflater.from(cntxRef.get().getApplicationContext());
        View customView=inflater.inflate(R.layout.pre_rate_stars_dialog_1, null);

        builder.setTitle(getApplicationName(cntxRef.get()));

        TextView tvText=(TextView)customView.findViewById(R.id.tvText);
        tvText.setTypeface(Fonts.getLightFont(cntxRef.get()));

        final RatingBar rating_bar_0=(RatingBar)customView.findViewById(R.id.rating_bar_0);

        builder.setView(customView)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Отображаем пред диалог
                        if (rating_bar_0.getProgress() == 5) {
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
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        lastDialog.dismiss();
                    }
                });
        lastDialog = builder.create();
        lastDialog.show();
    }

    private void showFeedbackDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(cntxRef.get());
        builder.setCancelable(false);

        LayoutInflater inflater = LayoutInflater.from(cntxRef.get().getApplicationContext());
        View customView=inflater.inflate(R.layout.feedback_dialog_2, null);

        builder.setTitle(R.string.help_us);

        TextView tvText=(TextView)customView.findViewById(R.id.tvText);
        tvText.setTypeface(Fonts.getLightFont(cntxRef.get()));

        final EditText etEmailText=(EditText)customView.findViewById(R.id.etEmailText);
        etEmailText.setTypeface(Fonts.getLightFont(cntxRef.get()));

        builder.setView(customView)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String text = etEmailText.getText().toString();
                        if (!TextUtils.isEmpty(text)) {
                            //TODO доделать отправку данных и сообщение о том, что письмо отправлено
                            Intent intentEmail = new Intent(Intent.ACTION_SEND);
                            intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
                            intentEmail.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
                            intentEmail.putExtra(Intent.EXTRA_TEXT, text);
                            intentEmail.setType("message/rfc822");
                            cntxRef.get().startActivity(Intent.createChooser(intentEmail, cntxRef.get().getString(R.string.choose_email_provider)));
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        lastDialog.dismiss();
                    }
                });
        lastDialog = builder.create();
        lastDialog.show();
        //Если пользователь попал сюда, то ему что-то не понравилось, больше не показываем диалог
        TimeSettings.setShowMode(cntxRef.get(), TimeSettings.NOT_SHOW);
    }

    private static String appName;
    public static String getApplicationName(Context context) {
        if(appName==null) {
            int stringId = context.getApplicationInfo().labelRes;
            appName = context.getString(stringId);
        }
        return appName;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
