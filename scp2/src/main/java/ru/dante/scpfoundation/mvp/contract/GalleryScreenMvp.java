package ru.dante.scpfoundation.mvp.contract;

import java.util.List;

import ru.dante.scpfoundation.db.model.VkImage;
import ru.dante.scpfoundation.mvp.base.DrawerMvp;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for scp_ru
 */
public interface GalleryScreenMvp extends DrawerMvp {
    interface View extends DrawerMvp.View {
        void showData(List<VkImage> data);

        void showCenterProgress(boolean show);

        void showEmptyPlaceholder(boolean show);
    }

    interface Presenter extends DrawerMvp.Presenter<View> {
        void updateData();

        void getDataFromDb();

        List<VkImage> getData();
    }
}