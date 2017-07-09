package ru.kuchanov.scpcore.mvp.presenter;

import java.util.List;

import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.ArticleTag;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.base.BasePresenter;
import ru.dante.scpfoundation.mvp.contract.TagsSearchMvp;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class TagsSearchFragmentPresenter
        extends BasePresenter<TagsSearchMvp.View>
        implements TagsSearchMvp.Presenter {

    private List<ArticleTag> mTags;

    private boolean alreadyRefreshFromApi;

    public TagsSearchFragmentPresenter(
            MyPreferenceManager myPreferencesManager,
            DbProviderFactory dbProviderFactory,
            ApiClient apiClient
    ) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    public void getTagsFromApi() {
        Timber.d("getTagsFromApi");

        getView().showSwipeProgress(true);

        mApiClient.getTagsFromSite()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(data -> mDbProviderFactory.getDbProvider().saveArticleTags(data))
                .subscribe(
                        data -> {
                            Timber.d("getTagsFromApi onNext: %s", data.size());
                            getView().showSwipeProgress(false);
                        },
                        e -> {
                            Timber.e(e);
                            getView().showSwipeProgress(false);
                            getView().showError(e);
                        }
                );
    }

    @Override
    public void getTagsFromDb() {
        Timber.d("getTagsFromDb");
        mDbProviderFactory.getDbProvider().getArticleTagsAsync()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        tags -> {
                            Timber.d("getTagsFromDb onNext: %s", tags.size());
                            mTags = tags;
                            getView().showAllTags(mTags);
                            if (mTags.isEmpty() && !alreadyRefreshFromApi) {
                                getTagsFromApi();
                            }
                        },
                        e -> {
                            Timber.e(e);
                            getView().showError(e);
                        }
                );
    }

    @Override
    public List<ArticleTag> getTags() {
        return mTags;
    }

    @Override
    public void searchByTags(List<ArticleTag> tags) {
        Timber.d("searchByTags: %s", tags);

        getView().showProgress(true);

        mApiClient.getArticlesByTags(tags)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        tagsSearchResponse -> {
                            Timber.d("tagsSearchResponse: %s", tagsSearchResponse);
                            alreadyRefreshFromApi = true;
                            getView().showProgress(false);

                            if (tagsSearchResponse.isEmpty()) {
                                getView().showMessage(R.string.error_no_search_results);
                            } else {
                                getView().showSearchResults(tagsSearchResponse);
                            }
                        },
                        e -> {
                            Timber.e(e);
                            alreadyRefreshFromApi = true;
                            getView().showProgress(false);
                        }
                );
    }
}