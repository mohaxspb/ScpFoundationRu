package ru.dante.scpfoundation.mvp.contract;

/**
 * Created by mohax on 24.03.2017.
 * <p>
 * for scp_ru
 */
public interface DataSyncActions {

    void syncFavorite(String url, boolean isFavorite);
}