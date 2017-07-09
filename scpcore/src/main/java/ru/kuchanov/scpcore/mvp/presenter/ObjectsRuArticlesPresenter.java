package ru.kuchanov.scpcore.mvp.presenter;

import ru.kuchanov.scpcore.Constants;
import ru.kuchanov.scpcore.api.ApiClient;
import ru.kuchanov.scpcore.db.DbProviderFactory;
import ru.kuchanov.scpcore.db.model.Article;
import ru.kuchanov.scpcore.manager.MyPreferenceManager;
import ru.kuchanov.scpcore.mvp.contract.ObjectsRuArticles;

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