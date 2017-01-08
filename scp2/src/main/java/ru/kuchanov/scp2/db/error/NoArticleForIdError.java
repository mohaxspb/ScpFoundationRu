package ru.kuchanov.scp2.db.error;

/**
 * Created by mohax on 08.01.2017.
 * <p>
 * for scp_ru
 */
public class NoArticleForIdError extends Throwable {

    public NoArticleForIdError(String message) {
        super(message);
    }
}