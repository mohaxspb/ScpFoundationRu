package ru.kuchanov.scp2.mvp.base;

import java.util.List;

/**
 * Created by mohax on 25.12.2016.
 * <p>
 * for pacanskiypublic
 */
public interface BaseListMvp {
    interface View<D> extends BaseMvpView {
        void showSwipeProgress(boolean show);

        void showCenterProgress(boolean show);

        void showBottomProgress(boolean show);

        void enableSwipeRefresh(boolean enable);

        void updateData(List<D> data);
    }

    interface Presenter<D, V extends View> extends BaseDataPresenter<V> {
        List<D> getData();

        void getDataFromDb();

        void getDataFromApi();
    }
}