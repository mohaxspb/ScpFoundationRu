package ru.dante.scpfoundation.di.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import ru.dante.scpfoundation.BuildConfig;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.db.model.SocialProviderModel;
import ru.dante.scpfoundation.db.model.User;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import timber.log.Timber;

/**
 * Created by y.kuchanov on 22.12.16.
 * <p>
 * for scp_ru
 */
@Module
public class StorageModule {

    @Provides
    @NonNull
    @Singleton
    SharedPreferences providesSharedPreferences(@NonNull Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @NonNull
    @Singleton
    MyPreferenceManager providesPreferencesManager(@NonNull Context context, Gson gson) {
        return new MyPreferenceManager(context, gson);
    }

    @Provides
    @NonNull
    @Singleton
    RealmMigration providesRealmMigration() {
        return (realm, oldVersion, newVersion) -> {
            RealmSchema schema = realm.getSchema();

            Timber.d("providesRealmMigration: %s/%s", oldVersion, newVersion);

            for (RealmObjectSchema realmObjectSchema : schema.getAll()) {
                Timber.d("realmObjectSchema: %s", realmObjectSchema.getClassName());
                Timber.d("realmObjectSchema: %s", realmObjectSchema.getFieldNames());
            }

            if (oldVersion == 0) {
                schema.create(SocialProviderModel.class.getSimpleName())
                        .addField(SocialProviderModel.FIELD_PROVIDER, String.class)
                        .addField(SocialProviderModel.FIELD_ID, String.class);

                schema.get(Article.class.getSimpleName())
                        .addField(Article.FIELD_SYNCED, int.class);

                schema.get(User.class.getSimpleName())
                        .addField(User.FIELD_SCORE, int.class)
                        .addField(User.FIELD_UID, String.class)
                        .addField(User.FIELD_EMAIL, String.class)
                        .addRealmListField(User.FIELD_SOCIAL_PROVIDERS, schema.get(SocialProviderModel.class.getSimpleName()))
                        .removeField("firstName")
                        .removeField("lastName")
                        .removeField("network");

                oldVersion++;
            }

            //TODO add new if blocks if schema changed
        };
    }

    @Provides
    @NonNull
    @Singleton
    RealmConfiguration providesRealmConfiguration(@NonNull RealmMigration realmMigration) {
        return new RealmConfiguration.Builder()
                .schemaVersion(BuildConfig.REALM_VERSION)
                .migration(realmMigration)
                .build();
    }

    @Provides
    @NonNull
    @Singleton
    DbProviderFactory providesDbProviderFactory(@NonNull RealmConfiguration configuration) {
        return new DbProviderFactory(configuration);
    }
}