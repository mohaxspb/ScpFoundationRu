package ru.kuchanov.scpcore.mvp.contract;

import java.util.List;

import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.db.model.ArticleTag;
import ru.dante.scpfoundation.mvp.base.BaseArticlesListMvp;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for scp_ru
 */
public interface TagsSearchResultsArticlesMvp {

    interface View extends BaseArticlesListMvp.View {

    }

    interface Presenter extends BaseArticlesListMvp.Presenter<View> {

        void setQueryTags(List<ArticleTag> queryTags);

        List<ArticleTag> getQueryTags();

        void setSearchData(List<Article> articles);

        List<Article> getSearchData();
    }
}