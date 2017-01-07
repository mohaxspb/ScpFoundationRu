package ru.kuchanov.scp2.db.model;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.kuchanov.scp2.api.ParseHtmlUtils;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class Article extends RealmObject implements Serializable {

    public static final String FIELD_IS_IN_READEN = "isInReaden";
    public static final String FIELD_IS_IN_RECENT = "isInRecent";
    public static final String FIELD_IS_IN_FAVORITE = "isInFavorite";
    public static final String FIELD_IS_IN_MOST_RATED = "isInMostRated";
    public static final int ORDER_NONE = -1;
    public static final String FIELD_URL = "url";

    //util fields
    public int isInRecent = ORDER_NONE;
    public int isInFavorite = ORDER_NONE;
    public int isInMostRated = ORDER_NONE;
    public boolean isInReaden;

    public boolean hasTabs;
    public RealmList<RealmString> tabsTexts;
    public RealmList<RealmString> tabsTitles;

    public RealmList<RealmString> textParts;
//    @ParseHtmlUtils.TextType
    public RealmList<RealmString> textPartsTypes;

    //site info
    @PrimaryKey
    public String url;
    public String title;
    /**
     * raw html
     */
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
                ", hasTabs=" + hasTabs +
//                ", tabsTexts=" + tabsTexts +
                ", tabsTitles=" + tabsTitles +
                ", textParts=" + textParts +
                ", textPartsTypes=" + textPartsTypes +
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