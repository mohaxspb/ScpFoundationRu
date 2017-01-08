package ru.kuchanov.scp2.mvp.contract;

import ru.kuchanov.scp2.mvp.base.BaseArticlesListMvp;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for scp_ru
 */
public interface RatedArticlesMvp {
    interface View extends BaseArticlesListMvp.View {
    }

    interface Presenter extends BaseArticlesListMvp.Presenter<View> {
    }
}