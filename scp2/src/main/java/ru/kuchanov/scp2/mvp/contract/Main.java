package ru.kuchanov.scp2.mvp.contract;

import ru.kuchanov.scp2.mvp.base.BaseDataPresenter;
import ru.kuchanov.scp2.mvp.base.BaseMvpView;

/**
 * Created by y.kuchanov on 21.12.16.
 *
 * for scp_ru
 */
public interface Main {
    interface View extends BaseMvpView {
    }

    interface Presenter extends BaseDataPresenter<View> {
    }
}