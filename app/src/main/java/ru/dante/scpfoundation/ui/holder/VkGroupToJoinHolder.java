package ru.dante.scpfoundation.ui.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.monetization.model.BaseModel;
import ru.dante.scpfoundation.monetization.model.VkGroupToJoin;
import ru.dante.scpfoundation.ui.adapter.BaseAdapterClickListener;

/**
 * Created by mohax on 25.02.2017.
 * <p>
 * for pacanskiypublic
 */
public class VkGroupToJoinHolder extends BaseHolder<BaseModel, BaseAdapterClickListener<BaseModel>> {

    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.title)
    TextView title;

    public VkGroupToJoinHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(BaseModel data) {
        super.bind(data);

        VkGroupToJoin application = (VkGroupToJoin) data;

        Context context = itemView.getContext();

        title.setText(application.name);

        content.setText(application.description);

        Glide.with(context)
                .load(application.imageUrl)
                .fitCenter()
                .dontAnimate()
                .into(image);

        itemView.setOnClickListener(view -> mAdapterClickListener.onItemClick(data));
    }
}