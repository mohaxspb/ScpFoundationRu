package ru.dante.scpfoundation.ui.fragment;

import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.mvp.contract.MaterialsOtherMvp;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class MaterialsOtherFragment
        extends BaseListArticlesWithSearchFragment<MaterialsOtherMvp.View, MaterialsOtherMvp.Presenter>
        implements MaterialsOtherMvp.View {

    public static final String TAG = MaterialsOtherFragment.class.getSimpleName();

    public static MaterialsOtherFragment newInstance() {
        return new MaterialsOtherFragment();
    }

    @Override
    protected void callInjections() {
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    protected void initViews() {
        super.initViews();

        if (getUserVisibleHint()) {
            if (getActivity() instanceof ArticleFragment.ToolbarStateSetter) {
                ((ArticleFragment.ToolbarStateSetter) getActivity()).setTitle(getString(R.string.materials_others));
            }
        }
    }

    @Override
    public void resetOnScrollListener() {
        //we do not have paging
    }

    @Override
    protected boolean shouldUpdateThisListOnLaunch() {
        return false;
    }
}