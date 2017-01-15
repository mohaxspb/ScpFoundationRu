package ru.dante.scpfoundation.db;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by y.kuchanov on 21.12.16.
 *
 * for scp_ru
 */
public class DbProviderFactory {

    public DbProviderFactory(RealmConfiguration realmConfiguration) {
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public DbProvider getDbProvider() {
        return new DbProvider();
    }
}