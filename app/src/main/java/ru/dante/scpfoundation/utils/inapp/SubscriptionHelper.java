package ru.dante.scpfoundation.utils.inapp;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import ru.dante.scpfoundation.fragments.FragmentDialogShowSubscription;

/**
 * Created for My Application by Dante on 05.03.2016  23:30.
 */
public class SubscriptionHelper
{
    private static final String LOG = SubscriptionHelper.class.getSimpleName() ;

    public static void showSubscriptionDialog(AppCompatActivity ctx)
    {
        Log.i(LOG,"showSubscriptionDialog");
        FragmentDialogShowSubscription fragmentDialogShowSubscription = FragmentDialogShowSubscription.newInstance();
        fragmentDialogShowSubscription.show(ctx.getFragmentManager(), FragmentDialogShowSubscription.LOG);
    }
}
