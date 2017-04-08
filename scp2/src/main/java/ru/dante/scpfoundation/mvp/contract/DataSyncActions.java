package ru.dante.scpfoundation.mvp.contract;

import ru.dante.scpfoundation.db.model.Article;

/**
 * Created by mohax on 24.03.2017.
 * <p>
 * for scp_ru
 */
public interface DataSyncActions {

    void updateArticleInFirebase(Article article, boolean showResultMessage);

    void syncArticles(boolean showResultMessage);
}