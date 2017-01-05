package ru.kuchanov.scp2.mvp.contract;

import ru.kuchanov.scp2.db.model.Article;
import ru.kuchanov.scp2.mvp.base.BaseDataPresenter;
import ru.kuchanov.scp2.mvp.base.BaseListMvp;
import ru.kuchanov.scp2.mvp.base.BaseMvpView;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for scp_ru
 */
public interface About {
    interface View extends BaseListMvp.View {
    }

    interface Presenter extends BaseListMvp.Presenter<View> {
    }
}