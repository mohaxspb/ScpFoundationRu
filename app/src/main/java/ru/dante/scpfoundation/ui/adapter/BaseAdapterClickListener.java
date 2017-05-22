package ru.dante.scpfoundation.ui.adapter;

import ru.dante.scpfoundation.monetization.model.BaseModel;

/**
 * Created by y.kuchanov on 11.01.17.
 * <p>
 * for TappAwards
 */
public interface BaseAdapterClickListener<D extends BaseModel> {

    void onItemClick(D data);
}