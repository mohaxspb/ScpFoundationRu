package ru.kuchanov.scpcore.mvp.presenter;

import java.util.List;

import ru.kuchanov.scpcore.Constants;
import ru.kuchanov.scpcore.api.ApiClient;
import ru.kuchanov.scpcore.db.DbProviderFactory;
import ru.kuchanov.scpcore.db.model.Article;
import ru.kuchanov.scpcore.manager.MyPreferenceManager;
import ru.kuchanov.scpcore.mvp.contract.MaterialsJokesMvp;
import rx.Observable;

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

    @Override
    protected Observable<List<Article>> getApiObservable(int offset) {
        return mApiClient.getMaterialsJokesArticles();
    }
}