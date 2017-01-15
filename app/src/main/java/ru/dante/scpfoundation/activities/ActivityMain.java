package ru.dante.scpfoundation.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.vending.billing.IInAppBillingService;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.yandex.metrica.YandexMetrica;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;

import ru.dante.scpfoundation.Const;
import ru.dante.scpfoundation.NavigationItemSelectedListenerMain;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.fragments.FragmentDialogShowSubscription;
import ru.dante.scpfoundation.fragments.FragmentDialogTextAppearance;
import ru.dante.scpfoundation.fragments.FragmentMaterials;
import ru.dante.scpfoundation.fragments.FragmentMaterialsAll;
import ru.dante.scpfoundation.otto.BusProvider;
import ru.dante.scpfoundation.otto.EventServiceConnected;
import ru.dante.scpfoundation.utils.NotificationUtils;
import ru.dante.scpfoundation.utils.RandomPage;
import ru.dante.scpfoundation.utils.SetTextViewHTML;
import ru.dante.scpfoundation.utils.VKUtils;
import ru.dante.scpfoundation.utils.instaleng.AppInstall;
import ru.dante.scpfoundation.utils.prerate.PreRate;
import ru.dante.scpfoundation.utils.wantmoney.GiveMeMoney;

public class ActivityMain extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private String needToShowDialog;
    public static final String KEY_NEED_TO_SHOW_DIALOG = "KEY_NEED_TO_SHOW_DIALOG";
    private final static String KEY_LIST_OF_DRAWLER_MENU_PRESSED_ID = "KEY_LIST_OF_DRAWLER_MENU_PRESSED_ID";
    private ArrayList<Integer> listOfDrawerMenuPressedIds = new ArrayList<>();
    public static final String KEY_URL = "KEY_URL";
    private Context ctx;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private SharedPreferences pref;
    private IInAppBillingService mService;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(KEY_LIST_OF_DRAWLER_MENU_PRESSED_ID, listOfDrawerMenuPressedIds);
        outState.putString(KEY_NEED_TO_SHOW_DIALOG, needToShowDialog);
    }

    public IInAppBillingService getIInAppBillingService() {
        return mService;
    }

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(LOG, "onServiceDisconnected");
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(LOG, "onServiceConnected");
            mService = IInAppBillingService.Stub.asInterface(service);
            BusProvider.getInstance().post(new EventServiceConnected());
            GiveMeMoney.init(ActivityMain.this).showIfNeed(mService);
        }
    };

    public void addIdtoToListOfDrawerMenuPressedIds(Integer id) {
        listOfDrawerMenuPressedIds.add(id);
    }

    public static void startActivityMain(String url, Context ctx) {
        Intent intent = new Intent(ctx, ActivityMain.class);
        intent.putExtra(KEY_URL, url);
        ctx.startActivity(intent);
    }

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
    ////////////

    private static final String LOG = ActivityMain.class.getSimpleName();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void uncheckedAllNavigationItems(NavigationView navigationView) {
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(this, R.xml.pref_design, true);
        PreferenceManager.setDefaultValues(this, R.xml.pref_notification, true);
        PreferenceManager.setDefaultValues(this, R.xml.pref_system, true);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.registerOnSharedPreferenceChangeListener(this);
        pref.edit().putInt(getResources().getString(R.string.key_new_articles_counter), 0).apply();
        boolean nightModeOn = pref.getBoolean("key_design_night_mode", false);
        if (nightModeOn) {
            setTheme(R.style.SCP_Theme_Dark);
        } else {
            setTheme(R.style.SCP_Theme_Light);
        }
        super.onCreate(savedInstanceState);
        ctx = this;
        RandomPage.getRandomPage(ctx);
        if (savedInstanceState != null) {
            listOfDrawerMenuPressedIds = savedInstanceState.getIntegerArrayList(KEY_LIST_OF_DRAWLER_MENU_PRESSED_ID);
            needToShowDialog = savedInstanceState.getString(KEY_NEED_TO_SHOW_DIALOG, null);
        }

        setContentView(R.layout.activity_main);
//        Log.d("fuck yeah", "message");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);

            mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name) {
                public void onDrawerClosed(View view) {
                    supportInvalidateOptionsMenu();
                }

                public void onDrawerOpened(View drawerView) {
                }
            };
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            drawerLayout.setDrawerListener(mDrawerToggle);
        }

        navigationView = (NavigationView) findViewById(R.id.navigation);
        if (nightModeOn) {
            navigationView.setItemTextColor(cslDark);
            navigationView.setItemIconTintList(csIconDark);
        } else {
            navigationView.setItemTextColor(csl);
            navigationView.setItemIconTintList(csIcon);
        }
        NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new NavigationItemSelectedListenerMain(this);
        navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);

        disableNavigationViewScrollbars(navigationView);

        if (getIntent().hasExtra(KEY_URL)) {
            String url = getIntent().getStringExtra(KEY_URL);
            Fragment fragment;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            switch (url) {
                case Const.Urls.ABOUT_SCP:
                    onNavigationItemSelectedListener.onNavigationItemSelected(navigationView.getMenu().findItem(R.id.about));
                    break;
                case Const.Urls.NEWS:
                    onNavigationItemSelectedListener.onNavigationItemSelected(navigationView.getMenu().findItem(R.id.news));
                    break;
                case Const.Urls.MAIN:
                case Const.Urls.RATE:
                    onNavigationItemSelectedListener.onNavigationItemSelected(navigationView.getMenu().findItem(R.id.rate_articles));
                    break;
                case Const.Urls.NEW_ARTICLES:
                    onNavigationItemSelectedListener.onNavigationItemSelected(navigationView.getMenu().findItem(R.id.new_articles));
                    break;
                case Const.Urls.OBJECTS_1:
                    onNavigationItemSelectedListener.onNavigationItemSelected(navigationView.getMenu().findItem(R.id.objects_I));
                    break;
                case Const.Urls.OBJECTS_2:
                    onNavigationItemSelectedListener.onNavigationItemSelected(navigationView.getMenu().findItem(R.id.objects_II));
                    break;
                case Const.Urls.OBJECTS_3:
                    onNavigationItemSelectedListener.onNavigationItemSelected(navigationView.getMenu().findItem(R.id.objects_III));
                    break;
                case Const.Urls.OBJECTS_RU:
                    onNavigationItemSelectedListener.onNavigationItemSelected(navigationView.getMenu().findItem(R.id.objects_RU));
                    break;
                case Const.Urls.STORIES:
                    onNavigationItemSelectedListener.onNavigationItemSelected(navigationView.getMenu().findItem(R.id.stories));
                    break;
                case Const.Urls.FAVORITES:
                    onNavigationItemSelectedListener.onNavigationItemSelected(navigationView.getMenu().findItem(R.id.favorite));
                    break;
                case Const.Urls.OFFLINE:
                    onNavigationItemSelectedListener.onNavigationItemSelected(navigationView.getMenu().findItem(R.id.offline));
                    break;
                case Const.Urls.MATERIALS_ALL:
                    onNavigationItemSelectedListener.onNavigationItemSelected(navigationView.getMenu().findItem(R.id.files));
                    break;
                case Const.Urls.PROTOCOLS:
                    uncheckedAllNavigationItems(navigationView);
                    fragment = FragmentMaterials.createFragment("http://scpfoundation.ru/experiment-logs");
                    fragmentTransaction.replace(R.id.content_frame, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    break;
                case Const.Urls.INCEDENTS:
                    uncheckedAllNavigationItems(navigationView);
                    fragment = FragmentMaterials.createFragment("http://scpfoundation.ru/incident-reports");
                    fragmentTransaction.replace(R.id.content_frame, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    break;
                case Const.Urls.INTERVIEWS:
                    uncheckedAllNavigationItems(navigationView);
                    fragment = FragmentMaterials.createFragment("http://scpfoundation.ru/eye-witness-interviews");
                    fragmentTransaction.replace(R.id.content_frame, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    break;
                case Const.Urls.OTHERS:
                    uncheckedAllNavigationItems(navigationView);
                    fragment = FragmentMaterials.createFragment("http://scpfoundation.ru/other");
                    fragmentTransaction.replace(R.id.content_frame, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    break;
                case Const.Urls.CANONS:
                    uncheckedAllNavigationItems(navigationView);
                    FragmentMaterialsAll.showAccessDialog(ctx);
                    break;
                case Const.Urls.GOI_HAB:
                    uncheckedAllNavigationItems(navigationView);
                    FragmentMaterialsAll.showAccessDialog(ctx);
                    break;
                case Const.Urls.ART_HUB:
                    uncheckedAllNavigationItems(navigationView);
                    FragmentMaterialsAll.showAccessDialog(ctx);
                    break;
                case Const.Urls.LEAKS:
                    uncheckedAllNavigationItems(navigationView);
                    FragmentMaterialsAll.showAccessDialog(ctx);
                    break;
            }
            getIntent().removeExtra(KEY_URL);
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.content_frame);
            if (fragment == null) {
                onNavigationItemSelectedListener.onNavigationItemSelected(navigationView.getMenu().findItem(R.id.rate_articles));

            } else {
                Log.d(LOG, "Fragment Already Exists");
                toolbar.setTitle(NavigationItemSelectedListenerMain.getTitleById(listOfDrawerMenuPressedIds.get(listOfDrawerMenuPressedIds.size() - 1)));
            }
        }
//Check if autoload alarm is set
        NotificationUtils.checkAlarm(ctx);

        VKUtils.checkVKAuth((AppCompatActivity) ctx, navigationView);

//        in app покупки
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        //restoreDiLOG QITH IMAGE
        if (needToShowDialog != null) {
            SetTextViewHTML.showImageDialog(ctx, needToShowDialog);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(broadcastReceiver);
        PreRate.clearDialogIfOpen();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected void onPause() {
        YandexMetrica.onPauseActivity(this);
//        isActive = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        YandexMetrica.onResumeActivity(this);

//        isActive = true;
        PreRate.init(this, "mohax.spb@gmail.com", "Отзыв по приложению").showIfNeed();
        AppInstall.init(this).showIfNeed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // Пользователь успешно авторизовался
                VKUtils.checkVKAuth((AppCompatActivity) ctx, navigationView);
            }

            @Override
            public void onError(VKError error) {
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
                VKUtils.checkVKAuth((AppCompatActivity) ctx, navigationView);
            }
        })) {
            if (requestCode == 1001) {
                int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

                if (resultCode == RESULT_OK) {
                    try {
                        JSONObject jo = new JSONObject(purchaseData);
                        String sku = jo.getString("productId");
                        Log.i(LOG, "You have bought the " + sku + ". Excellent choice, adventurer!");
                        Toast.makeText(ctx, R.string.thanks_for_subscription, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        Log.i(LOG, "Failed to parse purchase data.");
                        e.printStackTrace();
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("key_design_night_mode")) {
            recreate();
        }
    }

    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }


    //workaround from http://stackoverflow.com/a/30337653/3212712 to show menu icons
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "onMenuOpened...unable to set icons for overflow menu", e);
                }
            }

            boolean nightModeIsOn = this.pref.getBoolean("key_design_night_mode", false);
            MenuItem themeMenuItem = menu.findItem(R.id.night_mode_item);
            if (nightModeIsOn) {
                themeMenuItem.setIcon(R.drawable.ic_brightness_5_white_48dp);
                themeMenuItem.setTitle("Дневной режим");
            } else {
                themeMenuItem.setIcon(R.drawable.ic_brightness_3_white_48dp);
                themeMenuItem.setTitle("Ночной режим");
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG, "onOptionsItemSelected");

        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                this.drawerLayout.openDrawer(GravityCompat.START);
                return true;
           /* case R.id.settings:
                Log.d(LOG, "setting press");
                return true;*/
            case R.id.night_mode_item:
                boolean nightModeOn = pref.getBoolean(ctx.getString(R.string.key_design_night_mode), false);
                pref.edit().putBoolean(ctx.getString(R.string.key_design_night_mode), !nightModeOn).apply();
                recreate();
                return true;
            case R.id.text_size:
                Log.d(LOG, "text press");
                FragmentDialogTextAppearance fragmentDialogTextAppearance = FragmentDialogTextAppearance.newInstance();
                fragmentDialogTextAppearance.show(getFragmentManager(), "ХЗ");
                return true;
            case R.id.info:
                new MaterialDialog.Builder(ctx)
                        .content(R.string.dialog_info_content)
                        .title("О приложении")
                        .show();
                return true;
            case R.id.settings:
                Intent intent = new Intent(ctx, ActivitySettings.class);
                ctx.startActivity(intent);
                return true;
            case R.id.subscribe:
                FragmentDialogShowSubscription fragmentDialogShowSubscription = FragmentDialogShowSubscription.newInstance();
                fragmentDialogShowSubscription.show(getFragmentManager(), FragmentDialogShowSubscription.LOG);
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                listOfDrawerMenuPressedIds.remove(listOfDrawerMenuPressedIds.size() - 1);
                uncheckedAllNavigationItems(navigationView);
                if (listOfDrawerMenuPressedIds.get(listOfDrawerMenuPressedIds.size() - 1) != null) {
                    navigationView.setCheckedItem(listOfDrawerMenuPressedIds.get(listOfDrawerMenuPressedIds.size() - 1));
                    toolbar.setTitle(NavigationItemSelectedListenerMain.getTitleById(listOfDrawerMenuPressedIds.get(listOfDrawerMenuPressedIds.size() - 1)));
                } else {
                    toolbar.setTitle(NavigationItemSelectedListenerMain.getTitleById(listOfDrawerMenuPressedIds.get(listOfDrawerMenuPressedIds.size() - 1)));
                }
                super.onBackPressed();
            } else {
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_HOME);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

                if (pref.getBoolean(getString(R.string.pref_system_close_app), false)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        finishAndRemoveTask();
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            finishAffinity();
                        } else {
                            finish();
                        }
                    }
                    Process.killProcess(Process.myPid());
                }
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void setNeedToShowDialog(String needToShowDialog) {
        this.needToShowDialog = needToShowDialog;
    }

    public ArrayList<Integer> getListOfDrawerMenuPressedIds() {
        return listOfDrawerMenuPressedIds;
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }
}