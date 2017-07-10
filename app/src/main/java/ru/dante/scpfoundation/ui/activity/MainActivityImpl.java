package ru.dante.scpfoundation.ui.activity;

import ru.dante.scpfoundation.AppComponentImpl;
import ru.dante.scpfoundation.MyApplicationTest;
import ru.kuchanov.scpcore.ui.activity.MainActivity;

/**
 * Created by mohax on 10.07.2017.
 * <p>
 * for ScpFoundationRu
 */
public class MainActivityImpl extends MainActivity {
    @Override
    protected void callInjections() {
        ((AppComponentImpl) MyApplicationTest.getAppComponent()).inject(this);
    }
}
