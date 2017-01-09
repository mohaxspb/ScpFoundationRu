package ru.kuchanov.scp2.mvp.presenter;

import ru.kuchanov.scp2.Constants;
import ru.kuchanov.scp2.api.ApiClient;
import ru.kuchanov.scp2.db.DbProviderFactory;
import ru.kuchanov.scp2.db.model.Article;
import ru.kuchanov.scp2.manager.MyPreferenceManager;
import ru.kuchanov.scp2.mvp.contract.ObjectsRuArticles;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class ObjectsRuArticlesPresenter
        extends BaseObjectsArticlesPresenter<ObjectsRuArticles.View>
        implements ObjectsRuArticles.Presenter {

    public ObjectsRuArticlesPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    protected String getObjectsInDbFieldName() {
        return Article.FIELD_IS_IN_OBJECTS_RU;
    }

    @Override
    protected String getObjectsLink() {
        return Constants.Urls.OBJECTS_RU;
    }
}