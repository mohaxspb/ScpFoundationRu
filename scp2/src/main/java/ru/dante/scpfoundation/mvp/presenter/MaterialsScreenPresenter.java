package ru.dante.scpfoundation.mvp.presenter;

import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
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

    @Override
    public void onCreate() {
//        Timber.d("onCreate");
        //nothing to do
    }

    @Override
    public void onDestroy() {
        //nothing to do
    }

    @Override
    public void onNavigationItemClicked(int id) {
        //nothing to do
    }

    @Override
    protected void onReceiveUserFromDb() {
        super.onReceiveUserFromDb();
        getView().onGetUserFromDB(mUser);
    }
}