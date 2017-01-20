package ru.dante.scpfoundation.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import ru.dante.scpfoundation.BuildConfig;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.manager.InAppBillingServiceConnectionObservable;
import ru.dante.scpfoundation.monetization.model.Item;
import ru.dante.scpfoundation.monetization.model.Subscription;
import ru.dante.scpfoundation.ui.adapter.RecyclerAdapterSubscriptions;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;


public class SubscriptionsFragmentDialog
        extends BaseBottomSheetDialogFragment
        implements RecyclerAdapterSubscriptions.SubscriptionClickListener {

    public static final int REQUEST_CODE_SUBSCRIPTION = 1001;

    @BindView(R.id.progressCenter)
    ProgressBar progressCenter;
    @BindView(R.id.refresh)
    View refresh;
    @BindView(R.id.infoContainer)
    View infoContainer;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private IInAppBillingService mInAppBillingService;
    private boolean isDataLoaded;

    public static SubscriptionsFragmentDialog newInstance() {
        return new SubscriptionsFragmentDialog();
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

        getOwnedInappsObserveble(getActivity(), mInAppBillingService)
                .flatMap(ownedItems -> getInappsListToBuyObserveble(getActivity(), mInAppBillingService)
                        .flatMap(toBuy -> Observable.just(new Pair<>(ownedItems, toBuy))))
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
//                            if (ownedItemsAndSubscriptions.first.isEmpty()) {
//                                currentSubscriptionValue.setText(getString(R.string.no_subscriptions));
//                            } else {
//                                currentSubscriptionValue.setText(ownedItemsAndSubscriptions.first.get(0).sku);
//                            }
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setHasFixedSize(true);
                            RecyclerAdapterSubscriptions adapter = new RecyclerAdapterSubscriptions();
                            adapter.setData(ownedItemsAndSubscriptions.second);
                            adapter.setArticleClickListener(SubscriptionsFragmentDialog.this);
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
        } catch (Exception e) {
            Timber.e(e, "error ");
            Snackbar.make(root, e.getMessage(), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.d("called in fragment");
        if (requestCode == REQUEST_CODE_SUBSCRIPTION) {
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
                //remove ads item from menu via updating ownedItems list
                getBaseActivity().updateOwnedMarketItems();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static Observable<List<Item>> getOwnedInappsObserveble(Context context, IInAppBillingService mInAppBillingService) {
        return Observable.create(subscriber -> {
            try {
                Bundle ownedItemsBundle = mInAppBillingService.getPurchases(3, context.getPackageName(), "subs", null);

                Timber.d("ownedItems bundle: %s", ownedItemsBundle);
                if (ownedItemsBundle.getInt("RESPONSE_CODE") == 0) {
                    List<String> ownedSkus = ownedItemsBundle.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                    List<String> purchaseDataList = ownedItemsBundle.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                    List<String> signatureList = ownedItemsBundle.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                    String continuationToken = ownedItemsBundle.getString("INAPP_CONTINUATION_TOKEN");

                    if (ownedSkus == null || purchaseDataList == null || signatureList == null) {
                        subscriber.onError(new IllegalStateException("some of owned items info is null while get owned items"));
                    } else {
                        List<Item> ownedItemsList = new ArrayList<>();
                        for (int i = 0; i < purchaseDataList.size(); ++i) {
                            String purchaseData = purchaseDataList.get(i);
                            String signature = signatureList.get(i);
                            String sku = ownedSkus.get(i);
                            ownedItemsList.add(new Item(purchaseData, signature, sku, continuationToken));
                        }
                        subscriber.onNext(ownedItemsList);
                        subscriber.onCompleted();
                    }
                }
                subscriber.onError(new IllegalStateException("ownedItemsBundle.getInt(\"RESPONSE_CODE\") is not 0"));
            } catch (RemoteException e) {
                Timber.e(e);
                subscriber.onError(e);
            }
        });
    }

    public static Observable<List<Subscription>> getInappsListToBuyObserveble(Context context, IInAppBillingService mInAppBillingService) {
        return Observable.create(subscriber -> {
            try {
                //get all subs detailed info
                List<Subscription> allSubscriptions = new ArrayList<>();
                List<String> skuList = new ArrayList<>();
                //get it from build config
//                    Collections.addAll(skuList, BuildConfig.OLD_SKUS);
                Collections.addAll(skuList, BuildConfig.VER_2_SKUS);
                Timber.d("skuList: %s", skuList);

                Bundle querySkus = new Bundle();
                querySkus.putStringArrayList("ITEM_ID_LIST", (ArrayList<String>) skuList);
                Bundle skuDetails = mInAppBillingService.getSkuDetails(3, context.getPackageName(), "subs", querySkus);
                Timber.d("skuDetails: %s", skuDetails);
                if (skuDetails.getInt("RESPONSE_CODE") == 0) {
                    List<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                    if (responseList == null) {
                        subscriber.onError(new IllegalStateException("responseList is null while get subs details"));
                        return;
                    }

                    for (String thisResponse : responseList) {
//                            Timber.d(thisResponse);
                        JSONObject object = new JSONObject(thisResponse);
                        String sku = object.getString("productId");
                        String price = object.getString("price");
                        String title = object.getString("title");
                        allSubscriptions.add(new Subscription(sku, price, title));
                    }
                    Collections.sort(allSubscriptions, Subscription.COMPARATOR_PRICE);

                    subscriber.onNext(allSubscriptions);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new IllegalStateException("ownedItemsBundle.getInt(\"RESPONSE_CODE\") is not 0"));
                }
            } catch (RemoteException | JSONException e) {
                Timber.e(e);
                subscriber.onError(e);
            }
        });
    }
}