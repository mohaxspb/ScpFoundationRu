package ru.kuchanov.library;

/**
 * Created by mohax on 24.06.2017.
 * <p>
 * for ScpDownloads
 */
public interface DbProviderModel<T extends ArticleModel> {

    ArticleModel getUnmanagedArticleSync(String id);

//    void saveArticleSync(ArticleModel articleModel, boolean closeDbConnection);

    void saveArticleSync(T articleModel, boolean closeDbConnection);

    void close();

    int getScore();
}