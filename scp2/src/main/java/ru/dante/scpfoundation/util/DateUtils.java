package ru.dante.scpfoundation.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by mohax on 22.01.2017.
 * <p>
 * for scp_ru
 */
public class DateUtils {

    public static final String SITE_DATE_FORMAT = "HH:mm dd.MM.yyyy";

    public static final String SIMPLE_DATE_FORMAT = "dd.MM.yyyy";

    public static String getArticleDateShortFormat(String rawDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(SITE_DATE_FORMAT, Locale.getDefault());
        Date convertedDate;
        try {
            convertedDate = dateFormat.parse(rawDate);
            SimpleDateFormat sdf = new SimpleDateFormat(SIMPLE_DATE_FORMAT, Locale.getDefault());
            return sdf.format(convertedDate);
        } catch (Exception e) {
            Timber.e(e, "errro while parse date");
            return rawDate;
        }
    }
}
