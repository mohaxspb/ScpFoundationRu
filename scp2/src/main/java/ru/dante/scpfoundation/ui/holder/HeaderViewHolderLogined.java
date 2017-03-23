package ru.dante.scpfoundation.ui.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dante.scpfoundation.R;

/**
 * Created by mohax on 23.03.2017.
 * <p>
 * for scp_ru
 */
public class HeaderViewHolderLogined {

    @BindView(R.id.level)
    public TextView level;
    @BindView(R.id.name)
    public TextView name;
    @BindView(R.id.avatar)
    public ImageView avatar;
    @BindView(R.id.logout)
    public View logout;
    @BindView(R.id.inapp)
    public View inapp;

    public HeaderViewHolderLogined(View view) {
        ButterKnife.bind(this, view);
    }
}