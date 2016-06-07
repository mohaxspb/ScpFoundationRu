package ru.dante.scpfoundation;

/**
 * Created by Dante on 10.02.2016.
 */
public class Const
{
    public static final int DEFAULT_NUM_OF_ARTICLE_OF_PAGE = 30;
    public static final String DIVIDER = "BBPE!!!!!2";
    public static final String DIVIDER_GROUP = "BBPE!!!!!3";
    public static final String INTENT_ACTION_CLOSE_ACTIVITY = "INTENT_ACTION_CLOSE_ACTIVITY";
    public static final String DOMAIN_NAME = "http://scpfoundation.ru";
    public static final int LEVEL_LIMIT=3;

    public static class Urls
    {
        public static final String MAIN = "http://scpfoundation.ru/";
        public static final String NEW_ARTICLES = "http://scpfoundation.ru/most-recently-created";
        public static final String PROTOCOLS = "http://scpfoundation.ru/experiment-logs";
        public static final String INCEDENTS = "http://scpfoundation.ru/incident-reports";
        public static final String INTERVIEWS = "http://scpfoundation.ru/eye-witness-interviews";
        public static final String OTHERS = "http://scpfoundation.ru/other";
        public static final String STORIES = "http://scpfoundation.ru/stories";
        public static final String CANONS = "http://scpfoundation.ru/canon-hub";
        public static final String GOI_HAB = "http://scpfoundation.ru/goi-hub";
        public static final String ART_HUB = "http://scpfoundation.ru/sunnyparallax-artwork-hub";
        public static final String LEAKS = "http://scpfoundation.ru/the-leak";
        public static final String OBJECTS_1 = "http://scpfoundation.ru/scp-list";
        public static final String OBJECTS_2 = "http://scpfoundation.ru/scp-list-2";
        public static final String OBJECTS_3 = "http://scpfoundation.ru/scp-list-3";
        public static final String OBJECTS_RU = "http://scpfoundation.ru/scp-list-ru";
        public static final String ABOUT_SCP = "http://scpfoundation.ru/about-the-scp-foundation";
        public static final String NEWS = "http://scpfoundation.ru/news";
        public static final String[] ALL_LINKS_ARRAY = {MAIN, NEW_ARTICLES, PROTOCOLS, INCEDENTS, INTERVIEWS, OTHERS, STORIES, CANONS, GOI_HAB, ART_HUB, LEAKS, OBJECTS_1, OBJECTS_2, OBJECTS_3, OBJECTS_RU, NEWS};
        public static final String FAVORITES = "FAVORITES";
        public static final String OFFLINE = "OFFLINE";
        public static final String MATERIALS_ALL = "MATERIALS_ALL";
    }
    public static class Favorite{
        public static final String SERVER_DB_FIELD_URLS = "urls";
        public static final String SERVER_DB_FIELD_TITLES = "titles";
        public static final String SERVER_DB_FIELD_VK_ID = "vk_id";
    }
}
