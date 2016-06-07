package ru.dante.scpfoundation.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

import ru.dante.scpfoundation.Article;
import ru.dante.scpfoundation.Const;

/**
 * Created for MyApplication by Dante on 04.05.2016  21:19.
 */
public class CacheUtils
{
    public enum ObjectsType
    {
        ObjectI, ObjectII, ObjectIII, ObjectRU
    }

    public static void writeObjectsToCache(Context ctx, ArrayList<Article> objects, ObjectsType type)
    {
        SharedPreferences pref = ctx.getSharedPreferences(type.name(), Context.MODE_PRIVATE);
        pref.edit().clear().apply();
        SharedPreferences.Editor editor = pref.edit();
        for (int i = 0; i < objects.size(); i++)
        {
            String url = objects.get(i).getURL();
            String title = objects.get(i).getTitle();
            String imgUrl = objects.get(i).getImageUrl();
            editor.putString(type.name() + i, url + Const.DIVIDER + title + Const.DIVIDER + imgUrl);
        }
        editor.apply();
    }

    public static ObjectsType getTypeByUrl(String url)
    {
        ObjectsType type = ObjectsType.ObjectI;
        switch (url)
        {
            case Const.Urls.OBJECTS_1:
                type = ObjectsType.ObjectI;
                break;
            case Const.Urls.OBJECTS_2:
                type = ObjectsType.ObjectII;
                break;
            case Const.Urls.OBJECTS_3:
                type = ObjectsType.ObjectIII;
                break;
            case Const.Urls.OBJECTS_RU:
                type = ObjectsType.ObjectRU;
                break;
        }
        return type;
    }

    public static ArrayList<Article> getObjectsFromCache(Context ctx, ObjectsType type)
    {
        ArrayList<Article> objects = new ArrayList<>();
        SharedPreferences pref = ctx.getSharedPreferences(type.name(), Context.MODE_PRIVATE);
        for (int i = 0; i < pref.getAll().keySet().size(); i++)
        {
            Article a = new Article();
            String info = pref.getString(type.name() + i, "");
            String[] infoArray = info.split(Const.DIVIDER);
            a.setURL(infoArray[0]);
            a.setTitle(infoArray[1]);
            a.setImageUrl(infoArray[2]);
            objects.add(a);
        }
        return objects;
    }
}
