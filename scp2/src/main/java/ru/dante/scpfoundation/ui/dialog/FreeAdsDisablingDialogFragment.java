package ru.dante.scpfoundation.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.monetization.model.AppInstallHeader;
import ru.dante.scpfoundation.monetization.model.AppInviteModel;
import ru.dante.scpfoundation.monetization.model.BaseModel;
import ru.dante.scpfoundation.monetization.model.OurApplication;
import ru.dante.scpfoundation.monetization.model.OurApplicationsResponse;
import ru.dante.scpfoundation.monetization.model.RewardedVideo;
import ru.dante.scpfoundation.monetization.model.VkGroupToJoin;
import ru.dante.scpfoundation.monetization.model.VkGroupsToJoinResponse;
import ru.dante.scpfoundation.ui.adapter.FreeAdsDisableRecyclerAdapter;
import ru.dante.scpfoundation.ui.base.BaseActivity;
import ru.dante.scpfoundation.util.IntentUtils;
import timber.log.Timber;

public class FreeAdsDisablingDialogFragment extends DialogFragment {

    public static final String TAG = FreeAdsDisablingDialogFragment.class.getSimpleName();

    @Inject
    Gson mGson;

    public static DialogFragment newInstance() {
        return new FreeAdsDisablingDialogFragment();
    }

    @Inject
    protected MyPreferenceManager mMyPreferenceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getAppComponent().inject(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Timber.d("onCreateDialog");
        final MaterialDialog dialog;

        MaterialDialog.Builder dialogTextSizeBuilder = new MaterialDialog.Builder(getActivity());
        dialogTextSizeBuilder
                .title(R.string.dialog_free_ads_disable_title)
                .positiveText(android.R.string.cancel);

        List<BaseModel> data = new ArrayList<>();

        FirebaseRemoteConfig config = FirebaseRemoteConfig.getInstance();
        if (config.getBoolean(Constants.Firebase.RemoteConfigKeys.FREE_REWARDED_VIDEO_ENABLED)) {
            long numOfMillis = FirebaseRemoteConfig.getInstance()
                    .getLong(Constants.Firebase.RemoteConfigKeys.REWARDED_VIDEO_COOLDOWN_IN_MILLIS);
            long hours = numOfMillis / 1000 / 60 / 60;
            data.add(new RewardedVideo(getString(R.string.watch_video_to_disable_ads, hours)));
        }
        if (config.getBoolean(Constants.Firebase.RemoteConfigKeys.FREE_INVITES_ENABLED)) {
            data.add(new AppInviteModel(getString(R.string.invite_friends)));
        }
        if (config.getBoolean(Constants.Firebase.RemoteConfigKeys.FREE_APPS_INSTALL_ENABLED)) {
            String jsonString = config.getString(Constants.Firebase.RemoteConfigKeys.APPS_TO_INSTALL_JSON);

            List<OurApplication> applications = null;
            try {
                applications = mGson.fromJson(jsonString, OurApplicationsResponse.class).items;
            } catch (Exception e) {
                Timber.e(e);
            }
            if (applications != null) {
                List<OurApplication> availableAppsToInstall = new ArrayList<>();
                for (OurApplication application : applications) {
                    if (mMyPreferenceManager.isAppInstalledForPackage(application.id)) {
                        continue;
                    }
                    if (IntentUtils.isPackageInstalled(getActivity(), application.id)) {
                        continue;
                    }
                    availableAppsToInstall.add(application);
                }
                if (!availableAppsToInstall.isEmpty()) {
                    //add row with description
                    long numOfMillis = FirebaseRemoteConfig.getInstance()
                            .getLong(Constants.Firebase.RemoteConfigKeys.APP_INSTALL_REWARD_IN_MILLIS);
                    long hours = numOfMillis / 1000 / 60 / 60;
                    data.add(new AppInstallHeader(getString(R.string.app_install_ads_disable_title, hours)));
                    data.addAll(availableAppsToInstall);
                }
            }
        }
        if (config.getBoolean(Constants.Firebase.RemoteConfigKeys.FREE_VK_GROUPS_ENABLED)) {
            String jsonString = config.getString(Constants.Firebase.RemoteConfigKeys.VK_GROUPS_TO_JOIN_JSON);

            List<VkGroupToJoin> items = null;
            try {
                items = mGson.fromJson(jsonString, VkGroupsToJoinResponse.class).items;
            } catch (Exception e) {
                Timber.e(e);
            }
            if (items != null) {
                List<VkGroupToJoin> availableItems = new ArrayList<>();
                for (VkGroupToJoin item : items) {
                    if (mMyPreferenceManager.isVkGroupJoined(item.id)) {
                        continue;
                    }
                    availableItems.add(item);
                }
                if (!availableItems.isEmpty()) {
                    //add row with description
                    long numOfMillis = FirebaseRemoteConfig.getInstance()
                            .getLong(Constants.Firebase.RemoteConfigKeys.FREE_VK_GROUPS_JOIN_REWARD);
                    long hours = numOfMillis / 1000 / 60 / 60;
                    data.add(new AppInstallHeader(getString(R.string.vk_group_join_ads_disable_title, hours)));
                    data.addAll(availableItems);
                }
            }
        }
        //TODO add more options

        FreeAdsDisableRecyclerAdapter adapter = new FreeAdsDisableRecyclerAdapter();
        adapter.setData(data);
        adapter.setItemClickListener(data1 -> {
            Timber.d("Clicked data: %s", data1);
            if (data1 instanceof AppInviteModel) {
                IntentUtils.firebaseInvite(getActivity());
            } else if (data1 instanceof OurApplication) {
                IntentUtils.tryOpenPlayMarket(getActivity(), ((OurApplication) data1).id);
            } else if (data1 instanceof RewardedVideo) {
                dismiss();
                getBaseActivity().startRewardedVideoFlow();
            }
        });

        dialogTextSizeBuilder.adapter(adapter, new LinearLayoutManager(getActivity()));

        dialog = dialogTextSizeBuilder.build();

        dialog.getRecyclerView().addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        return dialog;
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }
}