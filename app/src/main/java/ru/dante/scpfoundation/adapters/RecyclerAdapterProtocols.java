package ru.dante.scpfoundation.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.activities.ActivityArticles;
import ru.dante.scpfoundation.utils.AttributeGetter;
import ru.dante.scpfoundation.utils.FavoriteUtils;
import ru.dante.scpfoundation.utils.OfflineUtils;
import ru.dante.scpfoundation.utils.parsing.DownloadArticleForOffline;

/**
 * Created by Dante on 17.01.2016.
 */
public class RecyclerAdapterProtocols extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    ArrayList<String> articles;
    private static final String LOG = RecyclerAdapterProtocols.class.getSimpleName();
    int textSizePrimary;



    public RecyclerAdapterProtocols(ArrayList<String> articles)
    {
        this.articles=articles;

        for (String resultItem:articles)
        {
            String[] urlAndTitle=resultItem.split("BBPE");
            titles.add(urlAndTitle[1]);
            urls.add(urlAndTitle[0]);

        }
    }
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> urls= new ArrayList<>();

    @Override
    public int getItemViewType(int position)
    {
      return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        RecyclerView.ViewHolder viewHolder = null;
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_new_articles, parent, false);
        viewHolder = new ViewHolderText(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position)
    {

        final ViewHolderText holderText= (ViewHolderText) holder;
        final Context ctx= holderText.textView.getContext();
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(ctx);
        float uiTextScale = pref.getFloat(ctx.getString(R.string.pref_design_key_text_size_ui), 0.75f);
        textSizePrimary = ctx.getResources().getDimensionPixelSize(R.dimen.text_size_primary);
        holderText.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, uiTextScale * textSizePrimary);
        holderText.textView.setText(titles.get(position));
        holderText.textView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(holderText.textView.getContext(), ActivityArticles.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", titles.get(position));
                bundle.putString("url", urls.get(position));
                intent.putExtras(bundle);
                ctx.startActivity(intent);
//                CheckTimeToAds.starActivityOrShowAds(ctx,intent);
            }
        });
        //        (отмечание прочитанного)
        final   SharedPreferences sharedPreferences = ctx.getSharedPreferences("read_articles", Context.MODE_PRIVATE);

        if (sharedPreferences.contains(urls.get(position)))
        {
            int colorId;
            int[] attrs = new int[]{R.attr.readTextColor};
            TypedArray ta = ctx.obtainStyledAttributes(attrs);
            colorId = ta.getColor(0, Color.RED);
            ta.recycle();
            holderText.textView.setTextColor(colorId);
            int readSelectedIcon = AttributeGetter.getDrawableId(ctx, R.attr.readIcon);
            holderText.read.setImageResource(readSelectedIcon);

        } else
        {
            int colorId;
            int[] attrs = new int[]{R.attr.newArticlesTextColor};
            TypedArray ta = ctx.obtainStyledAttributes(attrs);
            colorId = ta.getColor(0, Color.RED);
            ta.recycle();
            holderText.textView.setTextColor(colorId);
            int readUnSelectedIcon = AttributeGetter.getDrawableId(ctx, R.attr.readIconUnselected);
            holderText.read.setImageResource(readUnSelectedIcon);
        }
        holderText.read.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View v)
            {
                if (sharedPreferences.contains(urls.get(position)))
                {
                    sharedPreferences.edit().remove(urls.get(position)).commit();
                } else
                {
                    sharedPreferences.edit().putBoolean(urls.get(position), true).commit();
                }
                notifyItemChanged(position);
            }
        });

//        (отмтка избранных статей)
        if (FavoriteUtils.hasFavoriteWithURL(ctx,urls.get(position)))
        {
            int readSelectedIcon = AttributeGetter.getDrawableId(ctx, R.attr.favoriteIcon);
            holderText.favorite.setImageResource(readSelectedIcon);

        } else
        {

            int readUnSelectedIcon = AttributeGetter.getDrawableId(ctx, R.attr.favoriteIconUnselected);
            holderText.favorite.setImageResource(readUnSelectedIcon);
        }
        holderText.favorite.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View v)
            {
                FavoriteUtils.updateFavoritesOnDevice(ctx,urls.get(position),titles.get(position));
                notifyItemChanged(position);
            }
        });
        /*Кнопки Offline*/
        if (OfflineUtils.hasOfflineWithURL(ctx, urls.get(position)))
        {
            int readSelectedIcon = AttributeGetter.getDrawableId(ctx, R.attr.iconOfflineRemove);
            holderText.offline.setImageResource(readSelectedIcon);

        } else
        {

            int readUnSelectedIcon = AttributeGetter.getDrawableId(ctx, R.attr.iconOfflineAdd);
            holderText.offline.setImageResource(readUnSelectedIcon);
        }
        holderText.offline.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View v)
            {
                if (OfflineUtils.hasOfflineWithURL(ctx, urls.get(position)))
                {
                    String articletext = OfflineUtils.getTextByUrl(ctx, urls.get(position));
                    OfflineUtils.updateOfflineOnDevice(ctx, urls.get(position),titles.get(position), articletext,true);
                    notifyItemChanged(position);
                } else
                {
                    DownloadArticleForOffline articleForOffline = new DownloadArticleForOffline(ctx, urls.get(position), 0);
                    articleForOffline.execute();
                }
            }
        });


    }

    @Override
    public int getItemCount()
    {
        return this.articles.size();
    }

    public static class ViewHolderText extends RecyclerView.ViewHolder
    {
        TextView textView;
        ImageView favorite;
        ImageView read;
        ImageView offline;

        public ViewHolderText(View itemView)
        {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.title);
            favorite = (ImageView) itemView.findViewById(R.id.favorite);
            read = (ImageView) itemView.findViewById(R.id.read);
            offline = (ImageView) itemView.findViewById(R.id.offline);
        }
    }

}
