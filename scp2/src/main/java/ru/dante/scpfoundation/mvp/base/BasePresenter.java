package ru.dante.scpfoundation.mvp.base;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import ru.dante.scpfoundation.Constants;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.api.ApiClient;
import ru.dante.scpfoundation.db.DbProviderFactory;
import ru.dante.scpfoundation.db.model.User;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.mvp.contract.LoginActions;
import timber.log.Timber;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for scp_ru
 */
public abstract class BasePresenter<V extends BaseMvp.View>
        extends MvpNullObjectBasePresenter<V>
        implements BaseMvp.Presenter<V> {

    protected MyPreferenceManager mMyPreferencesManager;
    protected DbProviderFactory mDbProviderFactory;
    protected ApiClient mApiClient;

    private User mUser;

    public BasePresenter(MyPreferenceManager myPreferencesManager, DbProviderFactory dbProviderFactory, ApiClient apiClient) {
        mMyPreferencesManager = myPreferencesManager;
        mDbProviderFactory = dbProviderFactory;
        mApiClient = apiClient;

        getUserFromDb();
    }

    @Override
    public void onCreate() {
        Timber.d("onCreate");
    }

    @Override
    public void getUserFromDb() {
        mDbProviderFactory.getDbProvider().getUserAsync().subscribe(
                user -> {
                    mUser = user;
                    if(getView() instanceof LoginActions.View) {
                        ((LoginActions.View)getView()).updateUser(mUser);
                    }
                },
                error -> Timber.e(error, "error while get user from DB")
        );
    }

    @Override
    public User getUser() {
        return mUser;
    }

    @Override
    public void syncFavorite(String url, boolean isFavorite) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference()
                .child(Constants.Firebase.Refs.USERS)
                .child(mUser.uid)
                .child(Constants.Firebase.Refs.FAVORITES)
                .child(url);
        reference.setValue(isFavorite, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                getView().showMessage(R.string.sync_fav_success);
            } else {
                getView().showError(new Throwable(MyApplication.getAppInstance().getString(R.string.error_while_sync_fav)));
            }
        });
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Timber.d("onDataChange exists: %s", dataSnapshot.exists());
                //TODO think if we realy need to get data before updating it
                if (dataSnapshot.exists()) {

                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getView().showError(new Throwable(MyApplication.getAppInstance().getString(R.string.error_while_sync_fav)));
            }
        });
    }
}