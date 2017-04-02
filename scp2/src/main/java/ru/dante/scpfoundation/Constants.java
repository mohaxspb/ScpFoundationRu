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
        int GALLERY_VK_GROUP_ID = -98801766;
        int GALLERY_VK_ALBUM_ID = 219430203;
    }

    interface Firebase {

//        @Retention(RetentionPolicy.SOURCE)
//        @StringDef({SocialProvider.VK})
//        @interface SocialProvider {
//            String VK = "vk";
//        }

        enum CallToActionReason{
            REMOVE_ADS, ENABLE_AUTO_SYNC, ENABLE_FONTS
        }

        enum SocialProvider{
            VK
        }

        interface Refs{
            String USERS = "users";
            String FAVORITES = "favorites";
            String ARTICLES = "articles";
        }

        int REQUEST_INVITE = 1024;

        interface RemoteConfigKeys {
            String APPS_TO_INSTALL_JSON = "apps_to_install_json";
            String FREE_INVITES_ENABLED = "free_invites_enabled";
            String FREE_APPS_INSTALL_ENABLED = "free_apps_install_enabled";
            String FREE_REWARDED_VIDEO_ENABLED = "free_rewarded_video_enabled";
            String PERIOD_BETWEEN_INTERSTITIAL_IN_MILLIS = "period_between_interstitial_in_millis";
            String REWARDED_VIDEO_COOLDOWN_IN_MILLIS = "rewarded_video_cooldown_in_millis";
            String NUM_OF_INTERSITIAL_BETWEEN_REWARDED = "num_of_intersitial_between_rewarded";
            String APP_INSTALL_REWARD_IN_MILLIS = "app_install_reward_in_millis";
            String FREE_VK_GROUPS_ENABLED = "free_vk_groups_enabled";
            String FREE_VK_GROUPS_JOIN_REWARD = "free_vk_groups_join_reward";
            String VK_GROUPS_TO_JOIN_JSON = "vk_groups_to_join_json";
            String NUM_OF_SYNC_ATTEMPTS_BEFORE_CALL_TO_ACTION = "num_of_sync_attempts_before_call_to_action";
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
                String AUTO_SYNC = "AUTO_SYNC";
            }

            String INVITED_FIVE_FRIENDS = "INVITED_FIVE_FRIENDS";
            String APP_CRACKED = "APP_CRACKED";
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