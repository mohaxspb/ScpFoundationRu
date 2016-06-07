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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ru.dante.scpfoundation.Article;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.activities.ActivityArticles;
import ru.dante.scpfoundation.utils.AttributeGetter;
import ru.dante.scpfoundation.utils.FavoriteUtils;
import ru.dante.scpfoundation.utils.OfflineUtils;
import ru.dante.scpfoundation.utils.parsing.DownloadArticleForOffline;

/**
 * Created by Dante on 17.01.2016.
 */
public class RecyclerAdapterNewArticles extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    ArrayList<Article> articles;
    //    public static final int TYPE_TEXT = 0;
//    public static final int TYPE_SPOILER = 1;
//    public static final int TYPE_IMAGE = 2;
    private static final String LOG = RecyclerAdapterNewArticles.class.getSimpleName();
    int textSizePrimary;


    public RecyclerAdapterNewArticles(ArrayList<Article> articles)
    {
        this.articles = articles;
    }

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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        final ViewHolderText holderText = (ViewHolderText) holder;
        final Context ctx = holderText.textView.getContext();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        float uiTextScale = pref.getFloat(ctx.getString(R.string.pref_design_key_text_size_ui), 0.75f);
        textSizePrimary = ctx.getResources().getDimensionPixelSize(R.dimen.text_size_primary);
        holderText.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, uiTextScale * textSizePrimary);
        holderText.textView.setText(articles.get(position).getTitle());
        holderText.textView.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View v)
            {
                articles.get(position).setIsRead(true);
//                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(holderText.title.getContext());
                SharedPreferences sharedPreferences = ctx.getSharedPreferences("read_articles", Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean(articles.get(position).getURL(), true).commit();
                notifyItemChanged(position);
                Intent intent = new Intent(ctx, ActivityArticles.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", articles.get(position).getTitle());
                bundle.putString("url", articles.get(position).getURL());
                intent.putExtras(bundle);
                ctx.startActivity(intent);

//                CheckTimeToAds.starActivityOrShowAds(ctx,intent);
            }
        });
//        (отмечание прочитанного)
        final SharedPreferences sharedPreferences = ctx.getSharedPreferences("read_articles", Context.MODE_PRIVATE);

        if (sharedPreferences.contains(articles.get(position).getURL()))
        {
            int colorId;
            int[] attrs = new int[]{R.attr.readTextColor};
            TypedArray ta = holderText.linearLayout.getContext().obtainStyledAttributes(attrs);
            colorId = ta.getColor(0, Color.RED);
            ta.recycle();
            holderText.textView.setTextColor(colorId);
            int readSelectedIcon = AttributeGetter.getDrawableId(holderText.linearLayout.getContext(), R.attr.readIcon);
            holderText.read.setImageResource(readSelectedIcon);

        } else
        {
            int colorId;
            int[] attrs = new int[]{R.attr.newArticlesTextColor};
            TypedArray ta = holderText.linearLayout.getContext().obtainStyledAttributes(attrs);
            colorId = ta.getColor(0, Color.RED);
            ta.recycle();
            holderText.textView.setTextColor(colorId);
            int readUnSelectedIcon = AttributeGetter.getDrawableId(holderText.linearLayout.getContext(), R.attr.readIconUnselected);
            holderText.read.setImageResource(readUnSelectedIcon);
        }
        holderText.read.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View v)
            {
                if (sharedPreferences.contains(articles.get(position).getURL()))
                {
//                    sharedPreferences.edit().putBoolean(articles.get(position).getURL(), false).commit();
                    sharedPreferences.edit().remove(articles.get(position).getURL()).commit();
                } else
                {
                    sharedPreferences.edit().putBoolean(articles.get(position).getURL(), true).commit();
                }
                notifyItemChanged(position);
            }
        });

//        (отмтка избранных статей)
        if (FavoriteUtils.hasFavoriteWithURL(ctx, articles.get(position).getURL()))
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
                FavoriteUtils.updateFavoritesOnDevice(ctx, articles.get(position).getURL(), articles.get(position).getTitle());
                notifyItemChanged(position);
            }
        });
/*Кнопки Offline*/
        if (OfflineUtils.hasOfflineWithURL(ctx, articles.get(position).getURL()))
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
                if (OfflineUtils.hasOfflineWithURL(ctx, articles.get(position).getURL()))
                {
                    String articletext = OfflineUtils.getTextByUrl(ctx, articles.get(position).getURL());
                    OfflineUtils.updateOfflineOnDevice(ctx, articles.get(position).getURL(), articles.get(position).getTitle(), articletext,true);
                    notifyItemChanged(position);
                } else
                {
                    DownloadArticleForOffline articleForOffline = new DownloadArticleForOffline(ctx, articles.get(position).getURL(), 0);
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
        ImageView favorite;
        ImageView read;
        ImageView offline;
        TextView textView;
        LinearLayout linearLayout;

        public ViewHolderText(View itemView)
        {
            super(itemView);
            favorite = (ImageView) itemView.findViewById(R.id.favorite);
            read = (ImageView) itemView.findViewById(R.id.read);
            offline = (ImageView) itemView.findViewById(R.id.offline);
            textView = (TextView) itemView.findViewById(R.id.title);
            linearLayout = (LinearLayout) itemView;
        }
    }

}
