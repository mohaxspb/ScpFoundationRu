package ru.dante.scpfoundation.ui.activity;

import ru.kuchanov.scpcore.ui.activity.LicenceActivity;

/**
 * Created by mohax on 10.07.2017.
 * <p>
 * for ScpFoundationRu
 */
public class LicenceActivityImpl extends LicenceActivity {

    @Override
    protected Class getLaunchActivityClass() {
        return MainActivityImpl.class;
    }
}