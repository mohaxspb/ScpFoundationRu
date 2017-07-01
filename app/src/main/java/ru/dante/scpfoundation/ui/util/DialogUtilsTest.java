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
        //TODO
        List<DownloadEntry> downloadEntries = new ArrayList<>();
        downloadEntries.add(new DownloadEntry(R.string.type_all, context.getString(R.string.type_all)));
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
