package ru.kuchanov.scp2.mvp.presenter;

import ru.kuchanov.scp2.Constants;
import ru.kuchanov.scp2.api.ApiClient;
import ru.kuchanov.scp2.db.DbProviderFactory;
import ru.kuchanov.scp2.db.model.Article;
import ru.kuchanov.scp2.manager.MyPreferenceManager;
import ru.kuchanov.scp2.mvp.contract.Objects2Articles;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class Objects2ArticlesPresenter
        extends BaseObjectsArticlesPresenter<Objects2Articles.View>
        implements Objects2Articles.Presenter {

    public Objects2ArticlesPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    protected String getObjectsInDbFieldName() {
        return Article.FIELD_IS_IN_OBJECTS_2;
    }

    @Override
    protected String getObjectsLink() {
        return Constants.Urls.OBJECTS_2;
    }
}