package ru.dante.scpfoundation.activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;

import java.util.List;

import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.fragments.FragmentPreferenceAbout;
import ru.dante.scpfoundation.fragments.FragmentPreferenceNotifications;
import ru.dante.scpfoundation.fragments.FragmentPreferenceSystem;
import ru.dante.scpfoundation.utils.NotificationUtils;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class ActivitySettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{

    public static final String PREF_KEY_NIGHT_MODE = "pref_design_key_night_mode";
    private final static String LOG = ActivitySettings.class.getSimpleName();
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    public static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener()
    {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value)
        {
            String stringValue = value.toString();

            if (preference instanceof ListPreference)
            {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary((index >= 0) ? listPreference.getEntries()[index] : null);
            } else
            {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };
    SharedPreferences pref;

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context)
    {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    public static void bindPreferenceSummaryToValue(Preference preference)
    {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        int themeId;// = R.style.Theme_Preference_Light;
        //get default settings to get all settings later
        PreferenceManager.setDefaultValues(this, R.xml.pref_design, true);
        PreferenceManager.setDefaultValues(this, R.xml.pref_notification, true);
        PreferenceManager.setDefaultValues(this, R.xml.pref_about, true);
        this.pref = PreferenceManager.getDefaultSharedPreferences(this);
        themeId = (pref.getBoolean(getString(R.string.key_design_night_mode), false)) ? R.style.Theme_Preference_Dark : R.style.Theme_Preference_Light;
        this.setTheme(themeId);
        super.onCreate(savedInstanceState);

        ActionBar actionBar = this.getActionBar();
        if (null != actionBar)
        {
            this.getActionBar().setDisplayHomeAsUpEnabled(true);
            int gradientId = (pref.getBoolean(getString(R.string.key_design_night_mode), false)) ? R.drawable.gradient_dark : R.drawable.gradient_light;
            this.getActionBar().setBackgroundDrawable(ContextCompat.getDrawable(this, gradientId));
        }

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane()
    {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBuildHeaders(List<Header> target)
    {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName)
    {
        return FragmentPreferenceNotifications.class.getName().equals(fragmentName)
                || FragmentPreferenceAbout.class.getName().equals(fragmentName)
                || FragmentPreferenceSystem.class.getName().equals(fragmentName);
    }

    //change theme by restarting activity
    @Override
    public void onSharedPreferenceChanged(SharedPreferences pref, String key)
    {
        Log.d(LOG, "onSharedPreferenceChanged key: " + key);

        if (key.equals(getString(R.string.pref_notifications_key_period)))
        {
//            if (pref.getBoolean(key, false))
//            {
//                NotificationUtils.setAlarm(this);
//            }
//            else
//            {
//                NotificationUtils.cancelAlarm(this);
//            }
            NotificationUtils.checkAlarm(this);
        }
        if (key.equals(getString(R.string.pref_notifications_key_enable)))
        {
//            NotificationUtils.checkAlarm(this);
            boolean isOn = pref.getBoolean(key, false);
            if (isOn)
            {
                NotificationUtils.setAlarm(this);
            }
            else
            {
                NotificationUtils.cancelAlarm(this);
            }
        }
    }
}