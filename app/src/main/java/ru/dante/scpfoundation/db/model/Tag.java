package ru.dante.scpfoundation.db.model;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mohax on 24.05.2017.
 * <p>
 * for ScpFoundationRu
 */
public class Tag extends RealmObject {

    @PrimaryKey
    public String title;

    @Override
    public String toString() {
        return "Tag{" +
                "title='" + title + '\'' +
                '}';
    }

    public static List<String> getStringsFromTags(List<Tag> tags) {
        return //TODO
    }
}