package ru.dante.scpfoundation.api.service;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.dante.scpfoundation.api.model.ArticleFromSearchTagsOnSite;
import rx.Observable;

/**
 * Created by mohax on 06.05.2017.
 * <p>
 * for scp_ru
 */
public interface ScpServer {

    @GET("find?wiki=scp-ru")
    Observable<List<ArticleFromSearchTagsOnSite>> getArticlesByTags(@Query("tag") List<String> tags);

    @GET("list?wiki=scp-ru")
    Observable<List<String>> getTagsList();
}