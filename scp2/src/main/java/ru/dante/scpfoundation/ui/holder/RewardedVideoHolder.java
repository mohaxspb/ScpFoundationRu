package ru.dante.scpfoundation.ui.holder;

import android.support.v4.content.ContextCompat;
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
public class RewardedVideoHolder extends BaseHolder<BaseModel, BaseAdapterClickListener<BaseModel>> {

    @BindView(R.id.title)
    TextView title;

    public RewardedVideoHolder(View itemView, BaseAdapterClickListener<BaseModel> adapterClickListener) {
        super(itemView, adapterClickListener);
    }

    @Override
    public void bind(BaseModel data) {
        super.bind(data);

        title.setText(data.title);
        title.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.material_green_500));

        title.setCompoundDrawablePadding(DimensionUtils.getDefaultMargin());
        title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_video_library, 0, 0, 0);

        itemView.setOnClickListener(view -> mAdapterClickListener.onItemClick(data));
    }
}