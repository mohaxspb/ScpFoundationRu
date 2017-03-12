package ru.dante.scpfoundation.ui.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import ru.dante.scpfoundation.monetization.model.BaseModel;
import ru.dante.scpfoundation.ui.holder.BaseHolder;


/**
 * Created by y.kuchanov on 23.12.16.
 * <p>
 * for TappAwards
 */
public abstract class BaseRecyclerAdapter<D extends BaseModel, A extends BaseAdapterClickListener<D>, H extends BaseHolder<D, A>>
        extends RecyclerView.Adapter<H> {

    protected List<D> mData;

    protected A mAdapterClickListener;

    public void setItemClickListener(A postClickListener) {
        this.mAdapterClickListener = postClickListener;
    }

    public void setData(List<D> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public List<D> getData() {
        return mData;
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public void onBindViewHolder(H holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public void onViewAttachedToWindow(H holder) {
        super.onViewAttachedToWindow(holder);
        holder.setAdapterClickListener(mAdapterClickListener);
        //here you can reset inner adapters
    }

    @Override
    public void onViewDetachedFromWindow(H holder) {
        super.onViewDetachedFromWindow(holder);
        holder.setAdapterClickListener(null);
        //here you can clear resources in inner adapters
    }
}