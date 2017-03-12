package ru.dante.scpfoundation.ui.holder;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.monetization.model.BaseModel;
import ru.dante.scpfoundation.ui.adapter.BaseAdapterClickListener;
import ru.dante.scpfoundation.util.DimensionUtils;

/**
 * Created by mohax on 25.02.2017.
 * <p>
 * for pacanskiypublic
 */
public class AppInviteHolder extends BaseHolder<BaseModel, BaseAdapterClickListener<BaseModel>> {

    @BindView(R.id.title)
    TextView title;

    public AppInviteHolder(View itemView, BaseAdapterClickListener<BaseModel> adapterClickListener) {
        super(itemView, adapterClickListener);
    }

    @Override
    public void bind(BaseModel data) {
        super.bind(data);

        title.setText(data.title);

        title.setCompoundDrawablePadding(DimensionUtils.getDefaultMargin());
        title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person, 0, 0, 0);

        itemView.setOnClickListener(view -> mAdapterClickListener.onItemClick(data));
    }
}