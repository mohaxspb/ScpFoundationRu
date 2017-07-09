package ru.kuchanov.scpcore.mvp.presenter;

import ru.kuchanov.scpcore.Constants;
import ru.kuchanov.scpcore.api.ApiClient;
import ru.kuchanov.scpcore.db.DbProviderFactory;
import ru.kuchanov.scpcore.db.model.Article;
import ru.kuchanov.scpcore.manager.MyPreferenceManager;
import ru.kuchanov.scpcore.mvp.contract.Objects1Articles;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class Objects1ArticlesPresenter
        extends BaseObjectsArticlesPresenter<Objects1Articles.View>
        implements Objects1Articles.Presenter {

    public Objects1ArticlesPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    protected String getObjectsInDbFieldName() {
        return Article.FIELD_IS_IN_OBJECTS_1;
    }

    @Override
    protected String getObjectsLink() {
        return Constants.Urls.OBJECTS_1;
    }
}