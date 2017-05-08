package ru.dante.scpfoundation.db;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.dante.scpfoundation.manager.MyPreferenceManager;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for scp_ru
 */
public class DbProviderFactory {

    private MyPreferenceManager mMyPreferenceManager;

    public DbProviderFactory(RealmConfiguration realmConfiguration, MyPreferenceManager preferenceManager) {
        Realm.setDefaultConfiguration(realmConfiguration);
        mMyPreferenceManager = preferenceManager;
    }

    public DbProvider getDbProvider() {
        return new DbProvider(mMyPreferenceManager);
    }
}