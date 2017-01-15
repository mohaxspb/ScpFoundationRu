package ru.dante.scpfoundation.mvp.contract;

import ru.dante.scpfoundation.mvp.base.BaseArticlesListMvp;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for scp_ru
 */
public interface ObjectsRuArticles {
    interface View extends BaseArticlesListMvp.View {
    }

    interface Presenter extends BaseArticlesListMvp.Presenter<View> {
    }
}