package ru.kuchanov.scp2.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by y.kuchanov on 22.12.16.
 * <p>
 * for scp_ru
 */
public class MyPreferenceManager {

    public interface Keys {
        String SESSION_ID = "SESSION_ID";
        String USER_ID = "USER_ID";
        String NIGHT_MODE = "NIGHT_MODE";
        String TEXT_SCALE_UI = "TEXT_SCALE_UI";
        String TEXT_SCALE_ARTICLE = "TEXT_SCALE_ARTICLE";

        String NOTIFICATION_IS_ON = "NOTIFICATION_IS_ON";
        String NOTIFICATION_PERIOD = "NOTIFICATION_PERIOD";
        String NOTIFICATION_VIBRATION_IS_ON = "NOTIFICATION_VIBRATION_IS_ON";
        String NOTIFICATION_LED_IS_ON = "NOTIFICATION_LED_IS_ON";
        String NOTIFICATION_SOUND_IS_ON = "NOTIFICATION_SOUND_IS_ON";
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

    public void setIsNightMode(boolean isInNightMode) {
        mPreferences.edit().putBoolean(Keys.NIGHT_MODE, isInNightMode).apply();
    }

    public boolean isNightMode() {
        return mPreferences.getBoolean(Keys.NIGHT_MODE, false);
    }

    public void setUiTextScale(float uiTextScale) {
        mPreferences.edit().putFloat(Keys.TEXT_SCALE_UI, uiTextScale).apply();
    }

    public float getUiTextScale() {
        return mPreferences.getFloat(Keys.TEXT_SCALE_UI, .75f);
    }

    public float getArticleTextScale() {
        return mPreferences.getFloat(Keys.TEXT_SCALE_ARTICLE, .75f);
    }

    public void setArticleTextScale(float textScale) {
        mPreferences.edit().putFloat(Keys.TEXT_SCALE_ARTICLE, textScale).apply();
    }


    //new arts notifications
    public int getNotificationPeriodInMinutes() {
        return mPreferences.getInt(Keys.NOTIFICATION_PERIOD, 60);
    }

    public void setNotificationPeriodInMinutes(int minutes) {
        mPreferences.edit().putInt(Keys.NOTIFICATION_PERIOD, minutes).apply();
    }

    public boolean isNotificationEnabled() {
        return mPreferences.getBoolean(Keys.NOTIFICATION_IS_ON, true);
    }

    public void setNotificationEnabled(boolean enabled) {
        mPreferences.edit().putBoolean(Keys.NOTIFICATION_IS_ON, enabled).apply();
    }

    public boolean isNotificationVibrationEnabled() {
        return mPreferences.getBoolean(Keys.NOTIFICATION_VIBRATION_IS_ON, false);
    }

    public void setNotificationVibrationEnabled(boolean enabled) {
        mPreferences.edit().putBoolean(Keys.NOTIFICATION_VIBRATION_IS_ON, enabled).apply();
    }

    public boolean isNotificationLedEnabled() {
        return mPreferences.getBoolean(Keys.NOTIFICATION_LED_IS_ON, false);
    }

    public void setNotificationLedEnabled(boolean enabled) {
        mPreferences.edit().putBoolean(Keys.NOTIFICATION_LED_IS_ON, enabled).apply();
    }

    public boolean isNotificationSoundEnabled() {
        return mPreferences.getBoolean(Keys.NOTIFICATION_SOUND_IS_ON, false);
    }

    public void setNotificationSoundEnabled(boolean enabled) {
        mPreferences.edit().putBoolean(Keys.NOTIFICATION_SOUND_IS_ON, enabled).apply();
    }
}