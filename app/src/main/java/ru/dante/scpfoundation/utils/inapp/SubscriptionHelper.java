package ru.dante.scpfoundation.utils.inapp;

import android.support.v7.app.AppCompatActivity;

import ru.dante.scpfoundation.fragments.FragmentDialogShowSubscription;

/**
 * Created for My Application by Dante on 05.03.2016  23:30.
 */
public class SubscriptionHelper
{
    public static void showSubscriptionDialog(AppCompatActivity ctx){
        FragmentDialogShowSubscription fragmentDialogShowSubscription=FragmentDialogShowSubscription.newInstance();
        fragmentDialogShowSubscription.show(ctx.getFragmentManager(),FragmentDialogShowSubscription.LOG);
    }
}
