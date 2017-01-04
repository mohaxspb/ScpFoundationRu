package ru.kuchanov.scp2.db.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class Article extends RealmObject {

    public static final String FIELD_IS_IN_RECENT = "isInRecent";
    public static final int ORDER_NONE = -1;
    public static final String FIELD_URL = "url";

    public int isInRecent = ORDER_NONE;

    @PrimaryKey
    public String url;

    public String title;

    public String text;

    public String authorName;

    public String authorUrl;

    @Override
    public String toString() {
        return "Article{" +
                "isInRecent=" + isInRecent +
                ", url='" + url + '\'' +
                ", text='" + text + '\'' +
                ", authorName='" + authorName + '\'' +
                ", authorUrl='" + authorUrl + '\'' +
                '}';
    }
}