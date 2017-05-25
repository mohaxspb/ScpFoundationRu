package ru.dante.scpfoundation.mvp.presenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public TagsSearchFragmentPresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        super(myPreferencesManager, dbProviderFactory, apiClient);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        getTagsFromApi();

        //FIXME test
//        searchByTags(new ArrayList<>(Arrays.asList(new ArticleTag("ru"), new ArticleTag("ru_en"))));
    }


    @Override
    public void getTagsFromApi() {
        Timber.d("getTagsFromDb");
        //TODO
        mApiClient.getTagsFromSite()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        data -> {
                            Timber.d("data: %s", data.size());
                            getView().showAllTags(data);
                        },
                        e -> {
                            Timber.e(e);
                            getView().showError(e);
                        }
                );
    }

    @Override
    public void getTagsFromDb() {
        Timber.d("getTagsFromDb");
        //TODO
    }

    @Override
    public List<ArticleTag> getTags() {
        return mTags;
    }

    @Override
    public void searchByTags(List<ArticleTag> tags) {
        Timber.d("searchByTags: %s", tags);
        mApiClient.getArticlesByTags(tags)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        tagsSearchResponse -> {
                            Timber.d("tagsSearchResponse: %s", tagsSearchResponse);
                        },
                        e -> {
                            Timber.e(e);
                        }
                );
    }
}