package ru.dante.scpfoundation.utils.ads;

public class CheckTimeToAds
{
//    Intent intent;
//    public void setIntent(Intent intent){
//        this.intent=intent;
//    }
//    boolean adLoaded = false;
//    private final static String LOG = CheckTimeToAds.class.getSimpleName() + "/";
//
//    private Context ctx;
//    private SharedPreferences pref;
//    AdRequest.Builder adRequest;
//
//    private InterstitialAd mInterstitialAd;
//
//    public CheckTimeToAds(Context ctx, InterstitialAd mInterstitialAd)
//    {
//        this.ctx = ctx;
//        pref = PreferenceManager.getDefaultSharedPreferences(ctx);
//        this.mInterstitialAd = mInterstitialAd;
//        this.init();
//    }
//
//    public void show()
//    {
//        mInterstitialAd.show();
//    }
//
//    public void requestNewInterstitial()
//    {
//        Log.d(LOG, "requestNewInterstitial");
//        adRequest = new AdRequest.Builder();
//        mInterstitialAd.loadAd(adsTestDevice(ctx));
//    }
//
//    public String MD5(String md5)
//    {
//        try
//        {
//            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
//            byte[] array = md.digest(md5.getBytes());
//            StringBuffer sb = new StringBuffer();
//            for (int i = 0; i < array.length; ++i)
//            {
//                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
//            }
//            return sb.toString();
//        } catch (java.security.NoSuchAlgorithmException e)
//        {
//        }
//        return null;
//    }
//
//    private AdRequest adsTestDevice(Context ctx)
//    {
//        String android_id = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
//        String deviceId = MD5(android_id).toUpperCase();
//        adRequest.addTestDevice(deviceId);
//        AdRequest builded = adRequest.build();
//        boolean isTestDevice = builded.isTestDevice(ctx);
//
//        Log.v(LOG, "is Admob Test Device ? " + deviceId + " " + isTestDevice); //to confirm it worked
//        return builded;
//    }
//
//    public static boolean needToShowAds(Context ctx)
//    {
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
//        return pref.getBoolean(ctx.getString(R.string.pref_key_interstitial_need_to_show), false);
//    }
//
//    public static void starActivityOrShowAds(Context ctx,Intent intent){
//        ActivityMain activityMain = (ActivityMain) ctx;
//        if (CheckTimeToAds.needToShowAds(ctx) && activityMain.getCheckTimeToAds().isAdLoaded())
//        {
//            activityMain.getCheckTimeToAds().setIntent(intent);
//            activityMain.getCheckTimeToAds().show();
//        } else
//        {
//            ctx.startActivity(intent);
//        }
//    }
//    public static boolean isTimeToShowAds(Context ctx)
//    {
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
//        if (pref.getBoolean(ctx.getString(R.string.pref_key_interstitial_need_to_show), false))
//        {
//            return true;
//        } else
//        {
//            int noAdsLimit = pref.getInt(ctx.getString(R.string.pref_key_interstitial_no_ads_limit), 3);
//            int numOfFreeActions = pref.getInt(ctx.getString(R.string.pref_key_interstitial_counter), 0);
//            if (numOfFreeActions >= noAdsLimit)
//            {
//                pref.edit().putBoolean(ctx.getString(R.string.pref_key_interstitial_need_to_show), true).commit();
//                return true;
//            } else
//            {
//                numOfFreeActions++;
//                pref.edit().putInt(ctx.getString(R.string.pref_key_interstitial_counter), numOfFreeActions).commit();
//                return false;
//            }
//        }
//
//    }
//
//    public static void adsShown(Context ctx)
//    {
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
//        pref.edit().putBoolean(ctx.getString(R.string.pref_key_interstitial_need_to_show), false).commit();
//        pref.edit().putInt(ctx.getString(R.string.pref_key_interstitial_counter), 0).commit();
//    }
//
//    private void init()
//    {
//        //TODO set limit to i.e. 3
//        pref.edit().putInt(ctx.getString(R.string.pref_key_interstitial_no_ads_limit), 3).commit();
//        mInterstitialAd = new InterstitialAd(ctx);
//        mInterstitialAd.setAdUnitId(ctx.getResources().getString(R.string.admob_baner_id_interstitial));
//        mInterstitialAd.setAdListener(new AdListener()
//        {
//            @Override
//            public void onAdClosed()
//            {
//                //must reset needToShowAds
//                CheckTimeToAds.adsShown(ctx);
////                ctx.startActivity(new Intent(ctx, ActivityMain.class));
//                ctx.startActivity(intent);
//            }
//
//            public void onAdLeftApplication()
//            {
//                //must reset needToShowAds
//                CheckTimeToAds.adsShown(ctx);
//            }
//
//            @Override
//            public void onAdLoaded()
//            {
//                Log.e(LOG, "onAdLoaded");
////                if (CheckTimeToAds.isTimeToShowAds(ctx))
////                {
////                    mInterstitialAd.show();
//                adLoaded = true;
////                }
//            }
//
//            public void onAdFailedToLoad(int errorCode)
//            {
//                Log.e(LOG, "onAdFailedToLoad with errorCode " + errorCode);
//                //				requestNewInterstitial();
//            }
//
//            // Сохраняет состояние приложения перед переходом к оверлею объявления.
//            @Override
//            public void onAdOpened()
//            {
//                //must reset needToShowAds
//                CheckTimeToAds.adsShown(ctx);
//            }
//        });
//    }
//
//    public boolean isAdLoaded()
//    {
//        return adLoaded;
//    }
}