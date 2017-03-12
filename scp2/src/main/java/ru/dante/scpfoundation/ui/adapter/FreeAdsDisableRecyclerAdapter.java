package ru.dante.scpfoundation.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.monetization.model.AppInviteModel;
import ru.dante.scpfoundation.monetization.model.BaseModel;
import ru.dante.scpfoundation.monetization.model.OurApplication;
import ru.dante.scpfoundation.ui.holder.AppInviteHolder;
import ru.dante.scpfoundation.ui.holder.BaseHolder;
import ru.dante.scpfoundation.ui.holder.OurApplicationHolder;
import timber.log.Timber;

/**
 * Created by mohax on 25.02.2017.
 * <p>
 * for pacanskiypublic
 */
public class FreeAdsDisableRecyclerAdapter extends BaseRecyclerAdapter<
        BaseModel,
        BaseAdapterClickListener<BaseModel>,
        BaseHolder<BaseModel, BaseAdapterClickListener<BaseModel>>
        > {

    private static final int TYPE_INVITE = 0;
    private static final int TYPE_APP_TO_INSTALL = 1;

    public FreeAdsDisableRecyclerAdapter() {
        Timber.d("FreeAdsDisableRecyclerAdapter constructor");
    }

    @Override
    public BaseHolder<BaseModel, BaseAdapterClickListener<BaseModel>> onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case TYPE_INVITE:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_title, parent, false);
                return new AppInviteHolder(itemView, mAdapterClickListener);
            case TYPE_APP_TO_INSTALL:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_title_content_image, parent, false);
                return new OurApplicationHolder(itemView);
            default:
                throw new RuntimeException(String.format("unexpected type: %s", viewType));
        }
    }

    @Override
    public int getItemViewType(int position) {
        BaseModel baseModel = mData.get(position);
        if (baseModel instanceof AppInviteModel) {
            return TYPE_INVITE;
        } else if (baseModel instanceof OurApplication) {
            return TYPE_APP_TO_INSTALL;
        } else {
            throw new RuntimeException(String.format("unexpected type for position: %s", position));
        }
    }
}