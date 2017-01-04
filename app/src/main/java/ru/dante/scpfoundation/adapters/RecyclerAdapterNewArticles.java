package ru.dante.scpfoundation.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ru.dante.scpfoundation.Article;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.activities.ActivityArticles;
import ru.dante.scpfoundation.utils.AttributeGetter;
import ru.dante.scpfoundation.utils.FavoriteUtils;
import ru.dante.scpfoundation.utils.OfflineUtils;
import ru.dante.scpfoundation.utils.inapp.SubscriptionHelper;
import ru.dante.scpfoundation.utils.parsing.DownloadArticleForOffline;

/**
 * Created by Dante on 17.01.2016.
 * <p>
 * for scp_ru
 */
public class RecyclerAdapterNewArticles extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<Article> articles;
    private static final String LOG = RecyclerAdapterNewArticles.class.getSimpleName();
    int textSizePrimary;
    boolean needToShowAppInstall = false;
    private boolean needToShowGiveMeMoney = false;

    public void showAppInstall() {
        needToShowAppInstall = true;
        notifyItemChanged(0);
    }

    public void showGiveMeMoney() {
        Log.i(LOG, "Money called");
        needToShowGiveMeMoney = true;
        notifyItemChanged(0);
    }

    public RecyclerAdapterNewArticles(ArrayList<Article> articles) {
        this.articles = articles;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_new_articles, parent, false);
        viewHolder = new ViewHolderText(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolderText holderText = (ViewHolderText) holder;
        final Context ctx = holderText.title.getContext();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        float uiTextScale = pref.getFloat(ctx.getString(R.string.pref_design_key_text_size_ui), 0.75f);
        textSizePrimary = ctx.getResources().getDimensionPixelSize(R.dimen.text_size_primary);
        holderText.title.setTextSize(TypedValue.COMPLEX_UNIT_PX, uiTextScale * textSizePrimary);
        holderText.title.setText(articles.get(position).getTitle());
        holderText.title.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View v) {
                articles.get(position).setIsRead(true);
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

        if (sharedPreferences.contains(articles.get(position).getURL())) {
            int colorId;
            int[] attrs = new int[]{R.attr.readTextColor};
            TypedArray ta = ctx.obtainStyledAttributes(attrs);
            colorId = ta.getColor(0, Color.RED);
            ta.recycle();
            holderText.title.setTextColor(colorId);
            int readSelectedIcon = AttributeGetter.getDrawableId(ctx, R.attr.readIcon);
            holderText.read.setImageResource(readSelectedIcon);

        } else {
            int colorId;
            int[] attrs = new int[]{R.attr.newArticlesTextColor};
            TypedArray ta = ctx.obtainStyledAttributes(attrs);
            colorId = ta.getColor(0, Color.RED);
            ta.recycle();
            holderText.title.setTextColor(colorId);
            int readUnSelectedIcon = AttributeGetter.getDrawableId(ctx, R.attr.readIconUnselected);
            holderText.read.setImageResource(readUnSelectedIcon);
        }
        holderText.read.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View v) {
                if (sharedPreferences.contains(articles.get(position).getURL())) {
                    sharedPreferences.edit().remove(articles.get(position).getURL()).commit();
                } else {
                    sharedPreferences.edit().putBoolean(articles.get(position).getURL(), true).commit();
                }
                notifyItemChanged(position);
            }
        });

//        (отмтка избранных статей)
        if (FavoriteUtils.hasFavoriteWithURL(ctx, articles.get(position).getURL())) {
            int readSelectedIcon = AttributeGetter.getDrawableId(ctx, R.attr.favoriteIcon);
            holderText.favorite.setImageResource(readSelectedIcon);

        } else {

            int readUnSelectedIcon = AttributeGetter.getDrawableId(ctx, R.attr.favoriteIconUnselected);
            holderText.favorite.setImageResource(readUnSelectedIcon);
        }
        holderText.favorite.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View v) {
                FavoriteUtils.updateFavoritesOnDevice(ctx, articles.get(position).getURL(), articles.get(position).getTitle());
                notifyItemChanged(position);
            }
        });
        /*Кнопки Offline*/
        if (OfflineUtils.hasOfflineWithURL(ctx, articles.get(position).getURL())) {
            int readSelectedIcon = AttributeGetter.getDrawableId(ctx, R.attr.iconOfflineRemove);
            holderText.offline.setImageResource(readSelectedIcon);

        } else {

            int readUnSelectedIcon = AttributeGetter.getDrawableId(ctx, R.attr.iconOfflineAdd);
            holderText.offline.setImageResource(readUnSelectedIcon);
        }
        holderText.offline.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View v) {
                if (OfflineUtils.hasOfflineWithURL(ctx, articles.get(position).getURL())) {
                    String articletext = OfflineUtils.getTextByUrl(ctx, articles.get(position).getURL());
                    OfflineUtils.updateOfflineOnDevice(ctx, articles.get(position).getURL(), articles.get(position).getTitle(), articletext, true);
                    notifyItemChanged(position);
                } else {
                    DownloadArticleForOffline articleForOffline = new DownloadArticleForOffline(ctx, articles.get(position).getURL(), 0);
                    articleForOffline.execute();
                }
            }
        });
        // some money please
        if (position == 0 && needToShowGiveMeMoney) {
            Log.i(LOG, "Money True");
            if (holderText.root.getChildCount() != 1) {
                holderText.root.removeViewAt(1);
            }
            View view;
            view = LayoutInflater.from(ctx).inflate(R.layout.app_install, holderText.root, false);
            Button close = (Button) view.findViewById(R.id.close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    needToShowGiveMeMoney = false;
                    notifyItemChanged(0);
                }
            });
            Button install = (Button) view.findViewById(R.id.install);
            install.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    needToShowGiveMeMoney = false;
                    notifyItemChanged(0);
                    SubscriptionHelper.showSubscriptionDialog((AppCompatActivity) ctx);
                }
            });
            install.setText(R.string.buy_subs);
            TextView textView = (TextView) view.findViewById(R.id.text);
            textView.setText(R.string.givemymoney);

            holderText.root.addView(view);
        } else if (position == 0 && needToShowAppInstall) {
            if (holderText.root.getChildCount() != 1) {
                holderText.root.removeViewAt(1);
            }
            View view;
            view = LayoutInflater.from(ctx).inflate(R.layout.app_install, holderText.root, false);
            Button close = (Button) view.findViewById(R.id.close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    needToShowAppInstall = false;
                    notifyItemChanged(0);
                }
            });
            Button install = (Button) view.findViewById(R.id.install);
            install.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    needToShowAppInstall = false;
                    notifyItemChanged(0);
                    try {
                        ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "ru.dante.scpfoundation.eng")));
                    } catch (Exception e) {
                        String marketErrMsg = "Должен был запуститься Play Market, но что-то пошло не так...";
                        System.out.println(marketErrMsg);
                        Toast.makeText(ctx, marketErrMsg, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holderText.root.addView(view);
        } else {
            if (holderText.root.getChildCount() != 1) {
                holderText.root.removeViewAt(1);
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.articles.size();
    }

    static class ViewHolderText extends RecyclerView.ViewHolder {
        ImageView favorite;
        ImageView read;
        ImageView offline;
        TextView title;
        LinearLayout root;

        ViewHolderText(View itemView) {
            super(itemView);
            favorite = (ImageView) itemView.findViewById(R.id.favorite);
            read = (ImageView) itemView.findViewById(R.id.read);
            offline = (ImageView) itemView.findViewById(R.id.offline);
            title = (TextView) itemView.findViewById(R.id.title);
            root = (LinearLayout) itemView;
        }
    }
}