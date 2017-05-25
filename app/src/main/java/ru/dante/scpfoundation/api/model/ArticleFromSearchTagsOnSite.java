package ru.dante.scpfoundation.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mohax on 25.05.2017.
 * <p>
 * for ScpFoundationRu
 */
public class ArticleFromSearchTagsOnSite {
    public int id;
    public String name;
    public String title;
    @SerializedName("all_tags")
    public List<String> allTags;

    @Override
    public String toString() {
        return "ArticleFromSearchTagsOnSite{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", allTags=" + allTags +
                '}';
    }
}