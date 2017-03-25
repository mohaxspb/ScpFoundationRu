package ru.dante.scpfoundation.db.model;

import io.realm.RealmObject;

/**
 * Created by mohax on 25.03.2017.
 * <p>
 * for scp_ru
 */
public class SocialProviderModel extends RealmObject {

    public String provider;

    public String id;

    public SocialProviderModel(String provider, String id) {
        this.provider = provider;
        this.id = id;
    }

    public SocialProviderModel() {
    }

    @Override
    public String toString() {
        return "SocialProviderModel{" +
                "provider='" + provider + '\'' +
                ", id=" + id +
                '}';
    }
}