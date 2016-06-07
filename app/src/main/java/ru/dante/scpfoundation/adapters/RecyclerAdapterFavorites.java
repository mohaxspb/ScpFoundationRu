package ru.dante.scpfoundation.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.dante.scpfoundation.Article;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.activities.ActivityArticles;
import ru.dante.scpfoundation.utils.AttributeGetter;
import ru.dante.scpfoundation.utils.FavoriteUtils;
import ru.dante.scpfoundation.utils.MyUIL;
import ru.dante.scpfoundation.utils.OfflineUtils;
import ru.dante.scpfoundation.utils.parsing.DownloadArticleForOffline;

/**
 * Created by Dante on 17.01.2016.
 */
public class RecyclerAdapterFavorites extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    ArrayList<Article> articles;
    ArrayList<Article> sorted = new ArrayList<>();
    private RecyclerView recyclerView;
    private static final String LOG = RecyclerAdapterFavorites.class.getSimpleName();
    int textSizePrimary;
    private String searchQuery = "";

    public RecyclerAdapterFavorites(ArrayList<Article> articles, String searchQuery, RecyclerView recyclerView)
    {
        this.articles = articles;
        this.recyclerView = recyclerView;
        sortArticles(searchQuery);
    }

    public void sortArticles(String searchQuery)
    {
        this.searchQuery = searchQuery;
        sorted.clear();
        for (Article article : articles)
        {
            if (article.getTitle().toLowerCase().contains(searchQuery.toLowerCase()))
            {
                sorted.add(article);
            }
        }
        notifyDataSetChanged();
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
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_objects, parent, false);
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
        holderText.textView.setText(sorted.get(position).getTitle());
        holderText.textView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(holderText.textView.getContext(), ActivityArticles.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", sorted.get(position).getTitle());
                bundle.putString("url", sorted.get(position).getURL());
                intent.putExtras(bundle);
                ctx.startActivity(intent);
//                CheckTimeToAds.starActivityOrShowAds(ctx,intent);
            }
        });
        MyUIL.get(ctx).displayImage(sorted.get(position).getImageUrl(), holderText.imageView);
        //        (отмечание прочитанного)
        final SharedPreferences sharedPreferences = ctx.getSharedPreferences("read_articles", Context.MODE_PRIVATE);

        if (sharedPreferences.contains(sorted.get(position).getURL()))
        {
            int colorId = AttributeGetter.getColor(ctx, R.attr.readTextColor);
            holderText.textView.setTextColor(colorId);
            int readSelectedIcon = AttributeGetter.getDrawableId(ctx, R.attr.readIcon);
            holderText.read.setImageResource(readSelectedIcon);

        } else
        {
            int colorId = AttributeGetter.getColor(ctx, R.attr.newArticlesTextColor);
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
                if (sharedPreferences.contains(sorted.get(position).getURL()))
                {
                    sharedPreferences.edit().remove(sorted.get(position).getURL()).commit();
                } else
                {
                    sharedPreferences.edit().putBoolean(sorted.get(position).getURL(), true).commit();
                }
                notifyItemChanged(position);
            }
        });

//        (отмтка избранных статей)
        if (FavoriteUtils.hasFavoriteWithURL(ctx, sorted.get(position).getURL()))
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
                /*FavoriteUtils.updateFavoritesOnDevice(ctx,articles.get(position).getURL(),articles.get(position).getTitle());
                notifyItemChanged(position);*/
//                final Article deletedFavoriteUnsorted = articles.get(position);
                final Article deletedFavoriteSorted = sorted.get(position);
                Log.i(LOG, deletedFavoriteSorted.toString());
                final String deletedTitle = new String(deletedFavoriteSorted.getTitle());
                final String deletedUrl = new String(deletedFavoriteSorted.getURL());
                FavoriteUtils.updateFavoritesOnDevice(ctx, deletedUrl, deletedTitle);

                Snackbar snackbar = Snackbar.make(recyclerView, "Удалено", Snackbar.LENGTH_LONG);
                snackbar.setAction("Отмена", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Article articleToAdd = new Article();
                        articleToAdd.setURL(deletedUrl);
                        articleToAdd.setTitle(deletedTitle);
                        Log.i(LOG, articleToAdd.toString());
                        FavoriteUtils.updateFavoritesOnDevice(ctx, deletedUrl, deletedTitle);


                        articles.add(position, articleToAdd);
//                        sorted.add(position, articleToAdd);
                        sortArticles(searchQuery);
//                        notifyItemInserted(position);
                    /*    notifyItemRangeChanged(position, sorted.size());*/
                    }
                });
                snackbar.show();

                articles.remove(deletedFavoriteSorted);
//                sorted.remove(position);
                sortArticles(searchQuery);
//                notifyItemRemoved(position);
               /* notifyItemRangeChanged(position, sorted.size());*/
            }
        });
        /*Кнопки Offline*/
        if (OfflineUtils.hasOfflineWithURL(ctx, sorted.get(position).getURL()))
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
                if (OfflineUtils.hasOfflineWithURL(ctx, sorted.get(position).getURL()))
                {
                    String articletext = OfflineUtils.getTextByUrl(ctx, sorted.get(position).getURL());
                    OfflineUtils.updateOfflineOnDevice(ctx, sorted.get(position).getURL(), sorted.get(position).getTitle(), articletext, true);
                    notifyItemChanged(position);
                } else
                {
                    DownloadArticleForOffline articleForOffline = new DownloadArticleForOffline(ctx, sorted.get(position).getURL(), 0);
                    articleForOffline.execute();
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return this.sorted.size();
    }

    public static class ViewHolderText extends RecyclerView.ViewHolder
    {
        ImageView favorite;
        ImageView read;
        ImageView offline;
        TextView textView;
        ImageView imageView;

        public ViewHolderText(View itemView)
        {
            super(itemView);
            favorite = (ImageView) itemView.findViewById(R.id.favorite);
            read = (ImageView) itemView.findViewById(R.id.read);
            offline = (ImageView) itemView.findViewById(R.id.offline);
            textView = (TextView) itemView.findViewById(R.id.title);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }

}
