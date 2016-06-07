package ru.dante.scpfoundation.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.yandex.metrica.YandexMetrica;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

import ru.dante.scpfoundation.Const;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.fragments.FragmentArticle;
import ru.dante.scpfoundation.fragments.FragmentDialogTextAppearance;
import ru.dante.scpfoundation.otto.BusProvider;
import ru.dante.scpfoundation.otto.EventServiceConnected;
import ru.dante.scpfoundation.utils.RandomPage;
import ru.dante.scpfoundation.utils.SetTextViewHTML;
import ru.dante.scpfoundation.utils.VKUtils;
import ru.dante.scpfoundation.utils.inapp.SubscriptionHelper;

public class ActivityArticles extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private String needToShowDialog;
    public static final String KEY_NEED_TO_SHOW_DIALOG = "KEY_NEED_TO_SHOW_DIALOG";
    private NavigationView navigationView;
    private Context ctx;
    private IInAppBillingService mService;

    // FOR NAVIGATION VIEW ITEM TEXT COLOR
    int[][] state = new int[][]{

//            (Цвет текста всех "включеных" элементов бокового меню )
//            (если этот массив не закоментировать, то остальные массивы работать не будут)
//            new int[] {android.R.attr.state_enabled}, // enabled

            new int[]{android.R.attr.state_checked}, // checked
            new int[]{android.R.attr.state_pressed},  // pressed
            new int[]{}

    };

    int[] color = new int[]{

//            Color.GREEN,
            Color.parseColor("#724646"),
            Color.parseColor("#724646"),
            Color.parseColor("#724646")
    };

    ColorStateList csl = new ColorStateList(state, color);
    int[] colorDark = new int[]{

//            Color.GREEN,
            Color.parseColor("#ECEFF1"),
            Color.parseColor("#ECEFF1"),
            Color.parseColor("#ECEFF1")
    };

    ColorStateList cslDark = new ColorStateList(state, colorDark);

    //    // FOR NAVIGATION VIEW ITEM ICON COLOR
    int[][] stateIcon = new int[][]{

//            (Цвет текста всех "включеных" элементов бокового меню )
//            (если этот массив не закоментировать, то остальные массивы работать не будут)
//            new int[] {android.R.attr.state_enabled}, // enabled

            new int[]{android.R.attr.state_checked}, // checked
            new int[]{android.R.attr.state_pressed},  // pressed
            new int[]{}

    };

    int[] colorIcon = new int[]{

//            Color.GREEN,
            Color.parseColor("#724646"),
            Color.parseColor("#724646"),
            Color.parseColor("#724646")
    };
    ColorStateList csIcon = new ColorStateList(stateIcon, colorIcon);

    int[] colorIconDark = new int[]{

//            Color.GREEN,
            Color.parseColor("#ECEFF1"),
            Color.parseColor("#ECEFF1"),
            Color.parseColor("#ECEFF1")
    };

    ColorStateList csIconDark = new ColorStateList(stateIcon, colorIconDark);
    DrawerLayout drawerLayout;
    SharedPreferences pref;
    Toolbar toolbar;
    ActionBarDrawerToggle mDrawerToggle;

    public IInAppBillingService getIInAppBillingService()
    {
        return mService;
    }

    ServiceConnection mServiceConn = new ServiceConnection()
    {
        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            mService = IInAppBillingService.Stub.asInterface(service);
            BusProvider.getInstance().post(new EventServiceConnected());
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_NEED_TO_SHOW_DIALOG, needToShowDialog);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>()
        {
            @Override
            public void onResult(VKAccessToken res)
            {
                // Пользователь успешно авторизовался
                VKUtils.checkVKAuth((AppCompatActivity) ctx, navigationView);
            }

            @Override
            public void onError(VKError error)
            {
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
                VKUtils.checkVKAuth((AppCompatActivity) ctx, navigationView);
            }
        }))
        {
            if (requestCode == 1001)
            {
                int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

                if (resultCode == RESULT_OK)
                {
                    try
                    {
                        JSONObject jo = new JSONObject(purchaseData);
                        String sku = jo.getString("productId");
                        Log.i(LOG, "You have bought the " + sku + ". Excellent choice, adventurer!");
                        Toast.makeText(ctx, R.string.thanks_for_subscription, Toast.LENGTH_LONG).show();
                    } catch (JSONException e)
                    {
                        Log.i(LOG, "Failed to parse purchase data.");
                        e.printStackTrace();
                    }
                }
            } else
            {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        if (key.equals("key_design_night_mode"))
        {
            recreate();
        }
    }

    //workaround from http://stackoverflow.com/a/30337653/3212712 to show menu icons
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu)
    {
        if (menu != null)
        {
            if (menu.getClass().getSimpleName().equals("MenuBuilder"))
            {
                try
                {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e)
                {
                    Log.e(getClass().getSimpleName(), "onMenuOpened...unable to set icons for overflow menu", e);
                }
            }

            boolean nightModeIsOn = this.pref.getBoolean("key_design_night_mode", false);
            MenuItem themeMenuItem = menu.findItem(R.id.night_mode_item);
            if (nightModeIsOn)
            {
                themeMenuItem.setIcon(R.drawable.ic_brightness_5_white_48dp);
                themeMenuItem.setTitle("Дневной режим");
            } else
            {
                themeMenuItem.setIcon(R.drawable.ic_brightness_3_white_48dp);
                themeMenuItem.setTitle("Ночной режим");
            }

        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d(LOG, "onOptionsItemSelected");
        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.night_mode_item:
                boolean nightModeOn = pref.getBoolean("key_design_night_mode", false);
                pref.edit().putBoolean("key_design_night_mode", !nightModeOn).apply();
                recreate();
                return true;
            case R.id.text_size:
                Log.d(LOG, "text press");
                FragmentDialogTextAppearance fragmentDialogTextAppearance = FragmentDialogTextAppearance.newInstance();
                fragmentDialogTextAppearance.show(getFragmentManager(), "ХЗ");
                return true;
            case R.id.settings:
                Intent intent = new Intent(ctx, ActivitySettings.class);
                ctx.startActivity(intent);
                return true;
            case R.id.subscribe:
                SubscriptionHelper.showSubscriptionDialog(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

//    public static final String LOG=ActivityArticles.class.getSimpleName();

    private static final String LOG = ActivityArticles.class.getSimpleName();

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
//        unregisterReceiver(broadcastReceiver);
        if (mService != null)
        {
            unbindService(mServiceConn);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ctx = this;
        RandomPage.getRandomPage(ctx);
        PreferenceManager.setDefaultValues(this, R.xml.pref_design, true);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.registerOnSharedPreferenceChangeListener(this);
        boolean nightModeOn = pref.getBoolean("key_design_night_mode", false);
        if (nightModeOn)
        {
            setTheme(R.style.SCP_Theme_Dark);
        } else
        {
            setTheme(R.style.SCP_Theme_Light);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);

        if (savedInstanceState != null)
        {
            needToShowDialog = savedInstanceState.getString(KEY_NEED_TO_SHOW_DIALOG, null);
        }
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        linearLayout = (LinearLayout) findViewById(R.id.content_frame);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayShowTitleEnabled(false);

            actionBar.setDisplayHomeAsUpEnabled(true);

            mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name)
            {
                public void onDrawerClosed(View view)
                {
                    supportInvalidateOptionsMenu();
                }

                public void onDrawerOpened(View drawerView)
                {
                }
            };
            mDrawerToggle.setDrawerIndicatorEnabled(false);

            drawerLayout.setDrawerListener(mDrawerToggle);
        }

        String title = getIntent().getExtras().getString("title");
        String url = getIntent().getExtras().getString("url");
        Log.d(LOG, title + " = " + url);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentById(R.id.content_frame);
        if (fragment == null)
        {
            fragment = FragmentArticle.newInstance(url, title);
            fragmentTransaction.add(R.id.content_frame, fragment);
            fragmentTransaction.commit();
        } else
        {
            Log.d(LOG, "Fragment Already Exists");
        }

        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.news:
                        ActivityMain.startActivityMain(Const.Urls.NEWS, ActivityArticles.this);
                        break;
                    case R.id.new_articles:
                        ActivityMain.startActivityMain(Const.Urls.NEW_ARTICLES, ActivityArticles.this);
                        break;
                    case R.id.about:
                        ActivityMain.startActivityMain(Const.Urls.ABOUT_SCP, ActivityArticles.this);
                        break;
                    case R.id.objects_I:
                        ActivityMain.startActivityMain(Const.Urls.OBJECTS_1, ActivityArticles.this);
                        break;
                    case R.id.objects_II:
                        ActivityMain.startActivityMain(Const.Urls.OBJECTS_2, ActivityArticles.this);
                        break;
                    case R.id.objects_III:
                        ActivityMain.startActivityMain(Const.Urls.OBJECTS_3, ActivityArticles.this);
                        break;
                    case R.id.objects_RU:
                        ActivityMain.startActivityMain(Const.Urls.OBJECTS_RU, ActivityArticles.this);
                        break;
                    case R.id.favorite:
                        ActivityMain.startActivityMain(Const.Urls.FAVORITES, ActivityArticles.this);
                        break;
                    case R.id.offline:
                        ActivityMain.startActivityMain(Const.Urls.OFFLINE, ActivityArticles.this);
                        break;
                    case R.id.files:
                        ActivityMain.startActivityMain(Const.Urls.MATERIALS_ALL, ActivityArticles.this);
                        break;
                    case R.id.stories:
                        ActivityMain.startActivityMain(Const.Urls.STORIES, ActivityArticles.this);
                        break;
                    case R.id.gallery:
                        Intent galleryIntent = new Intent(ctx, ActivityGallery.class);
                        ctx.startActivity(galleryIntent);
                        break;
                    case R.id.random_page:
                        if (pref.contains(ctx.getString(R.string.pref_key_random_url)))
                        {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            Fragment fragment = FragmentArticle.newInstance(pref.getString(ctx.getString(R.string.pref_key_random_url), ""), "");
                            fragmentTransaction.replace(R.id.content_frame, fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();

                            pref.edit().remove(ctx.getString(R.string.pref_key_random_url)).apply();
                            RandomPage.getRandomPage(ctx);
                        } else
                        {
                            RandomPage.getRandomPage(ctx);
                            Toast.makeText(ctx, "Создаю случайную статью,нажмите еще раз", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
//                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });

        if (nightModeOn)
        {
            navigationView.setItemTextColor(cslDark);
            navigationView.setItemIconTintList(csIconDark);
        } else
        {
            navigationView.setItemTextColor(csl);
            navigationView.setItemIconTintList(csIcon);
        }
        disableNavigationViewScrollbars(navigationView);
        navigationView.getMenu().setGroupCheckable(0, false, true);

        /*посылка intant раз в час для закрытия активити*/
       /* final AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        final Intent intentToCloseActivity = new Intent(Const.INTENT_ACTION_CLOSE_ACTIVITY);
        boolean alarmfinishActivityUp = (PendingIntent.getBroadcast(this.getApplicationContext(), 0, intentToCloseActivity,
                PendingIntent.FLAG_NO_CREATE) != null);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intentToCloseActivity,
                PendingIntent.FLAG_UPDATE_CURRENT);
        if (!alarmfinishActivityUp)
        {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            {
                am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR, AlarmManager.INTERVAL_HOUR, pendingIntent);
            } else
            {
                am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR, AlarmManager.INTERVAL_HOUR, pendingIntent);
            }
        }
        broadcastReceiver = new BroadcastReceiver()
        {

            @Override
            public void onReceive(Context context, Intent intent)
            {
                Log.d(LOG, "finish");
                if (!isActive)
                {
                    PendingIntent pendingIntentFinishActivityCancel = PendingIntent.getBroadcast(getApplicationContext(), 0,
                            intentToCloseActivity,
                            PendingIntent.FLAG_CANCEL_CURRENT);
                    am.cancel(pendingIntentFinishActivityCancel);
                    finish();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(LOG);
        this.registerReceiver(broadcastReceiver, intentFilter);*/

        VKUtils.checkVKAuth((AppCompatActivity) ctx, navigationView);

        /*in app покупки*/
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

  /*  boolean isActive = true;
    BroadcastReceiver broadcastReceiver;*/


    @Override
    protected void onPause()
    {
        YandexMetrica.onPauseActivity(this);
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        YandexMetrica.onResumeActivity(this);

        //restoreDiLOG QITH IMAGE
        if (needToShowDialog != null)
        {
            Log.d(LOG, "needToShowDialog != null");
            SetTextViewHTML.showImageDialog(ctx, needToShowDialog);
        }
    }

    private void disableNavigationViewScrollbars(NavigationView navigationView)
    {
        if (navigationView != null)
        {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null)
            {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    public void setNeedToShowDialog(String needToShowDialog)
    {
        Log.d(LOG, "setNeedToShowDialog: " + needToShowDialog);
        this.needToShowDialog = needToShowDialog;
    }
}