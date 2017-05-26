package ru.dante.scpfoundation.mvp.contract;

import java.util.List;

import ru.dante.scpfoundation.db.model.ArticleTag;
import ru.dante.scpfoundation.mvp.base.BaseMvp;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for scp_ru
 */
public interface TagsSearchMvp extends DrawerMvp {

    interface View extends BaseMvp.View {

        void showAllTags(List<ArticleTag> data);

        void showProgress(boolean show);
    }

    interface Presenter extends BaseMvp.Presenter<View> {

        void getTagsFromApi();

        void getTagsFromDb();

        List<ArticleTag> getTags();

        void searchByTags(List<ArticleTag> tags);
    }
}