package ru.dante.scpfoundation.utils.licensingfuckup;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;

import ru.dante.scpfoundation.Article;
import ru.dante.scpfoundation.Const;
import ru.dante.scpfoundation.R;

/**
 * Created for My Application by Dante on 10.03.2016  23:37.
 */
public class LicenseUtils
{
    private static final String LOG = LicenseUtils.class.getSimpleName();

    public static void writeLicenseInfoToPref(Context ctx)
    {
        SharedPreferences prefRu = ctx.getSharedPreferences(ctx.getString(R.string.pref_authors_ru), Context.MODE_PRIVATE);
        SharedPreferences.Editor editorRU = prefRu.edit();
        String[] authorRuInfo = ctx.getResources().getStringArray(R.array.author_ru);
        for (String authorRu : authorRuInfo)
        {
            String[] urlTitleAuthor = authorRu.split(Const.DIVIDER);
            editorRU.putString(urlTitleAuthor[0], urlTitleAuthor[1] + Const.DIVIDER + urlTitleAuthor[2]);
        }
        editorRU.commit();
        /*Original Authors*/
        SharedPreferences prefEng = ctx.getSharedPreferences(ctx.getString(R.string.pref_authors_eng), Context.MODE_PRIVATE);
        SharedPreferences.Editor editorENG = prefEng.edit();
        String[] authorEngInfo = ctx.getResources().getStringArray(R.array.author_eng);
        for (String authorEng : authorEngInfo)
        {
            String[] titleAuthor = authorEng.split(Const.DIVIDER);
            editorENG.putString(titleAuthor[0], titleAuthor[1]);
        }
        editorENG.commit();
        /*Translate Authors*/
        SharedPreferences prefTranslate = ctx.getSharedPreferences(ctx.getString(R.string.pref_translate), Context.MODE_PRIVATE);
        SharedPreferences.Editor editorTranslate = prefTranslate.edit();
        String[] authorTranslateInfo = ctx.getResources().getStringArray(R.array.author_translate);
        for (String authorTranslae : authorTranslateInfo)
        {
            String[] urlAuthor = authorTranslae.split(Const.DIVIDER);
            editorTranslate.putString(urlAuthor[0], urlAuthor[1]);
        }
        editorTranslate.commit();
    }

    public static void addTranslateAuthors(Context ctx, ArrayList<Article> articles)
    {
    /*Translate Authors*/
        Log.i(LOG, "addTranslateAuthors called");
        SharedPreferences prefTranslate = ctx.getSharedPreferences(ctx.getString(R.string.pref_translate), Context.MODE_PRIVATE);
        SharedPreferences.Editor editorTranslate = prefTranslate.edit();
        int counter = 0;
        for (Article article : articles)
        {
            if (!prefTranslate.contains(article.getURL()))
            {
                counter++;
                editorTranslate.putString(article.getURL(), article.getAuthorName());
            }
        }
        Log.i(LOG, "counter: " + counter);
        editorTranslate.commit();
    }

    public static String getAuthorByUrlOrTitle(Context ctx, String url, String title)
    {
        String author = "не найден";
        SharedPreferences prefEng = ctx.getSharedPreferences(ctx.getString(R.string.pref_authors_eng), Context.MODE_PRIVATE);
        SharedPreferences prefTranslate = ctx.getSharedPreferences(ctx.getString(R.string.pref_translate), Context.MODE_PRIVATE);
        SharedPreferences prefRu = ctx.getSharedPreferences(ctx.getString(R.string.pref_authors_ru), Context.MODE_PRIVATE);

        for (String key : prefRu.getAll().keySet())
        {
            if (url.equals(key))
            {
                String[] titleAuthor = prefRu.getString(key, "").split(Const.DIVIDER);
                author = "Автор статьи: " + titleAuthor[1];
                return author;
            }
        }
        for (String key : prefTranslate.getAll().keySet())
        {
            if (url.equals(key))
            {
                for (String keyEng : prefEng.getAll().keySet())
                {
                    if (title.contains(keyEng))
                    {
                        author = "Автор статьи: " + prefEng.getString(keyEng, "") + ", автор перевода: " + prefTranslate.getString(key, "");
                        return author;
                    }
                }
                author = "Автор: " + prefTranslate.getString(key, "");
                return author;
            }

        }
        return author;
    }

}
