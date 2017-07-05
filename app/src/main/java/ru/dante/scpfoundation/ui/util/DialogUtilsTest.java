package ru.dante.scpfoundation.ui.util;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ru.dante.scpfoundation.R;
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
public class DialogUtilsTest extends DialogUtils<DownloadAllServiceTest> {

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
        downloadEntries.add(new DownloadEntry(R.string.type_all, context.getString(R.string.type_all)));

        downloadEntries.add(new DownloadEntry(R.string.type_1, context.getString(R.string.type_1)));
        downloadEntries.add(new DownloadEntry(R.string.type_2, context.getString(R.string.type_2)));
        downloadEntries.add(new DownloadEntry(R.string.type_3, context.getString(R.string.type_3)));
        downloadEntries.add(new DownloadEntry(R.string.type_4, context.getString(R.string.type_4)));
        downloadEntries.add(new DownloadEntry(R.string.type_ru, context.getString(R.string.type_ru)));

        downloadEntries.add(new DownloadEntry(R.string.type_experiments, context.getString(R.string.type_experiments)));
        downloadEntries.add(new DownloadEntry(R.string.type_incidents, context.getString(R.string.type_incidents)));
        downloadEntries.add(new DownloadEntry(R.string.type_interviews, context.getString(R.string.type_interviews)));
        downloadEntries.add(new DownloadEntry(R.string.type_jokes, context.getString(R.string.type_jokes)));
        downloadEntries.add(new DownloadEntry(R.string.type_archive, context.getString(R.string.type_archive)));
        downloadEntries.add(new DownloadEntry(R.string.type_other, context.getString(R.string.type_other)));

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
        //TODO
    }
}