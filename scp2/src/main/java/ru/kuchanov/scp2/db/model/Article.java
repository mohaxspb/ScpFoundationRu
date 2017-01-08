package ru.kuchanov.scp2.db.model;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import java.util.ArrayList;
import java.util.List;

import io.realm.ArticleRealmProxy;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.kuchanov.scp2.db.util.RealmStringListParcelConverter;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
@Parcel(implementations = {ArticleRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {Article.class})
public class Article extends RealmObject {

    public static final String FIELD_IS_IN_READEN = "isInReaden";
    public static final String FIELD_IS_IN_RECENT = "isInRecent";
    public static final String FIELD_IS_IN_FAVORITE = "isInFavorite";
    public static final String FIELD_IS_IN_MOST_RATED = "isInMostRated";
    public static final int ORDER_NONE = -1;
    public static final String FIELD_URL = "url";

    //util fields
    public long isInRecent = ORDER_NONE;
    public long isInFavorite = ORDER_NONE;
    public long isInMostRated = ORDER_NONE;
    public boolean isInReaden;

    public boolean hasTabs;
    @ParcelPropertyConverter(RealmStringListParcelConverter.class)
    public RealmList<RealmString> tabsTexts;
    @ParcelPropertyConverter(RealmStringListParcelConverter.class)
    public RealmList<RealmString> tabsTitles;

    @ParcelPropertyConverter(RealmStringListParcelConverter.class)
    public RealmList<RealmString> textParts;
    @ParcelPropertyConverter(RealmStringListParcelConverter.class)
    public RealmList<RealmString> textPartsTypes;

    //images
    @ParcelPropertyConverter(RealmStringListParcelConverter.class)
    public RealmList<RealmString> imagesUrls;

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

    public static List<String> getListOfUrls(Article... articles) {
        List<String> urls = new ArrayList<>();
        for (Article article : articles) {
            urls.add(article.url);
        }
        return urls;
    }

    public static List<String> getListOfUrls(List<Article> articles) {
        List<String> urls = new ArrayList<>();
        for (Article article : articles) {
            urls.add(article.url);
        }
        return urls;
    }

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