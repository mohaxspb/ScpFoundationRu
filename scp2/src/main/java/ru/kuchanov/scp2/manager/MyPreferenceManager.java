package ru.kuchanov.scp2.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by y.kuchanov on 22.12.16.
 *
 * for scp_ru
 */
public class MyPreferenceManager {

    interface Keys {
        String SESSION_ID = "SESSION_ID";
        String USER_ID = "USER_ID";
    }

    private SharedPreferences mPreferences;

    public MyPreferenceManager(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setSessionId(String sessionId) {
        mPreferences.edit().putString(Keys.SESSION_ID, sessionId).apply();
    }

    public String getSessionId() {
        return mPreferences.getString(Keys.SESSION_ID, "");
    }

    public void setUserId(String userId) {
        mPreferences.edit().putString(Keys.USER_ID, userId).apply();
    }

    public String getUserId() {
        return mPreferences.getString(Keys.USER_ID, "");
    }
}