package ru.dante.scpfoundation.mvp.presenter;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.contract.Objects3Articles;
import ru.dante.scpfoundation.mvp.contract.Objects4Articles;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class Objects4ArticlesPresenter
        extends BaseObjectsArticlesPresenter<Objects4Articles.View>
        implements Objects4Articles.Presenter {

    public Objects4ArticlesPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    protected String getObjectsInDbFieldName() {
        return Article.FIELD_IS_IN_OBJECTS_4;
    }

    @Override
    protected String getObjectsLink() {
        return Constants.Urls.OBJECTS_4;
    }
}