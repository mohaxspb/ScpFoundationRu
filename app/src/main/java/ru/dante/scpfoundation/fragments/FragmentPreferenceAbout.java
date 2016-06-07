package ru.dante.scpfoundation.fragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.utils.AttributeGetter;


/**
 * Created by Юрий on 21.09.2015 17:57.
 * For ExpListTest.
 */
public class FragmentPreferenceAbout extends PreferenceFragment
{
    protected Preference.OnPreferenceClickListener gitHubCL = new Preference.OnPreferenceClickListener()
    {
        @Override
        public boolean onPreferenceClick(Preference preference)
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mohaxspb/TProger")));
            return false;
        }
    };
    private int numOfClicks = 0;
    protected Preference.OnPreferenceClickListener easterEggCL = new Preference.OnPreferenceClickListener()
    {
        @Override
        public boolean onPreferenceClick(Preference preference)
        {
            if (numOfClicks > 7)
            {
                numOfClicks = 0;
                if (getView() == null)
                {
                    return false;
                }
                Snackbar snackbar = Snackbar.make(getView(), "Пасхальный SnackBar!", Snackbar.LENGTH_LONG);
                snackbar.setAction("Тык!", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        try
                        {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=ru.kuchanov.odnako")));
                        }
                        catch (Exception e)
                        {
                            String marketErrMsg = "Должен был запуститься Play Market, но что-то пошло не так... Так запустим же браузер!";
                            System.out.println(marketErrMsg);
                            Toast.makeText(getActivity(), marketErrMsg, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://details?id=ru.kuchanov.odnako")));
                        }
                    }
                });
                View snackBarView = snackbar.getView();
                int colorId = AttributeGetter.getColor(getActivity(), R.attr.colorPrimaryDark);
                snackBarView.setBackgroundColor(colorId);
                snackbar.show();
            }
            else
            {
                numOfClicks++;
            }
            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_about);

        //set onClickListeners
        Preference prefVersion = this.findPreference(this.getString(R.string.pref_about_version_key));
        PackageInfo pInfo = null;
        try
        {
            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String version = pInfo.versionName;
            prefVersion.setSummary(version);
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }


        Preference prefLicense = this.findPreference(this.getString(R.string.pref_about_license_key));
        prefLicense.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                new MaterialDialog.Builder(getActivity())
                        .content(R.string.dialog_info_content)
                        .title("О приложении")
                        .show();
                return false;
            }
        });
        Preference prefMarket= findPreference(getString(R.string.pref_about_market_key));
        prefMarket.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                try
                {
                    getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "ru.dante.scpfoundation")));
                } catch (Exception e)
                {
                    String marketErrMsg = "Должен был запуститься Play Market, но что-то пошло не так...";
                    System.out.println(marketErrMsg);
                    Toast.makeText(getActivity(), marketErrMsg, Toast.LENGTH_SHORT).show();
                }
                return false;
            }});
    }
}