package ru.dante.scpfoundation.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mohax on 24.05.2017.
 * <p>
 * for ScpFoundationRu
 */
public class TagsSearchResponse {

    public List<Example> data;

    public static class Example {

        public int id;
        public String name;
        public String title;
        @SerializedName("all_tags")
        public List<String> allTags = null;

        @Override
        public String toString() {
            return "Example{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", title='" + title + '\'' +
                    ", allTags=" + allTags +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TagsSearchResponse{" +
                "data=" + data +
                '}';
    }
}