package ru.dante.scpfoundation.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.manager.InAppBillingServiceConnectionObservable;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.monetization.model.Subscription;
import ru.dante.scpfoundation.monetization.util.InappHelper;
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

    @Inject
    MyPreferenceManager mMyPreferenceManager;

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

    @OnClick(R.id.removeAdsOneDay)
    void onRemoveAdsOneDayClicked() {
        Timber.d("onRemoveAdsOneDayClicked");
        dismiss();
        getBaseActivity().startRewardedVideoFlow();
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

        InappHelper.getOwnedInappsObserveble(getActivity(), mInAppBillingService)
                .flatMap(ownedItems -> InappHelper.getInappsListToBuyObserveble(getActivity(), mInAppBillingService)
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
                startIntentSenderForResult(pendingIntent.getIntentSender(), REQUEST_CODE_SUBSCRIPTION, new Intent(), 0, 0, 0, null);
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
//            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
//            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

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
}