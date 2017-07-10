package ru.dante.scpfoundation.ui.activity;

import android.content.Intent;

import butterknife.OnClick;
import ru.kuchanov.scpcore.R2;
import ru.kuchanov.scpcore.ui.activity.LicenceActivity;
import ru.kuchanov.scpcore.ui.activity.MainActivity;

/**
 * Created by mohax on 10.07.2017.
 * <p>
 * for ScpFoundationRu
 */
public class LicenceActivityImpl extends LicenceActivity {

    @Override
    @OnClick(R2.id.accept)
    public void onAcceptClick() {
        mMyPreferenceManager.setLicenceAccepted(true);
        startActivity(new Intent(this, getLaunchActivityClass()).putExtra(EXTRA_SHOW_ABOUT, true));
        finish();
    }

    @Override
    protected Class getLaunchActivityClass() {
        return MainActivityImpl.class;
    }
}