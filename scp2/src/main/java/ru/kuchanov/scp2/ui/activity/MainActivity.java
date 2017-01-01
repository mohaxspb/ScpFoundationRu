package ru.kuchanov.scp2.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;

import butterknife.ButterKnife;
import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.mvp.contract.Main;
import ru.kuchanov.scp2.ui.base.BaseDrawerActivity;
import timber.log.Timber;

public class MainActivity extends BaseDrawerActivity<Main.View, Main.Presenter> implements Main.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mPresenter.onCreate();

        mNavigationView.setNavigationItemSelectedListener(item -> {
            onNavigationItemClicked(item.getItemId());
            mPresenter.onNavigationItemClicked(item.getItemId());
            return false;
        });
    }

    @NonNull
    @Override
    public Main.Presenter createPresenter() {
        MyApplication.getAppComponent().inject(this);
        return mPresenter;
    }

    @Override
    public void onNavigationItemClicked(int id) {
        Timber.d("onNavigationItemClicked with id: %s", id);
        //TODO
    }
}