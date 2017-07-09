package ru.kuchanov.scpcore.mvp.presenter;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.contract.ObjectsRuArticles;

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