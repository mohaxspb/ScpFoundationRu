package ru.dante.scpfoundation.api.model.firebase;

import java.util.List;

import io.realm.RealmList;
import ru.dante.scpfoundation.db.model.SocialProviderModel;
import ru.dante.scpfoundation.db.model.User;

/**
 * Created by mohax on 26.03.2017.
 * <p>
 * for scp_ru
 * <p>
 * We need it as Realm stores List as RealmList, but firebase uses ArrayList
 * so we need to convert types...
 */
public class FirebaseObjectUser {

    public String uid;
    public String fullName;

    public String avatar;

    public String email;

    public List<SocialProviderModel> socialProviders;

    @Override
    public String toString() {
        return "FirebaseObjectUser{" +
                "uid='" + uid + '\'' +
                ", fullName='" + fullName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", socialProviders=" + socialProviders +
                '}';
    }

    public User toRealmUser() {
        return new User(uid, fullName, avatar, email, new RealmList<SocialProviderModel>() {{
            addAll(socialProviders);
        }});
    }
}