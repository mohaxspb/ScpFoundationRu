package ru.dante.scpfoundation.mvp.contract;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import ru.dante.scpfoundation.db.model.Article;

/**
 * Created by mohax on 24.03.2017.
 * <p>
 * for scp_ru
 */
public interface DataSyncActions {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ScoreAction.FAVORITE})
    @interface ScoreAction {
        String FAVORITE = "FAVORITE";
        String READ = "READ";
        String INTERSTITIAL_SHOWN = "INTERSTITIAL_SHOWN";
        String VK_GROUP = "VK_GROUP";
        String OUR_APP = "OUR_APP";
        String REWARDED_VIDEO = "REWARDED_VIDEO";
        String NONE = "NONE";
    }

    void updateArticleInFirebase(Article article, boolean showResultMessage);

    void syncData(boolean showResultMessage);

    void updateUserScoreFromAction(@ScoreAction String action);
}