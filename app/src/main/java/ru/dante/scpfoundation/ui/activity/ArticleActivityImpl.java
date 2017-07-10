package ru.dante.scpfoundation.ui.activity;

import ru.dante.scpfoundation.AppComponentImpl;
import ru.dante.scpfoundation.MyApplicationTest;
import ru.kuchanov.scpcore.ui.activity.ArticleActivity;

/**
 * Created by mohax on 10.07.2017.
 * <p>
 * for ScpFoundationRu
 */
public class ArticleActivityImpl extends ArticleActivity {
    @Override
    protected void callInjections() {
        ((AppComponentImpl) MyApplicationTest.getAppComponent()).inject(this);
    }
}
