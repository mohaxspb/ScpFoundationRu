package ru.kuchanov.scpcore.mvp.presenter;

import java.util.List;

import ru.kuchanov.scpcore.Constants;
import ru.kuchanov.scpcore.api.ApiClient;
import ru.kuchanov.scpcore.db.DbProviderFactory;
import ru.kuchanov.scpcore.db.model.Article;
import ru.kuchanov.scpcore.manager.MyPreferenceManager;
import ru.kuchanov.scpcore.mvp.contract.MaterialsExperimentsMvp;
import rx.Observable;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class MaterialsExperimentsPresenter
        extends BaseObjectsArticlesPresenter<MaterialsExperimentsMvp.View>
        implements MaterialsExperimentsMvp.Presenter {

    public MaterialsExperimentsPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    protected String getObjectsInDbFieldName() {
        return Article.FIELD_IS_IN_EXPERIMETS;
    }

    @Override
    protected String getObjectsLink() {
        return Constants.Urls.EXPERIMENTS;
    }

    @Override
    protected Observable<List<Article>> getApiObservable(int offset) {
        return mApiClient.getMaterialsArticles(getObjectsLink());
    }
}