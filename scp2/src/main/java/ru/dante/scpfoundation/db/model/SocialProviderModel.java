package ru.dante.scpfoundation.db.model;

import com.vk.sdk.VKAccessToken;

import java.io.Serializable;

import io.realm.RealmObject;
import ru.dante.scpfoundation.Constants;

/**
 * Created by mohax on 25.03.2017.
 * <p>
 * for scp_ru
 */
public class SocialProviderModel extends RealmObject implements Serializable {

    public static final String FIELD_PROVIDER = "provider";
    public static final String FIELD_ID = "id";

    public String provider;

    public String id;

    public SocialProviderModel(String provider, String id) {
        this.provider = provider;
        this.id = id;
    }

    public SocialProviderModel() {
    }

    public static SocialProviderModel getSocialProviderModelForProvider(Constants.Firebase.SocialProvider provider) {
        switch (provider) {
            case VK:
                return new SocialProviderModel(provider.name(), VKAccessToken.currentToken().userId);
            case GOOGLE:
                return new SocialProviderModel(provider.name(), VKAccessToken.currentToken().userId);
            default:
                throw new IllegalArgumentException("unexpected provider");
        }
    }

    @Override
    public String toString() {
        return "SocialProviderModel{" +
                "provider='" + provider + '\'' +
                ", id=" + id +
                '}';
    }
}