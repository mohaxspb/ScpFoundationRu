package ru.kuchanov.scpcore.mvp.presenter;

import java.util.List;

import ru.kuchanov.scpcore.Constants;
import ru.kuchanov.scpcore.api.ApiClient;
import ru.kuchanov.scpcore.db.DbProviderFactory;
import ru.kuchanov.scpcore.db.model.Article;
import ru.kuchanov.scpcore.manager.MyPreferenceManager;
import ru.kuchanov.scpcore.mvp.contract.MaterialsArchiveMvp;
import rx.Observable;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class MaterialsArchivePresenter
        extends BaseObjectsArticlesPresenter<MaterialsArchiveMvp.View>
        implements MaterialsArchiveMvp.Presenter {

    public MaterialsArchivePresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    protected String getObjectsInDbFieldName() {
        return Article.FIELD_IS_IN_ARCHIVE;
    }

    @Override
    protected String getObjectsLink() {
        return Constants.Urls.ARCHIVE;
    }

    @Override
    protected Observable<List<Article>> getApiObservable(int offset) {
        return mApiClient.getMaterialsArchiveArticles();
    }
}