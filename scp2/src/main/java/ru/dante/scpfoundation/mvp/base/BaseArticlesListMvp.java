package ru.dante.scpfoundation.mvp.base;

import android.util.Pair;

import ru.dante.scpfoundation.db.model.Article;
import rx.Subscriber;

/**
 * Created by mohax on 09.01.2017.
 * <p>
 * for scp_ru
 */
public interface BaseArticlesListMvp {
    interface View extends BaseListMvp.View {

    }

    interface Presenter<V extends View> extends BaseListMvp.Presenter<V>, BaseArticleActions {
        //TODO think if we need this subscribers
        Subscriber<Pair<String, Long>> getToggleFavoriteSubscriber();

        Subscriber<Pair<String, Boolean>> getToggleReadenSubscriber();

        Subscriber<String> getDeleteArticlesTextSubscriber();

        Subscriber<Article> getDownloadArticleSubscriber();
    }
}