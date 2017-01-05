package ru.kuchanov.scp2.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.api.ParseHtmlUtils;
import ru.kuchanov.scp2.db.model.Article;
import ru.kuchanov.scp2.manager.MyPreferenceManager;
import ru.kuchanov.scp2.ui.util.SetTextViewHTML;
import ru.kuchanov.scp2.util.AttributeGetter;
import ru.kuchanov.scp2.util.DialogUtils;
import ru.kuchanov.scp2.util.DimensionUtils;

/**
 * Created by Dante on 17.01.2016.
 * <p>
 * for scp_ru
 */
public class RecyclerAdapterArticle extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_TEXT = 0;
    private static final int TYPE_SPOILER = 1;
    private static final int TYPE_IMAGE = 2;
    private static final int TYPE_TITLE = 3;
    private static final int TYPE_TABLE = 4;

    @Inject
    MyPreferenceManager mMyPreferenceManager;

    private Article mArticle;
    private ArrayList<String> mArticlesTextParts;

    public ArrayList<String> getArticlesTextParts() {
        return mArticlesTextParts;
    }

    private ArrayList<ParseHtmlUtils.TextType> articlesTextpartsType;

    public RecyclerAdapterArticle() {
        MyApplication.getAppComponent().inject(this);
    }

    public void setData(Article article) {
        mArticle = article;
        mArticlesTextParts = ParseHtmlUtils.getArticlesTextParts(article.text);
        articlesTextpartsType = ParseHtmlUtils.getListOfTextTypes(mArticlesTextParts);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TITLE;
        }
        ParseHtmlUtils.TextType type = articlesTextpartsType.get(position - 1);
        switch (type) {
            default:
            case Text:
                return TYPE_TEXT;
            case Image:
                return TYPE_IMAGE;
            case Spoiler:
                return TYPE_SPOILER;
            case Table:
                return TYPE_TABLE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view;
        switch (viewType) {
            default:
            case TYPE_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_text, parent, false);
                viewHolder = new ViewHolderText(view);
                break;
            case TYPE_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_r_img, parent, false);
                viewHolder = new ViewHolderImage(view);
                break;
            case TYPE_SPOILER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_spoiler, parent, false);
                view.setBackgroundColor(Color.WHITE);
                viewHolder = new ViewHolderSpoiler(view);
                break;
            case TYPE_TITLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_title, parent, false);
                viewHolder = new ViewHolderTitle(view);
                break;
            case TYPE_TABLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_table, parent, false);
                viewHolder = new ViewHolderTable(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Context ctx = holder.itemView.getContext();
        int textSizePrimary = ctx.getResources().getDimensionPixelSize(R.dimen.text_size_primary);
        float articleTextScale = mMyPreferenceManager.getArticleTextScale();
        switch (getItemViewType(position)) {
            case TYPE_TEXT:
                ((ViewHolderText) holder).bind(mArticlesTextParts.get(position - 1));
                break;
            case TYPE_IMAGE:
                ((ViewHolderImage) holder).bind(mArticlesTextParts.get(position - 1));
                break;
            case TYPE_SPOILER:
                final ViewHolderSpoiler viewHolderSpoiler = (ViewHolderSpoiler) holder;
                viewHolderSpoiler.title.setTextSize(TypedValue.COMPLEX_UNIT_PX, articleTextScale * textSizePrimary);
                ArrayList<String> spoilerParts = ParseHtmlUtils.getSpoilerParts(mArticlesTextParts.get(position - 1));
                viewHolderSpoiler.title.setTextIsSelectable(true);
                viewHolderSpoiler.title.setText(spoilerParts.get(0));
                viewHolderSpoiler.content.setTextIsSelectable(true);
                viewHolderSpoiler.content.setLinksClickable(true);
                viewHolderSpoiler.content.setMovementMethod(LinkMovementMethod.getInstance());
                SetTextViewHTML.setText(viewHolderSpoiler.content, spoilerParts.get(1));

                LinearLayout.LayoutParams linerarParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                viewHolderSpoiler.content.setLayoutParams(linerarParams);
                viewHolderSpoiler.title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int contentHeight = viewHolderSpoiler.content.getLayoutParams().height;
                        if (contentHeight == 0) {
                            LinearLayout.LayoutParams paramsFullHeight = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            viewHolderSpoiler.content.setLayoutParams(paramsFullHeight);
                            viewHolderSpoiler.title.setCompoundDrawablesWithIntrinsicBounds(AttributeGetter.getDrawableId(ctx, R.attr.iconArrowUp), 0, 0, 0);
                        } else {
                            LinearLayout.LayoutParams linerarParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                            viewHolderSpoiler.content.setLayoutParams(linerarParams);
                            viewHolderSpoiler.title.setCompoundDrawablesWithIntrinsicBounds(AttributeGetter.getDrawableId(ctx, R.attr.iconArrowDown), 0, 0, 0);
                        }
                    }
                });
                break;
            case TYPE_TITLE:
                ((ViewHolderTitle) holder).bind(mArticle.title);
                break;
            case TYPE_TABLE:
                final ViewHolderTable holderTable = (ViewHolderTable) holder;
                String fullHtml = "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "    <head>\n" +
                        "        <meta charset=\"utf-8\">\n" +
                        "        <style>table.wiki-content-table{border-collapse:collapse;border-spacing:0;margin:.5em auto}table.wiki-content-table td{border:1px solid #888;padding:.3em .7em}table.wiki-content-table th{border:1px solid #888;padding:.3em .7em;background-color:#eee}</style>\n" +
                        "    </head>\n" +
                        "    <body>";
                fullHtml += mArticlesTextParts.get(position - 1);
                fullHtml += "</body>\n" +
                        "</html>";
                holderTable.webView.loadData(fullHtml, "text/html; charset=UTF-8", null);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mArticlesTextParts == null ? 0 : mArticlesTextParts.size() + 1;
    }

    class ViewHolderTitle extends RecyclerView.ViewHolder {
        @BindView(R.id.text)
        TextView textView;

        ViewHolderTitle(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(String title) {
            Context ctx = itemView.getContext();
            float articleTextScale = mMyPreferenceManager.getArticleTextScale();

            int textSizePrimary = ctx.getResources().getDimensionPixelSize(R.dimen.text_size_large);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, articleTextScale * textSizePrimary);
            textView.setTextIsSelectable(true);
            textView.setText(title);
        }
    }

    class ViewHolderText extends RecyclerView.ViewHolder {
        @BindView(R.id.text)
        TextView textView;

        ViewHolderText(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(String text) {
            Context ctx = itemView.getContext();
            int textSizePrimary = ctx.getResources().getDimensionPixelSize(R.dimen.text_size_primary);
            float articleTextScale = mMyPreferenceManager.getArticleTextScale();

            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, articleTextScale * textSizePrimary);
            textView.setLinksClickable(true);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setAutoLinkMask(Linkify.ALL);
            textView.setTextIsSelectable(true);
            SetTextViewHTML.setText(textView, text);
        }
    }

    class ViewHolderSpoiler extends RecyclerView.ViewHolder {
        TextView title, content;

        ViewHolderSpoiler(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            content = (TextView) itemView.findViewById(R.id.content);
        }
    }

    class ViewHolderImage extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        ImageView imageView;
        @BindView(R.id.title)
        TextView titleTextView;

        ViewHolderImage(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(String articleTextPart) {
            Context ctx = itemView.getContext();
            Document document = Jsoup.parse(articleTextPart);
            Element imageTag = document.getElementsByTag("img").first();
            String imageUrl = imageTag.attr("src");

            Glide.with(ctx)
                    .load(imageUrl)
                    .fitCenter()
                    .crossFade()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            int width = resource.getIntrinsicWidth();
                            int height = resource.getIntrinsicHeight();
                            if (resource.getIntrinsicWidth() > DimensionUtils.getScreenWidth()) {
                                width = DimensionUtils.getScreenWidth();
                                float multiplier = (float) width / height;
                                height = (int) (width / multiplier);
                            }
                            imageView.getLayoutParams().width = width;
                            imageView.getLayoutParams().height = height;
                            imageView.setImageDrawable(resource);
                            imageView.setOnClickListener(v -> DialogUtils.showImageDialog(ctx, imageUrl));
                            return true;
                        }
                    });

            String title = document.getElementsByTag("span").text();
            titleTextView.setTextIsSelectable(true);
            titleTextView.setText(title);
        }
    }

    class ViewHolderTable extends RecyclerView.ViewHolder {
        WebView webView;

        ViewHolderTable(View itemView) {
            super(itemView);
            webView = (WebView) itemView;
        }
    }
}