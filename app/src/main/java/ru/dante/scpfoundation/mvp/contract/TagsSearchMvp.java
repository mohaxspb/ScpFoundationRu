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

    }

    interface Presenter extends BaseMvp.Presenter<View> {

        void updateTagsFromApi();

        void getTagsFromDb();

        List<ArticleTag> getTags();

        void searchByTags(List<ArticleTag> tags);
    }
}