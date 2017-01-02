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
        String title = "";
        switch (id) {
            case R.id.about:
                title = "Об организации";
                break;
            case R.id.news:
                title = "Новости";
                break;
            case R.id.rate_articles:
                title = "Статьи по рейтингу";
                break;
            case R.id.new_articles:
                title = "Новые статьи";
                break;
            case R.id.objects_I:
                title = "Объекты I";
                break;
            case R.id.objects_II:
                title = "Объекты II";
                break;
            case R.id.objects_III:
                title = "Объекты III";
                break;
            case R.id.objects_RU:
                title = "Объекты RU";
                break;
            case R.id.favorite:
                title = "Избранное";
                break;
            case R.id.offline:
                title = "Офлайн";
                break;
            case R.id.stories:
                title = "Рассказы";
                break;
            case R.id.files:
                title = "Материалы";
                break;
            case R.id.site_search:
                title = "Поиск";
                break;
        }
    }
}