package ru.dante.scpfoundation.ui.util;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.service.DownloadAllServiceTest;
import ru.kuchanov.library.ApiClientModel;
import ru.kuchanov.library.DbProviderFactoryModel;
import ru.kuchanov.library.DialogUtils;
import ru.kuchanov.library.DownloadEntry;
import ru.kuchanov.library.MyPreferenceManagerModel;
import timber.log.Timber;

/**
 * Created by mohax on 01.07.2017.
 * <p>
 * for ScpFoundationRu
 */
public class DialogUtilsTest extends DialogUtils {

    public DialogUtilsTest(
            MyPreferenceManagerModel preferenceManager,
            DbProviderFactoryModel dbProviderFactory,
            ApiClientModel apiClient,
            Class clazz
    ) {
        super(preferenceManager, dbProviderFactory, apiClient, clazz);
    }

    @Override
    public List<DownloadEntry> getDownloadTypesEntries(Context context) {
        List<DownloadEntry> downloadEntries = new ArrayList<>();

        downloadEntries.add(new DownloadEntry(R.string.type_1, context.getString(R.string.type_1), Constants.Urls.OBJECTS_1, Article.FIELD_IS_IN_OBJECTS_1));
        downloadEntries.add(new DownloadEntry(R.string.type_2, context.getString(R.string.type_2), Constants.Urls.OBJECTS_2, Article.FIELD_IS_IN_OBJECTS_2));
        downloadEntries.add(new DownloadEntry(R.string.type_3, context.getString(R.string.type_3), Constants.Urls.OBJECTS_3, Article.FIELD_IS_IN_OBJECTS_3));
        downloadEntries.add(new DownloadEntry(R.string.type_4, context.getString(R.string.type_4), Constants.Urls.OBJECTS_4, Article.FIELD_IS_IN_OBJECTS_4));
        downloadEntries.add(new DownloadEntry(R.string.type_ru, context.getString(R.string.type_ru), Constants.Urls.OBJECTS_RU, Article.FIELD_IS_IN_OBJECTS_RU));

        downloadEntries.add(new DownloadEntry(R.string.type_experiments, context.getString(R.string.type_experiments), Constants.Urls.EXPERIMENTS, Article.FIELD_IS_IN_EXPERIMETS));
        downloadEntries.add(new DownloadEntry(R.string.type_incidents, context.getString(R.string.type_incidents), Constants.Urls.INCEDENTS, Article.FIELD_IS_IN_INCIDENTS));
        downloadEntries.add(new DownloadEntry(R.string.type_interviews, context.getString(R.string.type_interviews), Constants.Urls.INTERVIEWS, Article.FIELD_IS_IN_INTERVIEWS));
        downloadEntries.add(new DownloadEntry(R.string.type_jokes, context.getString(R.string.type_jokes), Constants.Urls.JOKES, Article.FIELD_IS_IN_JOKES));
        downloadEntries.add(new DownloadEntry(R.string.type_archive, context.getString(R.string.type_archive), Constants.Urls.ARCHIVE, Article.FIELD_IS_IN_ARCHIVE));
        downloadEntries.add(new DownloadEntry(R.string.type_other, context.getString(R.string.type_other), Constants.Urls.OTHERS, Article.FIELD_IS_IN_OTHER));

        downloadEntries.add(new DownloadEntry(R.string.type_all, context.getString(R.string.type_all), Constants.Urls.NEW_ARTICLES, Article.FIELD_IS_IN_RECENT));
        return downloadEntries;
    }

    @Override
    protected boolean isServiceRunning() {
        return DownloadAllServiceTest.isRunning();
    }

    @Override
    protected void onIncreaseLimitClick() {
        Timber.d("onIncreaseLimitClick");
        //TODO
    }

    @Override
    protected void logDownloadAttempt(DownloadEntry type) {
        Timber.d("logDownloadAttempt: %s", type);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, Constants.Firebase.Analitics.StartScreen.DOWNLOAD_DIALOG);
        FirebaseAnalytics.getInstance(MyApplication.getAppInstance()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}