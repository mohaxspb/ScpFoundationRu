package ru.kuchanov.scp2.db.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class Article extends RealmObject {

    @PrimaryKey
    String url;

    String text;

    String authorName;

    String authorUrl;
}