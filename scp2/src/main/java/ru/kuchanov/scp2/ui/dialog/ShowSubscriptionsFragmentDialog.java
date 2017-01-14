package ru.kuchanov.scp2.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import ru.kuchanov.scp2.BuildConfig;
import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.inapp.model.Item;
import ru.kuchanov.scp2.inapp.model.Subscription;
import ru.kuchanov.scp2.manager.InAppBillingServiceConnectionObservable;
import ru.kuchanov.scp2.ui.adapter.RecyclerAdapterSubscriptions;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;


public class ShowSubscriptionsFragmentDialog extends BaseBottomSheetDialogFragment implements RecyclerAdapterSubscriptions.SubscriptionClickListener {

//    public static final String DONATE_1_MONTH = "donate_1_month";
//    public static final String DONATE_2_3MONTH = "donate_2_3month";
//    public static final String DONATE_3_6MONTH = "donate_3_6month";
//    public static final String DONATE_4_1YEAR = "donate_4_1year";
//    public static final String DONATE_5_1MONTH = "donate_5_1month";
//    public static final String DONATE_6_3MONTH = "donate_6_3month";
//    public static final String DONATE_7_6MONTH = "donate_7_6month";
//    public static final String DONATE_8_1YEAR = "donate_8_1year";
//    public static final String DONATE_9_1MONTH = "donate_9_1month";


    @BindView(R.id.progressCenter)
    ProgressBar progressCenter;
    @BindView(R.id.refresh)
    View refresh;
    @BindView(R.id.infoContainer)
    View infoContainer;
    @BindView(R.id.currentSubscriptionValue)
    TextView currentSubscriptionValue;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private IInAppBillingService mInAppBillingService;
    private boolean isDataLoaded;

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

        InAppBillingServiceConnectionObservable.getInstance().getServiceStatusObservable()
                .subscribe(connected -> {
                    if (connected && !isDataLoaded) {
                        getMarketData();
                    }
                });

        getMarketData();
    }

    @OnClick(R.id.refresh)
    void onRefreshClicked() {
        Timber.d("onRefreshClicked");
        getMarketData();
    }

    private void getMarketData() {
        if (!isAdded()) {
            return;
        }
        mInAppBillingService = getBaseActivity().getIInAppBillingService();

        refresh.setVisibility(View.GONE);
        progressCenter.setVisibility(View.VISIBLE);

        Observable.<Pair<List<Item>, List<Subscription>>>create(subscriber -> {
            try {
                Bundle ownedItemsBundle = mInAppBillingService.getPurchases(3, getActivity().getPackageName(), "subs", null);
                Timber.d("ownedItems bundle: %s", ownedItemsBundle);
                int response = ownedItemsBundle.getInt("RESPONSE_CODE");
                if (response == 0) {
                    List<String> ownedSkus = ownedItemsBundle.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                    List<String> purchaseDataList = ownedItemsBundle.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                    List<String> signatureList = ownedItemsBundle.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                    String continuationToken = ownedItemsBundle.getString("INAPP_CONTINUATION_TOKEN");

                    if (ownedSkus == null || purchaseDataList == null || signatureList == null) {
                        subscriber.onError(new IllegalStateException("some of owned items info is null while get owned items"));
                        return;
                    }

                    List<Item> ownedItemsList = new ArrayList<>();
                    for (int i = 0; i < purchaseDataList.size(); ++i) {
                        String purchaseData = purchaseDataList.get(i);
                        String signature = signatureList.get(i);
                        String sku = ownedSkus.get(i);
                        ownedItemsList.add(new Item(purchaseData, signature, sku, continuationToken));
                    }

                    //get all subs deatailed info
                    List<Subscription> allSubscriptions = new ArrayList<>();
                    List<String> skuList = new ArrayList<>();
                    //get it from build config
                    Collections.addAll(skuList, BuildConfig.OLD_SKUS);
                    Collections.addAll(skuList, BuildConfig.VER_2_SKUS);
                    Timber.d("skuList: %s", skuList);

                    Bundle querySkus = new Bundle();
                    querySkus.putStringArrayList("ITEM_ID_LIST", (ArrayList<String>) skuList);
                    Bundle skuDetails = mInAppBillingService.getSkuDetails(3, getActivity().getPackageName(), "subs", querySkus);
                    Timber.d("skuDetails: %s", skuDetails);
                    if (skuDetails.getInt("RESPONSE_CODE") == 0) {
                        List<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                        if (responseList == null) {
                            subscriber.onError(new IllegalStateException("responseList is null while get subs details"));
                            return;
                        }

                        for (String thisResponse : responseList) {
                            JSONObject object = new JSONObject(thisResponse);
                            String sku = object.getString("productId");
                            String price = object.getString("price");
                            String title = object.getString("title");
                            allSubscriptions.add(new Subscription(sku, price, title));
//                            Timber.d(thisResponse);
                        }
                    }

                    subscriber.onNext(new Pair<>(ownedItemsList, allSubscriptions));
                    subscriber.onCompleted();
                }
            } catch (Exception e) {
                Timber.e(e, "error getting cur subs");
                subscriber.onError(e);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        ownedItemsAndSubscriptions -> {
                            if (!isAdded()) {
                                return;
                            }
                            Timber.d("items: %s", ownedItemsAndSubscriptions.first);
                            Timber.d("subs: %s", ownedItemsAndSubscriptions.second);
                            isDataLoaded = true;
                            refresh.setVisibility(View.GONE);
                            progressCenter.setVisibility(View.GONE);
                            infoContainer.setVisibility(View.VISIBLE);
                            if (ownedItemsAndSubscriptions.first.isEmpty()) {
                                currentSubscriptionValue.setText(getString(R.string.no_subscriptions));
                            } else {
                                //TODO show multiple subscriptions
                                currentSubscriptionValue.setText(ownedItemsAndSubscriptions.first.get(0).sku);
                            }
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setHasFixedSize(true);
                            RecyclerAdapterSubscriptions adapter = new RecyclerAdapterSubscriptions();
                            adapter.setArticleClickListener(ShowSubscriptionsFragmentDialog.this);
                            recyclerView.setAdapter(adapter);
                        },
                        error -> {
                            if (!isAdded()) {
                                return;
                            }
                            Timber.e(error, "error getting cur subs");
                            isDataLoaded = false;

                            Snackbar.make(root, error.getMessage(), Snackbar.LENGTH_SHORT).show();
                            progressCenter.setVisibility(View.GONE);
                            refresh.setVisibility(View.VISIBLE);
                        }
                );
    }

    @Override
    public void onSubscriptionClicked(Subscription article) {
        try {
            Bundle buyIntentBundle = mInAppBillingService.getBuyIntent(
                    3,
                    getActivity().getPackageName(),
                    article.productId,
                    "subs",
                    String.valueOf(System.currentTimeMillis()));
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            if (pendingIntent != null) {
                startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0, 0, 0, null);
            }
        } catch (RemoteException | IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.d("called in fragment");
        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == Activity.RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    Timber.d("You have bought the %s", sku);
                } catch (JSONException e) {
                    Timber.e(e, "Failed to parse purchase data.");
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}