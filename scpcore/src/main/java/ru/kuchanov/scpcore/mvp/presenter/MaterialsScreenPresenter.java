package ru.kuchanov.scpcore.mvp.presenter;

import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.base.BaseDrawerPresenter;
import ru.dante.scpfoundation.mvp.contract.MaterialsScreenMvp;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class MaterialsScreenPresenter
        extends BaseDrawerPresenter<MaterialsScreenMvp.View>
        implements MaterialsScreenMvp.Presenter {

    public MaterialsScreenPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }
}