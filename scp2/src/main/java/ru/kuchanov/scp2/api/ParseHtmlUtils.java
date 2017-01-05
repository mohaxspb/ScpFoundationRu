package ru.kuchanov.scp2.api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

/**
 * Created by mohax on 05.01.2017.
 * <p>
 * for scp_ru
 */
public class ParseHtmlUtils {

    public enum TextType {
        Text, Spoiler, Image, Table
    }

    public static ArrayList<String> getArticlesTextParts(String html) {
        ArrayList<String> articlesTextParts = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Element contentPage = document.getElementById("page-content");
        if (contentPage == null) {
            contentPage = document.body();
        }
        for (Element element : contentPage.children()) {
            articlesTextParts.add(element.outerHtml());
        }
        return articlesTextParts;
    }

    public static ArrayList<TextType> getListOfTextTypes(ArrayList<String> articlesTextParts) {
        ArrayList<TextType> listOfTextTypes = new ArrayList<>();
        for (String textPart : articlesTextParts) {

            Element element = Jsoup.parse(textPart);
            Element ourElement = element.getElementsByTag("body").first().children().first();
            if (ourElement == null) {
                listOfTextTypes.add(TextType.Text);
                continue;
            }
            if (ourElement.tagName().equals("p")) {
                listOfTextTypes.add(TextType.Text);
                continue;
            }
            if (ourElement.className().equals("collapsible-block")) {
                listOfTextTypes.add(TextType.Spoiler);
                continue;
            }
            if (ourElement.tagName().equals("table")) {
                listOfTextTypes.add(TextType.Table);
                continue;
            }

            if (ourElement.className().equals("rimg")) {
                listOfTextTypes.add(TextType.Image);
                continue;
            }
            listOfTextTypes.add(TextType.Text);
        }

        return listOfTextTypes;
    }

    public static ArrayList<String> getSpoilerParts(String html) {
        ArrayList<String> spoilerParts = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Element element = document.getElementsByClass("collapsible-block-folded").first();
        Element elementA = element.getElementsByTag("a").first();
        spoilerParts.add(elementA.text());

        Element elementUnfolded = document.getElementsByClass("collapsible-block-unfolded").first();
        Element elementContent = elementUnfolded.getElementsByClass("collapsible-block-content").first();
        spoilerParts.add(elementContent.html());
        return spoilerParts;
    }
}