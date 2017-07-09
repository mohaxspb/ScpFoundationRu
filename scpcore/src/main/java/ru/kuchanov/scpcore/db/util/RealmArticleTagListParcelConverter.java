package ru.kuchanov.scpcore.db.util;

import android.os.Parcel;

import org.parceler.Parcels;

import ru.kuchanov.scpcore.db.model.ArticleTag;

/**
 * Created by mohax on 07.01.2017.
 * <p>
 * for scp_ru
 *
 * from https://gist.github.com/cmelchior/72c35fcb55cec33a71e1
 */
// Specific class for a RealmList<Bar> field
public class RealmArticleTagListParcelConverter extends RealmListParcelConverter<ArticleTag> {

    @Override
    public void itemToParcel(ArticleTag input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override
    public ArticleTag itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(ArticleTag.class.getClassLoader()));
    }
}