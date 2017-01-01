package ru.kuchanov.scp2.mvp.contract;

import ru.kuchanov.scp2.mvp.base.BaseDataPresenter;
import ru.kuchanov.scp2.mvp.base.BaseMvpView;
import ru.kuchanov.scp2.mvp.base.Drawer;

/**
 * Created by y.kuchanov on 21.12.16.
 *
 * for scp_ru
 */
public interface Main extends Drawer {
    interface View extends Drawer.View {
    }

    interface Presenter extends Drawer.Presenter<View> {
    }
}