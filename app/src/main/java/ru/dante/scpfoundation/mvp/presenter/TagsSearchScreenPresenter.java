package ru.dante.scpfoundation.mvp.presenter;

import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.base.BaseDrawerPresenter;
import ru.dante.scpfoundation.mvp.contract.TagsScreenMvp;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class TagsSearchScreenPresenter
        extends BaseDrawerPresenter<TagsScreenMvp.View>
        implements TagsScreenMvp.Presenter {

    public TagsSearchScreenPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }
}