package ru.dante.scpfoundation.utils.wantmoney;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by o.leonov on 08.10.2014.
 */
class TimeSettings
{
    private static final long FIRST_SHOW_INTERVALE=14*24*60*60*1000;// Через 14 дней показывать первый диалог
    private final static String KEY_FIRST_START_TIME="firstRunTime";
    private final static String KEY_LAST_SHOW_TIME="last_show_time";

    private final static String KEY_SHOW_MODE="show_mode";
    final static int SHOW=0;
    final static int SHOW_LATER=1;
    final static int NOT_SHOW=2;

    /*** Устанавливаем время запуска приложения */
    static void setFirstStartTime(Context cntx)
    {
        if(getStartTime(cntx)==0) {
            SharedPreferences.Editor editor = getPrefs(cntx).edit();
            editor.putLong(KEY_FIRST_START_TIME,System.currentTimeMillis()+ 7*24*60*60*1000);
            editor.apply();
        }
    }
    static boolean needShowPreRateDialog(Context cntx)
    {
        int showMode=getShowMode(cntx);
        if(showMode==SHOW)
        {
            long firstTimeStart=getStartTime(cntx);
            if(firstTimeStart!=0)
            {
                return (System.currentTimeMillis()-firstTimeStart)>FIRST_SHOW_INTERVALE;
            }
        }
     return false;
    }
    private static long getStartTime(Context cntx)
    {
        return getPrefs(cntx).getLong(KEY_FIRST_START_TIME,0);
    }

    /*** Последнее время показа окна */
    private static long getLastShowTime(Context cntx)
    {
        return getPrefs(cntx).getLong(KEY_LAST_SHOW_TIME,0);
    }
    /*** Сохранение времени последнего показа окна */
    static void saveLastShowTime(Context cntx)
    {
        SharedPreferences.Editor editor = getPrefs(cntx).edit();
        editor.putLong(KEY_LAST_SHOW_TIME,System.currentTimeMillis());
        editor.apply();
    }

    /*** Режим отображения диалога */
    private static int getShowMode(Context cntx)
    {
        return getPrefs(cntx).getInt(KEY_SHOW_MODE,SHOW);
    }
    static void setShowMode(Context cntx, int showMode)
    {
        SharedPreferences.Editor editor = getPrefs(cntx).edit();
        editor.putInt(KEY_SHOW_MODE,showMode);
        editor.apply();
    }

    private static SharedPreferences getPrefs(Context cntx)
    {
       return cntx.getSharedPreferences(cntx.getPackageName()+"Money",Context.MODE_PRIVATE);
    }
}
