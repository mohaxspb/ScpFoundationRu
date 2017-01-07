package ru.kuchanov.scp2;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public interface Constants {

    interface Api {
        String MOST_RECENT_URL = "/most-recently-created/p/";
        String MOST_RATED_URL = "/top-rated-pages/p/";
        int NUM_OF_ARTICLES_ON_RECENT_PAGE = 30;
        int NUM_OF_ARTICLES_ON_RATED_PAGE = 20;
        int ZERO_OFFSET = 0;
    }

    interface Urls {
        String MAIN = "http://scpfoundation.ru/";
        String RATE = "http://scpfoundation.ru/top-rated-pages";
        String NEW_ARTICLES = "http://scpfoundation.ru/most-recently-created";
        String PROTOCOLS = "http://scpfoundation.ru/experiment-logs";
        String INCEDENTS = "http://scpfoundation.ru/incident-reports";
        String INTERVIEWS = "http://scpfoundation.ru/eye-witness-interviews";
        String OTHERS = "http://scpfoundation.ru/other";
        String STORIES = "http://scpfoundation.ru/stories";
        String CANONS = "http://scpfoundation.ru/canon-hub";
        String GOI_HAB = "http://scpfoundation.ru/goi-hub";
        String ART_HUB = "http://scpfoundation.ru/sunnyparallax-artwork-hub";
        String LEAKS = "http://scpfoundation.ru/the-leak";
        String OBJECTS_1 = "http://scpfoundation.ru/scp-list";
        String OBJECTS_2 = "http://scpfoundation.ru/scp-list-2";
        String OBJECTS_3 = "http://scpfoundation.ru/scp-list-3";
        String OBJECTS_RU = "http://scpfoundation.ru/scp-list-ru";
        String ABOUT_SCP = "http://scpfoundation.ru/about-the-scp-foundation";
        String SEARCH = "SEARCH";
        String NEWS = "http://scpfoundation.ru/news";
        String[] ALL_LINKS_ARRAY = {MAIN, RATE, NEW_ARTICLES, PROTOCOLS, INCEDENTS, INTERVIEWS, OTHERS, STORIES, CANONS, GOI_HAB, ART_HUB, LEAKS, OBJECTS_1, OBJECTS_2, OBJECTS_3, OBJECTS_RU, NEWS, SEARCH};
        String FAVORITES = "FAVORITES";
        String OFFLINE = "OFFLINE";
        String MATERIALS_ALL = "MATERIALS_ALL";

    }
}