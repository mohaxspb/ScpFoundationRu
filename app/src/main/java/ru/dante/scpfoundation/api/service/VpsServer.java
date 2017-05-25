package ru.dante.scpfoundation.api.service;

import retrofit2.http.GET;
import ru.dante.scpfoundation.api.model.response.LeaderBoardResponse;
import rx.Observable;

/**
 * Created by mohax on 06.05.2017.
 * <p>
 * for scp_ru
 */
public interface VpsServer {

    @GET("LeaderBoard")
    Observable<LeaderBoardResponse> getLeaderboard();
}