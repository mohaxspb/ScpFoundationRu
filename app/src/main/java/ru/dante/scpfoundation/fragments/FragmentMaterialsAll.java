package ru.dante.scpfoundation.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Arrays;

import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.activities.ActivityMain;

/**
 * Created for MyApplication by Dante on 16.01.2016  22:45.
 */
public class FragmentMaterialsAll extends Fragment
{
    public static final String LOG = FragmentMaterialsAll.class.getSimpleName();
    private Context ctx;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        this.ctx = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(LOG, "on create view called");
        View v = new ListView(getActivity());
        ListView listView = (ListView) v;
        String[] materialsTitles = getResources().getStringArray(R.array.materials_titles);
        final ArrayList<String> materialsTitlesList = new ArrayList<>(Arrays.asList(materialsTitles));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(),
                R.layout.article_item_text, materialsTitlesList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d(LOG, materialsTitlesList.get(position));
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment;
                ActivityMain activityMain = (ActivityMain) getActivity();
                switch (position)
                {
                    case 0:
                        activityMain.addIdtoToListOfDrawerMenuPressedIds(null);
                        fragment = FragmentMaterials.createFragment("http://scpfoundation.ru/experiment-logs");
                        fragmentTransaction.replace(R.id.content_frame, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                    case 1:
                        activityMain.addIdtoToListOfDrawerMenuPressedIds(null);
                        fragment = FragmentMaterials.createFragment("http://scpfoundation.ru/incident-reports");
                        fragmentTransaction.replace(R.id.content_frame, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                    case 2:
                        activityMain.addIdtoToListOfDrawerMenuPressedIds(null);
                        fragment = FragmentMaterials.createFragment("http://scpfoundation.ru/eye-witness-interviews");
                        fragmentTransaction.replace(R.id.content_frame, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                    case 3:
                        activityMain.addIdtoToListOfDrawerMenuPressedIds(null);
                        fragment = FragmentJoke.newInstanse("http://scpfoundation.ru/scp-list-j");
                        fragmentTransaction.replace(R.id.content_frame, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                    case 4:
                        activityMain.addIdtoToListOfDrawerMenuPressedIds(null);
                        fragment = FragmentArchive.newInstanse("http://scpfoundation.ru/archive");
                        fragmentTransaction.replace(R.id.content_frame, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                    case 5:
                        activityMain.addIdtoToListOfDrawerMenuPressedIds(null);
                        fragment = FragmentMaterials.createFragment("http://scpfoundation.ru/other");
                        fragmentTransaction.replace(R.id.content_frame, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                    case 6:
                        showAccessDialog(ctx);
                        break;
                    case 7:
                        showAccessDialog(ctx);
                        break;
                    case 8:
                        showAccessDialog(ctx);
                        break;
                    case 9:
                        activityMain.addIdtoToListOfDrawerMenuPressedIds(null);
                        fragment = FragmentArticle.newInstance("http://scpfoundation.ru/the-leak", "За кадром");
                        fragmentTransaction.replace(R.id.content_frame, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                }
            }
        });
        return v;
    }

    public static void showAccessDialog(final Context ctx)
    {
        new MaterialDialog.Builder(ctx)
                .title("Доступ закрыт")
                .customView(R.layout.access, true)
                .positiveText("Ok")
                .onPositive(new MaterialDialog.SingleButtonCallback()
                {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                    {
                        Toast.makeText(ctx, "Пароль неверный,доступ закрыт", Toast.LENGTH_SHORT).show();
                    }
                })
                .negativeText("Закрыть")
                .show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
}