package ru.kuchanov.scp2.mvp.base;

/**
 * Created by mohax on 09.01.2017.
 * <p>
 * for scp_ru
 */
public interface BaseArticleActions {
    void toggleFavoriteState(String url);

    void toggleReadenState(String url);

    void toggleOfflineState(String url);
}