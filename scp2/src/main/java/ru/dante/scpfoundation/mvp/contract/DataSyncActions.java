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
    }

    void updateArticleInFirebase(Article article, boolean showResultMessage);

    void syncArticles(boolean showResultMessage);

    void updateUserScoreFromAction(@ScoreAction String action);
}