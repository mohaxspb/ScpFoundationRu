package ru.dante.scpfoundation.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

import ru.dante.scpfoundation.Const;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.utils.RandomPage;
import ru.dante.scpfoundation.utils.licensingfuckup.LicenseUtils;

/**
 * Created for My Application by Dante on 02.02.2016  18:19.
 */
public class ActivitySplashScreen extends AppCompatActivity {
    private final static String LOG = ActivitySplashScreen.class.getSimpleName();
    ProgressBar progressBar;
    Timer timer = new Timer();
    Context ctx;

    class UpdateBallTask extends TimerTask {
        public void run() {
            progressBar.setProgress(progressBar.getProgress() + 10);
            if (progressBar.getProgress() == progressBar.getMax()) {
                timer.cancel();
                startActivity(new Intent(ActivitySplashScreen.this, ActivityMain.class));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Base_Theme_Light);
        super.onCreate(savedInstanceState);
        ctx = this;
        RandomPage.getRandomPage(ctx);
        /*Первичная запись авторов*/
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        if (!pref.contains(ctx.getString(R.string.authors_exists))) {
            LicenseUtils.writeLicenseInfoToPref(ctx);
            pref.edit().putBoolean(ctx.getString(R.string.authors_exists), true).apply();
        }
        String data = this.getIntent().getDataString();
        Log.d(LOG, "Uri data: " + data);
        if (data != null) {
            for (String pressedLink : Const.Urls.ALL_LINKS_ARRAY) {
                if (data.equals(pressedLink)) {
                    ActivityMain.startActivityMain(data, this);
                    return;
                }
            }
            Intent intent = new Intent(this, ActivityArticles.class);
            Bundle bundle = new Bundle();
            bundle.putString("title", "");
            bundle.putString("url", data);
            intent.putExtras(bundle);
            this.startActivity(intent);
            return;
        }
        setContentView(R.layout.activity_splash_screen);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame);
        frameLayout.setBackgroundResource(R.drawable.cut_splash_screen);
        progressBar = (ProgressBar) findViewById(R.id.progress_Bar);
//        progressBar.setProgressDrawable(new ColorDrawable(ContextCompat.getColor(this,R.color.material_teal_200)));
        progressBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(this, R.color.material_blue_gray_300), PorterDuff.Mode.MULTIPLY);
        progressBar.setMax(300);
        TimerTask updateBall = new UpdateBallTask();
        timer.scheduleAtFixedRate(updateBall, 0, 100);
    }
}