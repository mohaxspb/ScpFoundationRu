package ru.kuchanov.library;

import android.util.Pair;

import java.util.List;

import rx.Observable;

/**
 * Created by mohax on 24.06.2017.
 * <p>
 * for ScpDownloads
 */
public interface DbProviderModel<T extends ArticleModel> {

    T getUnmanagedArticleSync(String id);

    void saveArticleSync(T articleModel, boolean closeDbConnection);

    void close();

    int getScore();

    Observable<Pair<Integer, Integer>> saveObjectsArticlesList(List<T> articles, String dbField);
}