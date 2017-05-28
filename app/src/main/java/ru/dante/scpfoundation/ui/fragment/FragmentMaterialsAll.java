package ru.dante.scpfoundation.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.mvp.contract.MaterialsScreenMvp;
import timber.log.Timber;

/**
 * Created for MyApplication by Dante on 16.01.2016  22:45.
 */
//TODO extend from baseListFragment to set all its advantages
public class FragmentMaterialsAll extends Fragment {

    public static final String TAG = FragmentMaterialsAll.class.getSimpleName();

    public static FragmentMaterialsAll newInstance() {
        return new FragmentMaterialsAll();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("onCreateView");
        View v = new ListView(getActivity());
        ListView listView = (ListView) v;
        String[] materialsTitles = getResources().getStringArray(R.array.materials_titles);
        final ArrayList<String> materialsTitlesList = new ArrayList<>(Arrays.asList(materialsTitles));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(),
                R.layout.article_item_text, materialsTitlesList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Timber.d(materialsTitlesList.get(position));
            MaterialsScreenMvp.View materialsScreenView = (MaterialsScreenMvp.View) getActivity();
            materialsScreenView.onMaterialsListItemClicked(position);
        });

        if (getUserVisibleHint()) {
            if (getActivity() instanceof ArticleFragment.ToolbarStateSetter) {
                ((ArticleFragment.ToolbarStateSetter) getActivity()).setTitle(getString(R.string.materials));
            }
        }

        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        Timber.d("setUserVisibleHint url: %s, isVisibleToUser: %b", url, isVisibleToUser);
        if (isVisibleToUser) {
            if (getActivity() instanceof ArticleFragment.ToolbarStateSetter) {
                ((ArticleFragment.ToolbarStateSetter) getActivity()).setTitle(getString(R.string.materials));
            }
        }
    }
}