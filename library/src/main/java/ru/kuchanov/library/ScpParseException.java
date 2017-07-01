package ru.kuchanov.library;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public class ScpParseException extends Throwable {

    private String message;

    public ScpParseException(String s) {
        super(s);
        message = s;
    }

    public String getMessage() {
        return message;
    }
}