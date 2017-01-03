package ru.kuchanov.scp2.mvp.contract;

import java.util.List;

import ru.kuchanov.scp2.db.model.Article;
import ru.kuchanov.scp2.mvp.base.BaseListMvp;
import ru.kuchanov.scp2.mvp.base.Drawer;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for scp_ru
 */
public interface RecentArticles {
    interface View extends BaseListMvp.View<Article> {
    }

    interface Presenter extends BaseListMvp.Presenter<Article, View> {
    }
}