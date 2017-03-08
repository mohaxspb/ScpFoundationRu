package ru.dante.scpfoundation.mvp.presenter;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.contract.MaterialsJokesMvp;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class MaterialsJokesPresenter
        extends BaseObjectsArticlesPresenter<MaterialsJokesMvp.View>
        implements MaterialsJokesMvp.Presenter {

    public MaterialsJokesPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    protected String getObjectsInDbFieldName() {
        return Article.FIELD_IS_IN_JOKES;
    }

    @Override
    protected String getObjectsLink() {
        return Constants.Urls.JOKES;
    }

//    @Override
//    protected Observable<List<Article>> getApiObservable(int offset) {
//        return mApiClient.getMaterialsArticles(getObjectsLink());
//    }
}