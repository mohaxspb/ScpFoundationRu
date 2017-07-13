package ru.dante.scpfoundation.ui.activity;

import ru.dante.scpfoundation.AppComponentImpl;
import ru.dante.scpfoundation.MyApplicationImpl;
import ru.kuchanov.scpcore.ui.activity.MainActivity;

/**
 * Created by mohax on 10.07.2017.
 * <p>
 * for ScpFoundationRu
 */
public class MainActivityImpl extends MainActivity {

    @Override
    protected void callInjections() {
        ((AppComponentImpl) MyApplicationImpl.getAppComponent()).inject(this);
    }

    @Override
    protected Class getTagsSearchActivityClass() {
        return TagSearchActivityImpl.class;
    }

    @Override
    protected Class getGalleryActivityClass() {
        return GalleryActivityImpl.class;
    }

    @Override
    protected Class getMaterialsActivityClass() {
        return MaterialsActivityImpl.class;
    }

    @Override
    protected Class getArticleActivityClass() {
        return ArticleActivityImpl.class;
    }
}