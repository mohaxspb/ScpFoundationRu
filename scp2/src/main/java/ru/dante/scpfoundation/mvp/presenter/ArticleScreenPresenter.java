package ru.dante.scpfoundation.mvp.presenter;

import com.google.firebase.auth.FirebaseAuth;

import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.base.BaseDrawerPresenter;
import ru.dante.scpfoundation.mvp.contract.ArticleScreenMvp;
import timber.log.Timber;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class ArticleScreenPresenter
        extends BaseDrawerPresenter<ArticleScreenMvp.View>
        implements ArticleScreenMvp.Presenter {

    public ArticleScreenPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    public void toggleFavorite(String url) {
        Timber.d("toggleFavorite url: %s", url);
        //TODO seems to that we can move it to ArticlePresenter
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            getView().showNeedLoginPopup();
            return;
        }
        mDbProviderFactory.getDbProvider().toggleFavorite(url)
                .flatMap(article1 -> mDbProviderFactory.getDbProvider().setArticleSynced(article1, false))
                .subscribe(
                        article -> {
                            Timber.d("fav state now is: %s", article);
                            updateArticleInFirebase(article, true);
                        },
                        Timber::e
                );
    }
}