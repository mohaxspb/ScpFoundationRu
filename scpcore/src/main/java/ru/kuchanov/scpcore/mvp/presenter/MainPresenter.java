package ru.kuchanov.scpcore.mvp.presenter;

import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.base.BaseDrawerPresenter;
import ru.dante.scpfoundation.mvp.contract.MainMvp;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class MainPresenter
        extends BaseDrawerPresenter<MainMvp.View>
        implements MainMvp.Presenter {

    public MainPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }
}