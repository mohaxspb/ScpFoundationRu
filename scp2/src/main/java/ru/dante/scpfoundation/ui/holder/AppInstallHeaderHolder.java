package ru.dante.scpfoundation.ui.holder;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.monetization.model.BaseModel;
import ru.dante.scpfoundation.ui.adapter.BaseAdapterClickListener;

/**
 * Created by mohax on 25.02.2017.
 * <p>
 * for pacanskiypublic
 */
public class AppInstallHeaderHolder extends BaseHolder<BaseModel, BaseAdapterClickListener<BaseModel>> {

    @BindView(R.id.title)
    TextView title;

    public AppInstallHeaderHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(BaseModel data) {
        super.bind(data);

        title.setText(data.title);

//        title.setCompoundDrawablePadding(DimensionUtils.getDefaultMargin());
//        title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_shop, 0, 0, 0);
    }
}