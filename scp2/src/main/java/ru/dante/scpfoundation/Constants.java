package ru.dante.scpfoundation;

/**
 * Created by mohax on 03.01.2017.
 * <p>
 * for scp_ru
 */
public interface Constants {

    interface Api {
        String MOST_RECENT_URL = "/most-recently-created/p/";
        String MOST_RATED_URL = "/top-rated-pages/p/";
        /**
         * first arg is searchQuery with SPACEs replaced by "%20"
         * second - num of page
         */
        String SEARCH_URL = "/search:site/a/p/q/%1$s/p/%2$s";
        String RANDOM_PAGE_SCRIPT_URL = "https://beta.scpfoundation.net/wikidot_random_page";
        int NUM_OF_ARTICLES_ON_RECENT_PAGE = 30;
        int NUM_OF_ARTICLES_ON_RATED_PAGE = 20;
        int ZERO_OFFSET = 0;
        int NUM_OF_ARTICLES_ON_SEARCH_PAGE = 10;
    }

    interface Analitics {
        interface EventType {
            String REWARD_GAINED = "REWARD_GAINED";
            String REWARD_REQUESTED = "REWARD_REQUESTED";
        }

        interface StartScreen {
            String MAIN_TO_ARTICLE_SNACK_BAR = "MAIN_TO_ARTICLE_SNACK_BAR";
            String SNACK_BAR = "SNACK_BAR";
            String MENU = "MENU";
            String DRAWER_HEADER_LOGINED = "DRAWER_HEADER_LOGINED";
            String FONT = "FONT";
        }
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
        String ARCHIVE = "http://scpfoundation.ru/archive";
        String JOKES = "http://scpfoundation.ru/scp-list-j";
    }
}