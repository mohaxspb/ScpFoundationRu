package ru.dante.scpfoundation.db.model;

import org.parceler.Parcel;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.UserRealmProxy;
import io.realm.annotations.Ignore;
import ru.dante.scpfoundation.Constants;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
@Parcel(implementations = {UserRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {User.class})
public class User extends RealmObject {

    public String uid;
    public String fullName;

    public String avatar;

    public String email;

    @Ignore
    public List<Constants.Firebase.SocialProvider> socialNetworks;

    public RealmList<RealmString> socialNetworksRealmList;

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", fullName='" + fullName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", socialNetworks='" + socialNetworks + '\'' +
                ", socialNetworksRealmList='" + socialNetworksRealmList + '\'' +
                '}';
    }
}