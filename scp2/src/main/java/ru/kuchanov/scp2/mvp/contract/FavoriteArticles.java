package ru.kuchanov.scp2.mvp.contract;

import ru.kuchanov.scp2.mvp.base.BaseListMvp;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for scp_ru
 */
public interface FavoriteArticles {
    interface View extends BaseListMvp.View {
    }

    interface Presenter extends BaseListMvp.Presenter<View> {
    }
}