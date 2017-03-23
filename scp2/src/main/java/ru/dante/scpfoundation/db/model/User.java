package ru.dante.scpfoundation.db.model;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.UserRealmProxy;

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

//    public String firstName;
//    public String lastName;

    public String avatar;

    public String email;

//    @NetworkType
    public String network;

//    @Retention(RetentionPolicy.SOURCE)
//    @StringDef({
//            NetworkType.VK,
//            NetworkType.FACEBOOK,
//            NetworkType.GOOGLE
//    })
//    public @interface NetworkType {
//        String VK = "vk";
//        String FACEBOOK = "facebook";
//        String GOOGLE = "google";
//    }


    @Override
    public String toString() {
        return "User{" +
                "fullName='" + fullName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", network='" + network + '\'' +
                '}';
    }
}