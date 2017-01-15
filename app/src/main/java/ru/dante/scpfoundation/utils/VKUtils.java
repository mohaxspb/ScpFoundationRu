package ru.dante.scpfoundation.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.fragments.FragmentDialogShowSubscription;

/**
 * Created for My Application by Dante on 28.02.2016  21:14.
 */
public class VKUtils
{
    public static void showLoginDialog(final Context ctx, String messege)
    {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(ctx)
                .customView(R.layout.unloged_dialog, true)
                .title(R.string.unloged_dialog_title)
                .positiveText("Закрыть");
        final MaterialDialog dialog = builder.build();

        if (dialog.getCustomView() != null)
        {
            View login = dialog.getCustomView().findViewById(R.id.login);
            login.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dialog.dismiss();
                    VKSdk.login((AppCompatActivity) ctx);
                }
            });
            TextView info = (TextView) dialog.getCustomView().findViewById(R.id.info);
            info.setText(messege);
        }

        dialog.show();
    }

    public static void checkVKAuth(final AppCompatActivity activity, final NavigationView navigationView)
    {

        if (VKSdk.isLoggedIn())
        {
            /*Удаление банера*/
           /* if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP)
            {
                View banner = activity.findViewById(R.id.adView);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) banner.getLayoutParams();
                params.height = 0;
                banner.setLayoutParams(params);
            }*/
            for (int i = 0; i < navigationView.getHeaderCount(); i++)
            {
                navigationView.removeHeaderView(navigationView.getHeaderView(i));
            }
            View headerlogined = LayoutInflater.from(activity).inflate(R.layout.drawer_header_logined, navigationView, false);
            navigationView.addHeaderView(headerlogined);
            final TextView name = (TextView) headerlogined.findViewById(R.id.vk_name);
            final ImageView avatar = (ImageView) headerlogined.findViewById(R.id.vk_avatar);
            VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_200")).executeWithListener(new VKRequest.VKRequestListener()
            {
                @Override
                public void onComplete(VKResponse response)
                {
                    VKApiUser user = ((VKList<VKApiUser>) response.parsedModel).get(0);
                    Log.d("User name", user.first_name + " " + user.last_name);
                    name.setText(user.first_name + " " + user.last_name);
                    MyUIL.get(activity).displayImage(user.photo_200, avatar, MyUIL.getRoundVKAvatarOptions(activity));
                    SharedPreferences prefVK = activity.getSharedPreferences(activity.getString(R.string.pref_vk), Context.MODE_PRIVATE);
                    prefVK.edit().putString(activity.getString(R.string.pref_vk_name), user.first_name).commit();
                    prefVK.edit().putString(activity.getString(R.string.pref_vk_surname), user.last_name).commit();
                    prefVK.edit().putString(activity.getString(R.string.pref_vk_avatar), user.photo_200).commit();
                }

                @Override
                public void onError(VKError error)
                {
                    super.onError(error);
                    SharedPreferences prefVK = activity.getSharedPreferences(activity.getString(R.string.pref_vk), Context.MODE_PRIVATE);
                    String first_name = prefVK.getString(activity.getString(R.string.pref_vk_name), "");
                    String last_name = prefVK.getString(activity.getString(R.string.pref_vk_surname), "");
                    String avatarUrl = prefVK.getString(activity.getString(R.string.pref_vk_avatar), "");
                    name.setText(first_name + " " + last_name);
                    MyUIL.get(activity).displayImage(avatarUrl, avatar, MyUIL.getRoundVKAvatarOptions(activity));
                }
            });

            ImageView logout = (ImageView) headerlogined.findViewById(R.id.logout);
            logout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    VKSdk.logout();
                    checkVKAuth(activity, navigationView);
                }
            });
        } else
        {
            /*View banner = activity.findViewById(R.id.adView);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) banner.getLayoutParams();
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;*/
            for (int i = 0; i < navigationView.getHeaderCount(); i++)
            {
                navigationView.removeHeaderView(navigationView.getHeaderView(i));
            }
            View headerUnlogined = LayoutInflater.from(activity).inflate(R.layout.drawer_header_unlogined, navigationView, false);
            navigationView.addHeaderView(headerUnlogined);
            TextView login = (TextView) headerUnlogined.findViewById(R.id.login);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                login.setText("Log in?");
            }
            login.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    VKSdk.login(activity);
                }
            });
            ImageView info = (ImageView) headerUnlogined.findViewById(R.id.info);
            info.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    new MaterialDialog.Builder(activity)
                            .content(R.string.login_advantages)
                            .title("Преимущества авторизации")
                            .positiveText("Ok")
                            .show();
                }
            });
        }
        ImageView donate = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.donate);
        donate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentDialogShowSubscription fragmentDialogShowSubscription = FragmentDialogShowSubscription.newInstance();
                fragmentDialogShowSubscription.show(activity.getFragmentManager(), FragmentDialogShowSubscription.LOG);

            }
        });
    }
}