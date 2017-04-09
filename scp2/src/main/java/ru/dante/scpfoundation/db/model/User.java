package ru.dante.scpfoundation.db.model;

import java.util.ArrayList;

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

    public int score;

    public RealmList<SocialProviderModel> socialProviders;

    public User(
            String uid,
            String fullName,
            String avatar,
            String email,
            RealmList<SocialProviderModel> socialProviders
    ) {
        this.uid = uid;
        this.fullName = fullName;
        this.avatar = avatar;
        this.email = email;
        this.socialProviders = socialProviders;
    }

    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", fullName='" + fullName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", score=" + score +
                ", socialProviders=" + socialProviders +
                '}';
    }
}