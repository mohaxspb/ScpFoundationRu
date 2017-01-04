package ru.kuchanov.scp2.ui.adapter;

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

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.db.model.Article;
import ru.kuchanov.scp2.manager.MyPreferenceManager;
import ru.kuchanov.scp2.util.AttributeGetter;

/**
 * Created by Dante on 17.01.2016.
 * <p>
 * for scp_ru
 */
public class RecyclerAdapterNewArticles extends RecyclerView.Adapter<RecyclerAdapterNewArticles.ViewHolderText> {

    @Inject
    MyPreferenceManager mMyPreferenceManager;

    private List<Article> mData;
//    int textSizePrimary;

    private ArticleClickListener mArticleClickListener;

    public RecyclerAdapterNewArticles() {
        MyApplication.getAppComponent().inject(this);
    }

    public void setData(List<Article> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RecyclerAdapterNewArticles.ViewHolderText onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerAdapterNewArticles.ViewHolderText viewHolder = null;
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_new_articles, parent, false);
        viewHolder = new ViewHolderText(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapterNewArticles.ViewHolderText holder, int position) {
        holder.bind(article);
    }

    @Override
    public int getItemCount() {
        return this.mData.size();
    }

    public void setArticleClickListener(ArticleClickListener articleClickListener) {
        mArticleClickListener = articleClickListener;
    }

    class ViewHolderText extends RecyclerView.ViewHolder {
        @BindView(R.id.favorite)
        ImageView favorite;
        @BindView(R.id.read)
        ImageView read;
        @BindView(R.id.offline)
        ImageView offline;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.root)
        LinearLayout root;

        ViewHolderText(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Article article) {
            Context ctx = itemView.getContext();
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
            float uiTextScale = mMyPreferenceManager.getUiTextScale();
            int textSizePrimary = ctx.getResources().getDimensionPixelSize(R.dimen.text_size_primary);
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, uiTextScale * textSizePrimary);
            title.setText(article.title);
            title.setOnClickListener(v -> {
                article.setIsRead(true);
                notifyItemChanged(position);
                Intent intent = new Intent(ctx, ActivityArticles.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", article.title);
                bundle.putString("url", article.url);
                intent.putExtras(bundle);
                ctx.startActivity(intent);
            });
//        (отмечание прочитанного)
            final SharedPreferences sharedPreferences = ctx.getSharedPreferences("read_articles", Context.MODE_PRIVATE);

            if (sharedPreferences.contains(article.url)) {
                int colorId;
                int[] attrs = new int[]{R.attr.readTextColor};
                TypedArray ta = ctx.obtainStyledAttributes(attrs);
                colorId = ta.getColor(0, Color.RED);
                ta.recycle();
                title.setTextColor(colorId);
                int readSelectedIcon = AttributeGetter.getDrawableId(ctx, R.attr.readIcon);
                read.setImageResource(readSelectedIcon);
            } else {
                int colorId;
                int[] attrs = new int[]{R.attr.newArticlesTextColor};
                TypedArray ta = ctx.obtainStyledAttributes(attrs);
                colorId = ta.getColor(0, Color.RED);
                ta.recycle();
                title.setTextColor(colorId);
                int readUnSelectedIcon = AttributeGetter.getDrawableId(ctx, R.attr.readIconUnselected);
                read.setImageResource(readUnSelectedIcon);
            }
            read.setOnClickListener(v -> {
                if (sharedPreferences.contains(article.getURL())) {
                    sharedPreferences.edit().remove(article.getURL()).commit();
                } else {
                    sharedPreferences.edit().putBoolean(article.getURL(), true).commit();
                }
                notifyItemChanged(position);
            });

//        (отмтка избранных статей)
            if (FavoriteUtils.hasFavoriteWithURL(ctx, article.getURL())) {
                int readSelectedIcon = AttributeGetter.getDrawableId(ctx, R.attr.favoriteIcon);
                favorite.setImageResource(readSelectedIcon);

            } else {
                int readUnSelectedIcon = AttributeGetter.getDrawableId(ctx, R.attr.favoriteIconUnselected);
                favorite.setImageResource(readUnSelectedIcon);
            }
            favorite.setOnClickListener(v -> {
                FavoriteUtils.updateFavoritesOnDevice(ctx, article.getURL(), article.getTitle());
                notifyItemChanged(position);
            });
        /*Кнопки Offline*/
            if (OfflineUtils.hasOfflineWithURL(ctx, article.getURL())) {
                int readSelectedIcon = AttributeGetter.getDrawableId(ctx, R.attr.iconOfflineRemove);
                offline.setImageResource(readSelectedIcon);
            } else {
                int readUnSelectedIcon = AttributeGetter.getDrawableId(ctx, R.attr.iconOfflineAdd);
                offline.setImageResource(readUnSelectedIcon);
            }
            offline.setOnClickListener(v -> {
                if (OfflineUtils.hasOfflineWithURL(ctx, article.getURL())) {
                    String articletext = OfflineUtils.getTextByUrl(ctx, article.getURL());
                    OfflineUtils.updateOfflineOnDevice(ctx, article.getURL(), article.getTitle(), articletext, true);
                    notifyItemChanged(position);
                } else {
                    DownloadArticleForOffline articleForOffline = new DownloadArticleForOffline(ctx, article.getURL(), 0);
                    articleForOffline.execute();
                }
            });
        }
    }

    interface ArticleClickListener {
        void onArticleClicked(Article article);

        void toggleReadenState(Article article, boolean isReaden);

        void toggleFavoriteState(Article article, boolean isFavorite);
    }
}