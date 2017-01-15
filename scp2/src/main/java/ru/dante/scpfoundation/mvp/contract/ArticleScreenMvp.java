package ru.dante.scpfoundation.mvp.contract;

import java.util.List;

import ru.dante.scpfoundation.mvp.base.DrawerMvp;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for scp_ru
 */
public interface ArticleScreenMvp extends DrawerMvp {
    interface View extends DrawerMvp.View {
    }

    interface Presenter extends DrawerMvp.Presenter<View> {
        void setArticlesUrls(List<String> urls);

        void toggleFavorite(String url);
    }
}