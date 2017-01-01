package ru.kuchanov.scp2.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;

import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.mvp.contract.Main;
import ru.kuchanov.scp2.ui.base.BaseActivity;

public class MainActivity extends BaseActivity<Main.View, Main.Presenter> implements Main.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPresenter.onCreate();
    }

    @NonNull
    @Override
    public Main.Presenter createPresenter() {
        MyApplication.getAppComponent().inject(this);
        return mPresenter;
    }
}