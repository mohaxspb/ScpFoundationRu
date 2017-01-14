package ru.dante.scpfoundation.utils.wantmoney;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.android.vending.billing.IInAppBillingService;

import java.lang.ref.WeakReference;
import java.util.List;

import ru.dante.scpfoundation.otto.BusProvider;
import ru.dante.scpfoundation.otto.EventGiveMeMoney;

public class GiveMeMoney {
    private static final String LOG = GiveMeMoney.class.getSimpleName();
    private WeakReference<Context> cntxRef;
    private static GiveMeMoney instance;

    private GiveMeMoney() {
    }

    public static GiveMeMoney init(Activity act) {
        if (instance == null) {
            instance = new GiveMeMoney();
            TimeSettings.setFirstStartTime(act);
        }
        instance.cntxRef = new WeakReference<Context>(act);

        return instance;
    }

    /***
     * Эта команда как раз и запускает диалог когда необходимо
     */
    public void showIfNeed(IInAppBillingService inAppBillingService) {
        //Показываем если прошло время и есть интернет(без интернета пользователь не может проголосовать)
        if (TimeSettings.needShowPreRateDialog(cntxRef.get())) {
            Bundle ownedItems = null;
            try {
                ownedItems = inAppBillingService.getPurchases(3, cntxRef.get().getPackageName(), "subs", null);
                int response = ownedItems.getInt("RESPONSE_CODE");
                if (response == 0) {
                    List<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                    if (ownedSkus != null && ownedSkus.size() == 0) {
                        Log.i(LOG, "no subs");
                        showRateDialog();
                    } else {
                        Log.i(LOG, "MONEY!!! given");
                    }
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }

        } else {
            Log.i(LOG, "No time to explain");
        }
    }

    private void showRateDialog() {
        BusProvider.getInstance().post(new EventGiveMeMoney());
        // Ставим флаг, что надо показать позже(если пользователь в самом диалоге не выберет другой вариант)
        TimeSettings.setShowMode(cntxRef.get(), TimeSettings.SHOW_LATER);
        TimeSettings.saveLastShowTime(cntxRef.get());
    }
}