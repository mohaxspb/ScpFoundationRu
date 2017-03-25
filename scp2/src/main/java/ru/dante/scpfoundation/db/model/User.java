package ru.dante.scpfoundation.db.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class User extends RealmObject {

    public String uid;
    public String fullName;

    public String avatar;

    public String email;

    public RealmList<SocialProviderModel> socialProviders;

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", fullName='" + fullName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", socialProviders='" + socialProviders + '\'' +
                '}';
    }
}