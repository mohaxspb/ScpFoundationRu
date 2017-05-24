package ru.dante.scpfoundation.api;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.dante.scpfoundation.api.model.response.LeaderBoardResponse;
import rx.Observable;

/**
 * Created by mohax on 06.05.2017.
 * <p>
 * for scp_ru
 */
public interface ScpServer {

    @GET("find?wiki=scp-ru")
    Observable<LeaderBoardResponse> getArticlesByTags(@Query("tag") List<String> tags);
}