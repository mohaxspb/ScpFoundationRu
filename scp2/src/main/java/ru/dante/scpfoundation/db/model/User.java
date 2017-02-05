package ru.dante.scpfoundation.db.model;

import android.support.annotation.StringDef;

import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import io.realm.ArticleRealmProxy;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.UserRealmProxy;
import io.realm.annotations.PrimaryKey;
import ru.dante.scpfoundation.db.util.RealmStringListParcelConverter;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
@Parcel(implementations = {UserRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {User.class})
public class User extends RealmObject {

    //    @PrimaryKey
//    public String uid;
    public String fullName;

    public String firstName;
    public String lastName;

    public String avatar;

    @NetworkType
    public String network;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            NetworkType.VK,
            NetworkType.FACEBOOK,
            NetworkType.GOOGLE
    })
    public @interface NetworkType {
        String VK = "vk";
        String FACEBOOK = "facebook";
        String GOOGLE = "google";
    }

    @Override
    public String toString() {
        return "User{" +
                "fullName='" + fullName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", network='" + network + '\'' +
                '}';
    }
}