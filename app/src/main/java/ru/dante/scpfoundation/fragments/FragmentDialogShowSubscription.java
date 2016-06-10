package ru.dante.scpfoundation.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.vending.billing.IInAppBillingService;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.activities.ActivityArticles;
import ru.dante.scpfoundation.activities.ActivityMain;
import ru.dante.scpfoundation.otto.BusProvider;
import ru.dante.scpfoundation.otto.EventServiceConnected;


public class FragmentDialogShowSubscription extends DialogFragment
{
    public final static String LOG = FragmentDialogShowSubscription.class.getSimpleName();
    private SharedPreferences pref;
    private Context ctx;
    public static final String DONATE_1_MONTH = "donate_1_month";
    public static final String DONATE_2_3MONTH = "donate_2_3month";
    public static final String DONATE_3_6MONTH = "donate_3_6month";
    public static final String DONATE_4_1YEAR = "donate_4_1year";
    public static final String DONATE_5_1MONTH = "donate_5_1month";
    public static final String DONATE_6_3MONTH = "donate_6_3month";
    public static final String DONATE_7_6MONTH = "donate_7_6month";
    public static final String DONATE_8_1YEAR = "donate_8_1year";
    public static final String DONATE_9_1MONTH = "donate_9_1month";
    private MaterialDialog dialogTextSize;


    public static FragmentDialogShowSubscription newInstance()
    {
        return new FragmentDialogShowSubscription();
    }

    @Override
    public void onCreate(Bundle savedState)
    {
        super.onCreate(savedState);
        Log.i(LOG, "onCreate");
        this.ctx = this.getActivity();
        this.pref = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    @Subscribe
    public void onServiceConnected(EventServiceConnected eventServiceConnected)
    {
        Log.i(LOG, "onServiceConnected called");
        getInfoFromGooglePlay();
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


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Log.i(LOG, "onCreateDialog");

        MaterialDialog.Builder dialogTextSizeBuilder = new MaterialDialog.Builder(ctx);
        dialogTextSizeBuilder.title("Поддержать проект")
                .positiveText("Закрыть")
                .customView(R.layout.fragment_dialog_subscription, true);

        dialogTextSize = dialogTextSizeBuilder.build();

        View customView = dialogTextSize.getCustomView();

        if (customView == null)
        {
            return dialogTextSize;
        }
        getInfoFromGooglePlay();
        return dialogTextSize;
    }

    private void getInfoFromGooglePlay()
    {
        final IInAppBillingService iInAppBillingService;
        if (getActivity() instanceof ActivityMain)
        {
            ActivityMain activityMain = (ActivityMain) getActivity();
            iInAppBillingService = activityMain.getIInAppBillingService();
        } else
        {
            ActivityArticles activityArticles = (ActivityArticles) getActivity();
            iInAppBillingService = activityArticles.getIInAppBillingService();
        }
        if (iInAppBillingService == null)
        {
            return;
        }
        try
        {
            Bundle ownedItems = iInAppBillingService.getPurchases(3, ctx.getPackageName(), "subs", null);
            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == 0)
            {
                ArrayList<String> ownedSkus =
                        ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                ArrayList<String> purchaseDataList =
                        ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList<String> signatureList =
                        ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
               /* String continuationToken =
                        ownedItems.getString("INAPP_CONTINUATION_TOKEN");*/
                TextView currentDonate = (TextView) dialogTextSize.getCustomView().findViewById(R.id.current_subscription);
                for (int i = 0; i < purchaseDataList.size(); ++i)
                {
                    String purchaseData = purchaseDataList.get(i);
                    String signature = signatureList.get(i);
                    String sku = ownedSkus.get(i);
                    Log.i(LOG, purchaseData);
                    Log.i(LOG, sku);

                    // do something with this purchase information
                    // e.g. display the updated list of products owned by user
                    if (i == 0)
                    {
                        currentDonate.setText("");
                    }
                    JSONObject object = null;
                    try
                    {
                        object = new JSONObject(purchaseData);
                        final String subscriptionDonateId = object.getString("productId");
//                        final String donatePrice = object.getString("price");
                        Log.i(LOG,purchaseData);
//                        Log.i(LOG,donatePrice);
                        switch (subscriptionDonateId)
                        {
                            case DONATE_1_MONTH:
                                currentDonate.append("Ежемесечная поддержка\n");
                                break;
                            case DONATE_2_3MONTH:
                                currentDonate.append("Поддержка каждые 3 месяца\n");
                                break;
                            case DONATE_3_6MONTH:
                                currentDonate.append("Поддержка каждые 6 месяцев\n");
                                break;
                            case DONATE_4_1YEAR:
                                currentDonate.append("Ежегодная поддержка\n");
                                break;
                            case DONATE_5_1MONTH:
                                currentDonate.append("Поддержка на 1 месяц\n");
                                break;
                            case DONATE_6_3MONTH:
                                currentDonate.append("Поддержка каждые 3 месяца\n");
                                break;
                            case DONATE_7_6MONTH:
                                currentDonate.append("Поддержка каждые 6 месяцев\n");
                                break;
                            case DONATE_8_1YEAR:
                                currentDonate.append("Ежегодная поддержка\n");
                                break;
                            case DONATE_9_1MONTH:
                                currentDonate.append("Ежемесечная поддержка\n");
                                break;
                        }
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }

            }
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
        ArrayList<String> skuList = new ArrayList<String>();
        skuList.add(DONATE_1_MONTH);
        skuList.add(DONATE_2_3MONTH);
        skuList.add(DONATE_3_6MONTH);
        skuList.add(DONATE_4_1YEAR);
        skuList.add(DONATE_5_1MONTH);
        skuList.add(DONATE_6_3MONTH);
        skuList.add(DONATE_7_6MONTH);
        skuList.add(DONATE_8_1YEAR);
        skuList.add(DONATE_9_1MONTH);
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
        try
        {
            Bundle skuDetails = iInAppBillingService.getSkuDetails(3, ctx.getPackageName(), "subs", querySkus);
            int response = skuDetails.getInt("RESPONSE_CODE");
            if (response == 0)
            {
                ArrayList<String> responseList
                        = skuDetails.getStringArrayList("DETAILS_LIST");
                LinearLayout linearLayout = (LinearLayout) dialogTextSize.getCustomView().findViewById(R.id.avaible_subscription);

                for (String thisResponse : responseList)
                {
                    JSONObject object = new JSONObject(thisResponse);
                    final String sku = object.getString("productId");
                    String price = object.getString("price");
                    Log.i(LOG, "id,price: " + sku + " " + price);
                    Log.i(LOG, thisResponse);
                    TextView textView = new TextView(ctx);
                    textView.setText(object.getString("title").replace("(SCP Foundation RU On/Off-line)","")+" - "+price);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
                    linearLayout.addView(textView);
                    textView.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            try
                            {
                                Bundle buyIntentBundle = iInAppBillingService.getBuyIntent(
                                        3,
                                        ctx.getPackageName(),
                                        sku,
                                        "subs",
                                        String.valueOf(System.currentTimeMillis()));
                                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                                if (pendingIntent != null)
                                {
                                    getActivity().startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0, 0, 0);
                                }
                            } catch (RemoteException e)
                            {
                                e.printStackTrace();
                            } catch (IntentSender.SendIntentException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        } catch (RemoteException e)
        {
            e.printStackTrace();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(LOG, "cold in fragment");
        if (requestCode == 1001)
        {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == -1)
            {
                try
                {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    Log.i(LOG, "You have bought the " + sku + ". Excellent choice, adventurer!");
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