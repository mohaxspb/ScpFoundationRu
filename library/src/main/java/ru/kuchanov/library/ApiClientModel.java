package ru.kuchanov.library;

import java.util.List;

import rx.Observable;

/**
 * Created by mohax on 25.06.2017.
 * <p>
 * for ScpDownloads
 */
public interface ApiClientModel<T extends ArticleModel> {

    Observable<Integer> getRecentArticlesPageCountObservable();

    Observable<List<T>> getRecentArticlesForPage(int integer);

    Observable<List<T>> getMaterialsArticles(String link);

    Observable<List<T>> getObjectsArticles(String link);

    Observable<List<T>> getMaterialsJokesArticles();

    Observable<List<T>> getMaterialsArchiveArticles();

    T getArticleFromApi(String url) throws Exception, ru.kuchanov.library.ScpParseException;
}