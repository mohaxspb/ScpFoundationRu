package ru.kuchanov.scpcore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import timber.log.Timber;

/**
 * Created by mohax on 15.01.2017.
 * <p>
 * for scp_ru
 */
public class LicenceActivity extends AppCompatActivity {

    public static final String EXTRA_SHOW_ABOUT = "EXTRA_SHOW_ABOUT";

    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.root)
    View mRoot;

    @Inject
    MyPreferenceManager mMyPreferenceManager;

    @OnClick(R.id.accept)
    public void onAcceptClick() {
        mMyPreferenceManager.setLicenceAccepted(true);
        startActivity(new Intent(this, MainActivity.class).putExtra(EXTRA_SHOW_ABOUT, true));
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getAppComponent().inject(this);
        if (mMyPreferenceManager.isLicenceAccepted()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        setContentView(R.layout.activity_license);
        ButterKnife.bind(this);

        try {
            String licence = readFromAssets(this, "licence.txt");
            text.setText(Html.fromHtml(licence));
        } catch (IOException e) {
            Timber.e(e, "error while read licence from file");
            Snackbar.make(mRoot, R.string.error_read_licence, Snackbar.LENGTH_SHORT).show();
        }
    }

    //TODO move to utils
    public static String readFromAssets(Context context, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename), "UTF-8"));

        // do reading, usually loop until end of file reading
        StringBuilder sb = new StringBuilder();
        String mLine = reader.readLine();
        while (mLine != null) {
            sb.append(mLine); // process line
            mLine = reader.readLine();
        }
        reader.close();
        return sb.toString();
    }
}