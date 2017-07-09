package ru.kuchanov.scpcore.mvp.presenter;

import java.util.List;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.contract.MaterialsOtherMvp;
import rx.Observable;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class MaterialsOtherPresenter
        extends BaseObjectsArticlesPresenter<MaterialsOtherMvp.View>
        implements MaterialsOtherMvp.Presenter {

    public MaterialsOtherPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    protected String getObjectsInDbFieldName() {
        return Article.FIELD_IS_IN_OTHER;
    }

    @Override
    protected String getObjectsLink() {
        return Constants.Urls.OTHERS;
    }

    @Override
    protected Observable<List<Article>> getApiObservable(int offset) {
        return mApiClient.getMaterialsArticles(getObjectsLink());
    }
}