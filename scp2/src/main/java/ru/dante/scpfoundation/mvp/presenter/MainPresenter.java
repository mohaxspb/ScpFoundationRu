package ru.dante.scpfoundation.mvp.presenter;

import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.contract.MainMvp;
import timber.log.Timber;

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

    @Override
    public void onCreate() {
        Timber.d("onCreate");
        //nothing to do
    }

    @Override
    public void onDestroy() {
        //nothing to do
    }

    @Override
    public void onNavigationItemClicked(int id) {
        //nothing to do
//        //FIXME delete test
//        mApiClient.test();
    }

    @Override
    protected void onReceiveUserFromDb() {
        super.onReceiveUserFromDb();
        getView().onGetUserFromDB(mUser);
    }
}