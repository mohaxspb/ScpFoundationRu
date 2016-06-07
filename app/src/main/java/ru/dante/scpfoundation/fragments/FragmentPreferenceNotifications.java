package ru.dante.scpfoundation.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import ru.dante.scpfoundation.R;

/**
 * Created by Юрий on 21.09.2015 18:01.
 * For ExpListTest.
 */
public class FragmentPreferenceNotifications extends PreferenceFragment
{
    private static final String LOG = FragmentPreferenceNotifications.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_design, true);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_notification, true);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_system, true);

        addPreferencesFromResource(R.xml.pref_notification);
//        SwitchPreference notificationOnOff = (SwitchPreference) findPreference(getString(R.string.pref_notifications_key_enable));
//        notificationOnOff.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
//        {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object newValue)
//            {
//                SwitchPreference notificationOnOff = (SwitchPreference) preference;
//                boolean isnotificationOn = notificationOnOff.isChecked();
//
//                if (isnotificationOn)
//                {
//                    NotificationUtils.setAlarm(getActivity());
//                } else
//                {
//                  NotificationUtils.cancelAlarm(getActivity());
//                }
//                return true;
//            }
//        });
        ListPreference periodPref = (ListPreference) findPreference(getString(R.string.pref_notifications_key_period));
        periodPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                Log.i(LOG,newValue.toString());
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                int periodInMinutes = Integer.parseInt(pref.getString(getActivity().getString(R.string.pref_notifications_key_period), "30"));
                Log.i(LOG, String.valueOf(periodInMinutes));
                return true;
            }
        });
    }
}