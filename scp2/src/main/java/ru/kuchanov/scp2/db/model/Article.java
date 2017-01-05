package ru.kuchanov.scp2.db.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class Article extends RealmObject {

    public static final String FIELD_IS_IN_READEN = "isInReaden";
    public static final String FIELD_IS_IN_RECENT = "isInRecent";
    public static final String FIELD_IS_IN_FAVORITE = "isInFavorite";
    public static final String FIELD_IS_IN_MOST_RATED = "isInMostRated";
    public static final int ORDER_NONE = -1;
    public static final String FIELD_URL = "url";

    public int isInRecent = ORDER_NONE;
    public int isInFavorite = ORDER_NONE;
    public int isInMostRated = ORDER_NONE;
    public boolean isInReaden;

    @PrimaryKey
    public String url;
    public String title;
    public String text;

    public int rating;

    public String authorName;
    public String authorUrl;
    /**
     * in format 01:06 01.07.2010
     */
    public String createdDate;
    /**
     * in format 01:06 01.07.2010
     */
    public String updatedDate;

    @Override
    public String toString() {
        return "Article{" +
                "isInRecent=" + isInRecent +
                ", isInFavorite=" + isInFavorite +
                ", isInMostRated=" + isInMostRated +
                ", isInReaden=" + isInReaden +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
//                ", text='" + text + '\'' +
                ", rating=" + rating +
                ", authorName='" + authorName + '\'' +
                ", authorUrl='" + authorUrl + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", updatedDate='" + updatedDate + '\'' +
                '}';
    }
}