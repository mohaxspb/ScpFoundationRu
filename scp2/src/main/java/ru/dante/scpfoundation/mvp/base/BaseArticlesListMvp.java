package ru.dante.scpfoundation.mvp.base;

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