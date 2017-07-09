package ru.kuchanov.scpcore.ui.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import at.grabner.circleprogress.CircleProgressView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ru.kuchanov.scpcore.R;

/**
 * Created by mohax on 23.03.2017.
 * <p>
 * for scp_ru
 */
public class HeaderViewHolderLogined {

    @BindView(R.id.level)
    public TextView level;
    @BindView(R.id.levelUp)
    public ImageView levelUp;
    @BindView(R.id.name)
    public TextView name;
    @BindView(R.id.avatar)
    public ImageView avatar;
    @BindView(R.id.circleView)
    public CircleProgressView circleProgress;
    @BindView(R.id.levelNum)
    public TextView levelNum;
    @BindView(R.id.logout)
    public View logout;
    @BindView(R.id.relogin)
    public View relogin;
    @BindView(R.id.inapp)
    public View inapp;

    public HeaderViewHolderLogined(View view) {
        ButterKnife.bind(this, view);
    }
}