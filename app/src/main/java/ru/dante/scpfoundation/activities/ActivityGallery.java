package ru.dante.scpfoundation.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.veinhorn.scrollgalleryview.MediaInfo;
import com.veinhorn.scrollgalleryview.ScrollGalleryView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import ru.dante.scpfoundation.Const;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.utils.PicassoImageLoader;
import ru.dante.scpfoundation.utils.parsing.DownloadImg;

/**
 * Created for My Application by Dante on 11.03.2016  23:47.
 */
public class ActivityGallery extends AppCompatActivity implements DownloadImg.SetImagInfo, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String KEY_IMG_URLS = "KEY_IMG_URLS";
    private static final String KEY_IMG_DESCRIPTION = "KEY_IMG_DESCRIPTION";

    private Context ctx;
    private ViewPager viewPager;
    private ArrayList<String> imgUrls = new ArrayList<>();
    private ArrayList<String> descriptionImg = new ArrayList<>();

    //workaround from http://stackoverflow.com/a/30337653/3212712 to show menu icons
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "onMenuOpened...unable to set icons for overflow menu", e);
                }
            }
            if (descriptionImg.size() == 0) {
                menu.findItem(R.id.info).setEnabled(false);

            } else {
                menu.findItem(R.id.info).setEnabled(true);
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info:
                int currentPositionViewPager = viewPager.getCurrentItem();
                Spanned dialogText = Html.fromHtml(descriptionImg.get(currentPositionViewPager) + "<br><br><a href=\"http://artscp.com/\">Заказать артбук и календарь</a>");
                new MaterialDialog.Builder(ctx)
                        .title("Информация об изображении")
                        .positiveText("Закрыть")
                        .content(dialogText)
                        .show();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }

    private void setUpGallery() {
        List<MediaInfo> infos = new ArrayList<>(imgUrls.size());
        for (String url : imgUrls) {
            infos.add(MediaInfo.mediaLoader(new PicassoImageLoader(url)));
        }
        ScrollGalleryView scrollGalleryView = (ScrollGalleryView) findViewById(R.id.scroll_gallery_view);
        scrollGalleryView
                .setThumbnailSize(100)
                .setZoom(true)
                .setFragmentManager(getSupportFragmentManager())
                .addMedia(infos)
                .setCurrentItem(0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(KEY_IMG_URLS, imgUrls);
        outState.putStringArrayList(KEY_IMG_DESCRIPTION, descriptionImg);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(this, R.xml.pref_design, true);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.registerOnSharedPreferenceChangeListener(this);
        pref.edit().putInt(getResources().getString(R.string.key_new_articles_counter), 0).apply();
        boolean nightModeOn = pref.getBoolean("key_design_night_mode", false);
        if (nightModeOn) {
            setTheme(R.style.SCP_Theme_Dark);
        } else {
            setTheme(R.style.SCP_Theme_Light);
        }
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_gallery);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);

            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState != null) {
            imgUrls = savedInstanceState.getStringArrayList(KEY_IMG_URLS);
            descriptionImg = savedInstanceState.getStringArrayList(KEY_IMG_DESCRIPTION);
            if (imgUrls.size() != 0) {
                setUpGallery();

            } else {
                DownloadImg downloadImg = new DownloadImg(this, this);
                downloadImg.execute();
            }
        } else {
            DownloadImg downloadImg = new DownloadImg(this, this);
            downloadImg.execute();
        }
    }

    @Override
    public void setImgInfo(ArrayList<String> description) {
        imgUrls.clear();
        descriptionImg.clear();
        for (String info : description) {
            String[] allInfoimg = info.split(Const.DIVIDER);
            imgUrls.add(allInfoimg[0]);
            descriptionImg.add(allInfoimg[1]);
        }
        setUpGallery();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("key_design_night_mode")) {
            recreate();
        }
    }
}