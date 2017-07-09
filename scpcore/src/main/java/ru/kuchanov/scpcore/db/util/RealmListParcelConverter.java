package ru.kuchanov.scpcore.db.util;

import org.parceler.converter.CollectionParcelConverter;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by mohax on 07.01.2017.
 * <p>
 * for scp_ru
 *
 * from https://gist.github.com/cmelchior/72c35fcb55cec33a71e1
 */
// Abstract class for working with RealmLists
abstract class RealmListParcelConverter<T extends RealmObject> extends CollectionParcelConverter<T, RealmList<T>> {
    @Override
    public RealmList<T> createCollection() {
        return new RealmList<>();
    }
}