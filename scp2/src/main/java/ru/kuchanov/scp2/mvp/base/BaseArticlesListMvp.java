package ru.kuchanov.scp2.mvp.base;

import java.util.List;

import ru.kuchanov.scp2.db.model.Article;

/**
 * Created by mohax on 09.01.2017.
 * <p>
 * for scp_ru
 */
public interface BaseArticlesListMvp {
    interface View extends BaseListMvp.View {

    }

    interface Presenter<V extends View> extends BaseListMvp.Presenter<V>, BaseArticleActions {

    }
}