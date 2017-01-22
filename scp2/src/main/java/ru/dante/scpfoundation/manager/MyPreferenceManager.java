package ru.dante.scpfoundation.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ru.dante.scpfoundation.ui.dialog.SetttingsBottomSheetDialogFragment;

/**
 * Created by y.kuchanov on 22.12.16.
 * <p>
 * for scp_ru
 */
public class MyPreferenceManager {

    //test values
//    private static final long PERIOD_BETWEEN_ADS = 20 * 1000;
//    private static final long PERIOD_REWARDED_ADS_SHOWN = 60 * 1000;
    private static final long PERIOD_BETWEEN_ADS = 3 * 60 * 60 * 1000;
    private static final long PERIOD_REWARDED_ADS_SHOWN = 24 * 60 * 60 * 1000;

    public interface Keys {
        String SESSION_ID = "SESSION_ID";
        String USER_ID = "USER_ID";
        String NIGHT_MODE = "NIGHT_MODE";
        String TEXT_SCALE_UI = "TEXT_SCALE_UI";
        String TEXT_SCALE_ARTICLE = "TEXT_SCALE_ARTICLE";
        String DESIGN_LIST_NEW_IS_ON = "DESIGN_LIST_NEW_IS_ON";

        String NOTIFICATION_IS_ON = "NOTIFICATION_IS_ON";
        String NOTIFICATION_PERIOD = "NOTIFICATION_PERIOD";
        String NOTIFICATION_VIBRATION_IS_ON = "NOTIFICATION_VIBRATION_IS_ON";
        String NOTIFICATION_LED_IS_ON = "NOTIFICATION_LED_IS_ON";
        String NOTIFICATION_SOUND_IS_ON = "NOTIFICATION_SOUND_IS_ON";

        String ADS_LAST_TIME_SHOWS = "ADS_LAST_TIME_SHOWS";
        String ADS_REWARDED_DESCRIPTION_IS_SHOWN = "ADS_REWARDED_DESCRIPTION_IS_SHOWN";

        String LICENCE_ACCEPTED = "LICENCE_ACCEPTED";
        String CUR_APP_VERSION = "CUR_APP_VERSION";
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

    //design settings
    public boolean isDesignListNewEnabled() {
        return !mPreferences.getString(Keys.DESIGN_LIST_NEW_IS_ON, SetttingsBottomSheetDialogFragment.ListItemType.MIDDLE).equals(SetttingsBottomSheetDialogFragment.ListItemType.MIN);
    }

    public void setListDesignType(@SetttingsBottomSheetDialogFragment.ListItemType String type) {
        mPreferences.edit().putString(Keys.DESIGN_LIST_NEW_IS_ON, type).apply();
    }

    @SetttingsBottomSheetDialogFragment.ListItemType
    public String getListDesignType() {
        @SetttingsBottomSheetDialogFragment.ListItemType
        String type = mPreferences.getString(Keys.DESIGN_LIST_NEW_IS_ON, SetttingsBottomSheetDialogFragment.ListItemType.MIDDLE);
        return type;
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

    //ads
    public boolean isTimeToShowAds() {
        return System.currentTimeMillis() - getLastTimeAdsShows() >= PERIOD_BETWEEN_ADS;
    }

    public void applyRewardFromAds() {
        setLastTimeAdsShows(System.currentTimeMillis() + PERIOD_REWARDED_ADS_SHOWN);
    }

    public boolean isRewardedDescriptionShown() {
        return mPreferences.getBoolean(Keys.ADS_REWARDED_DESCRIPTION_IS_SHOWN, false);
    }

    public void setRewardedDescriptionIsNotShown(boolean isShown) {
        mPreferences.edit().putBoolean(Keys.ADS_REWARDED_DESCRIPTION_IS_SHOWN, isShown).apply();
    }

    public void setLastTimeAdsShows(long timeInMillis) {
        mPreferences.edit().putLong(Keys.ADS_LAST_TIME_SHOWS, timeInMillis).apply();
    }

    private long getLastTimeAdsShows() {
        long timeFromLastShow = mPreferences.getLong(Keys.ADS_LAST_TIME_SHOWS, 0);
        if (timeFromLastShow == 0) {
            setLastTimeAdsShows(System.currentTimeMillis());
        }
        return timeFromLastShow;
    }

    //utils
    public boolean isLicenceAccepted() {
        return mPreferences.getBoolean(Keys.LICENCE_ACCEPTED, false);
    }

    public void setLicenceAccepted(boolean accepted) {
        mPreferences.edit().putBoolean(Keys.LICENCE_ACCEPTED, accepted).apply();
    }

    public int getCurAppVersion() {
        return mPreferences.getInt(Keys.CUR_APP_VERSION, 0);
    }

    public void setCurAppVersion(int versionCode) {
        mPreferences.edit().putInt(Keys.CUR_APP_VERSION, versionCode).apply();
    }
}