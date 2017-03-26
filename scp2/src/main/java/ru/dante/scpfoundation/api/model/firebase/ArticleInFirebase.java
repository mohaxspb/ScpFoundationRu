package ru.dante.scpfoundation.api.model.firebase;

/**
 * Created by mohax on 26.03.2017.
 * <p>
 * for scp_ru
 */
public class ArticleInFirebase {

    public boolean isFavorite;
    public boolean isRead;
    public String title;

    public ArticleInFirebase(boolean isFavorite, boolean isRead, String title) {
        this.isFavorite = isFavorite;
        this.isRead = isRead;
        this.title = title;
    }

    public ArticleInFirebase() {
    }

    @Override
    public String toString() {
        return "ArticleInFirebase{" +
                "isFavorite=" + isFavorite +
                ", isRead=" + isRead +
                ", title='" + title + '\'' +
                '}';
    }
}