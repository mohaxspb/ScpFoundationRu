package ru.kuchanov.scp2.ui.dialog;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import timber.log.Timber;


public class ShowSubscriptionsFragmentDialog extends BaseBottomSheetDialogFragment {

    public static final String DONATE_1_MONTH = "donate_1_month";
    public static final String DONATE_2_3MONTH = "donate_2_3month";
    public static final String DONATE_3_6MONTH = "donate_3_6month";
    public static final String DONATE_4_1YEAR = "donate_4_1year";
    public static final String DONATE_5_1MONTH = "donate_5_1month";
    public static final String DONATE_6_3MONTH = "donate_6_3month";
    public static final String DONATE_7_6MONTH = "donate_7_6month";
    public static final String DONATE_8_1YEAR = "donate_8_1year";
    public static final String DONATE_9_1MONTH = "donate_9_1month";


    @BindView(R.id.currentSubscriptionValue)
    TextView currentSubscriptionValue;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    public static ShowSubscriptionsFragmentDialog newInstance() {
        return new ShowSubscriptionsFragmentDialog();
    }

    @Override
    protected void callInjection() {
        MyApplication.getAppComponent().inject(this);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_bottom_sheet_subscriptions;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        //TODO
//        getInfoFromGooglePlay();

    }

//        private void getInfoFromGooglePlay() {
//        final IInAppBillingService iInAppBillingService;
//        if (getActivity() instanceof ActivityMain) {
//            ActivityMain activityMain = (ActivityMain) getActivity();
//            iInAppBillingService = activityMain.getIInAppBillingService();
//        } else {
//            ActivityArticles activityArticles = (ActivityArticles) getActivity();
//            iInAppBillingService = activityArticles.getIInAppBillingService();
//        }
//        if (iInAppBillingService == null) {
//            return;
//        }
//        try {
//            Bundle ownedItems = iInAppBillingService.getPurchases(3, ctx.getPackageName(), "subs", null);
//            int response = ownedItems.getInt("RESPONSE_CODE");
//            if (response == 0) {
//                ArrayList<String> ownedSkus =
//                        ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
//                ArrayList<String> purchaseDataList =
//                        ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
//                ArrayList<String> signatureList =
//                        ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
//               /* String continuationToken =
//                        ownedItems.getString("INAPP_CONTINUATION_TOKEN");*/
//                TextView currentDonate = (TextView) dialogTextSize.getCustomView().findViewById(R.id.current_subscription);
//                for (int i = 0; i < purchaseDataList.size(); ++i) {
//                    String purchaseData = purchaseDataList.get(i);
//                    String signature = signatureList.get(i);
//                    String sku = ownedSkus.get(i);
//                    Timber.d(purchaseData);
//                    Timber.d(sku);
//
//                    // do something with this purchase information
//                    // e.g. display the updated list of products owned by user
//                    if (i == 0) {
//                        currentDonate.setText("");
//                    }
//                    JSONObject object = null;
//                    try {
//                        object = new JSONObject(purchaseData);
//                        final String subscriptionDonateId = object.getString("productId");
////                        final String donatePrice = object.getString("price");
//                        Timber.d(purchaseData);
////                        Log.i(LOG,donatePrice);
//                        switch (subscriptionDonateId) {
//                            case DONATE_1_MONTH:
//                                currentDonate.append("Ежемесечная поддержка\n");
//                                break;
//                            case DONATE_2_3MONTH:
//                                currentDonate.append("Поддержка каждые 3 месяца\n");
//                                break;
//                            case DONATE_3_6MONTH:
//                                currentDonate.append("Поддержка каждые 6 месяцев\n");
//                                break;
//                            case DONATE_4_1YEAR:
//                                currentDonate.append("Ежегодная поддержка\n");
//                                break;
//                            case DONATE_5_1MONTH:
//                                currentDonate.append("Поддержка на 1 месяц\n");
//                                break;
//                            case DONATE_6_3MONTH:
//                                currentDonate.append("Поддержка каждые 3 месяца\n");
//                                break;
//                            case DONATE_7_6MONTH:
//                                currentDonate.append("Поддержка каждые 6 месяцев\n");
//                                break;
//                            case DONATE_8_1YEAR:
//                                currentDonate.append("Ежегодная поддержка\n");
//                                break;
//                            case DONATE_9_1MONTH:
//                                currentDonate.append("Ежемесечная поддержка\n");
//                                break;
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        List<String> skuList = new ArrayList<>();
//        skuList.add(DONATE_1_MONTH);
//        skuList.add(DONATE_2_3MONTH);
//        skuList.add(DONATE_3_6MONTH);
//        skuList.add(DONATE_4_1YEAR);
//        skuList.add(DONATE_5_1MONTH);
//        skuList.add(DONATE_6_3MONTH);
//        skuList.add(DONATE_7_6MONTH);
//        skuList.add(DONATE_8_1YEAR);
//        skuList.add(DONATE_9_1MONTH);
//        Bundle querySkus = new Bundle();
//        querySkus.putStringArrayList("ITEM_ID_LIST", (ArrayList<String>) skuList);
//        try {
//            Bundle skuDetails = iInAppBillingService.getSkuDetails(3, getActivity().getPackageName(), "subs", querySkus);
//            int response = skuDetails.getInt("RESPONSE_CODE");
//            if (response == 0) {
//                ArrayList<String> responseList
//                        = skuDetails.getStringArrayList("DETAILS_LIST");
//                LinearLayout linearLayout = (LinearLayout) dialogTextSize.getCustomView().findViewById(R.id.avaible_subscription);
//
//                for (String thisResponse : responseList) {
//                    JSONObject object = new JSONObject(thisResponse);
//                    final String sku = object.getString("productId");
//                    String price = object.getString("price");
//                    Timber.d("id,price: " + sku + " " + price);
//                    Timber.d(thisResponse);
//                    TextView textView = new TextView(getActivity());
//                    textView.setText(object.getString("title").replace("(SCP Foundation RU On/Off-line)", "") + " - " + price);
//                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
//                    linearLayout.addView(textView);
//                    textView.setOnClickListener(v -> {
//                        try {
//                            Bundle buyIntentBundle = iInAppBillingService.getBuyIntent(
//                                    3,
//                                    getActivity().getPackageName(),
//                                    sku,
//                                    "subs",
//                                    String.valueOf(System.currentTimeMillis()));
//                            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
//                            if (pendingIntent != null) {
//                                getActivity().startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0, 0, 0);
//                            }
//                        } catch (RemoteException | IntentSender.SendIntentException e) {
//                            e.printStackTrace();
//                        }
//                    });
//                }
//            }
//        } catch (RemoteException | JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.d("onActivityResult requestCode: %s, resultCode: %s", requestCode, resultCode);
        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == -1) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    Timber.d("You have bought the " + sku + ". Excellent choice, adventurer!");
                } catch (JSONException e) {
                    Timber.d("Failed to parse purchase data.");
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}