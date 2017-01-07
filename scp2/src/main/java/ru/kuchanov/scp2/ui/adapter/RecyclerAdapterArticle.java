package ru.kuchanov.scp2.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.api.ParseHtmlUtils;
import ru.kuchanov.scp2.db.model.Article;
import ru.kuchanov.scp2.db.model.RealmString;
import ru.kuchanov.scp2.manager.MyPreferenceManager;
import ru.kuchanov.scp2.ui.util.SetTextViewHTML;
import ru.kuchanov.scp2.util.AttributeGetter;
import ru.kuchanov.scp2.util.DialogUtils;
import ru.kuchanov.scp2.util.DimensionUtils;
import timber.log.Timber;

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
    private List<String> mArticlesTextParts;
    @ParseHtmlUtils.TextType
    private List<String> mArticlesTextPartsTypes;

    public List<String> getArticlesTextParts() {
        return mArticlesTextParts;
    }

    private SetTextViewHTML.TextItemsClickListener mTextItemsClickListener;

    public void setTextItemsClickListener(SetTextViewHTML.TextItemsClickListener textItemsClickListener) {
        mTextItemsClickListener = textItemsClickListener;
    }

    public RecyclerAdapterArticle() {
        MyApplication.getAppComponent().inject(this);
    }

    public void setData(Article article) {
//        Timber.d("setData: %s", article);
        mArticle = article;
        if (mArticle.hasTabs) {
            mArticlesTextParts = ParseHtmlUtils.getArticlesTextParts(mArticle.text);
            mArticlesTextPartsTypes = ParseHtmlUtils.getListOfTextTypes(mArticlesTextParts);
        } else {
            mArticlesTextParts = RealmString.toStringList(mArticle.textParts);
            mArticlesTextPartsTypes = RealmString.toStringList(mArticle.textPartsTypes);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TITLE;
        }
        String type = mArticlesTextPartsTypes.get(position - 1);
        switch (type) {
            default:
            case ParseHtmlUtils.TextType.TEXT:
                return TYPE_TEXT;
            case ParseHtmlUtils.TextType.IMAGE:
                return TYPE_IMAGE;
            case ParseHtmlUtils.TextType.SPOILER:
                return TYPE_SPOILER;
            case ParseHtmlUtils.TextType.TABLE:
                return TYPE_TABLE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
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
        switch (getItemViewType(position)) {
            case TYPE_TEXT:
                ((ViewHolderText) holder).bind(mArticlesTextParts.get(position - 1));
                break;
            case TYPE_IMAGE:
                ((ViewHolderImage) holder).bind(mArticlesTextParts.get(position - 1));
                break;
            case TYPE_SPOILER:
                ((ViewHolderSpoiler) holder).bind(mArticlesTextParts.get(position - 1));
                break;
            case TYPE_TITLE:
                ((ViewHolderTitle) holder).bind(mArticle.title);
                break;
            case TYPE_TABLE:
                ((ViewHolderTable) holder).bind(mArticlesTextParts.get(position - 1));
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
            Context context = itemView.getContext();
            int textSizePrimary = context.getResources().getDimensionPixelSize(R.dimen.text_size_primary);
            float articleTextScale = mMyPreferenceManager.getArticleTextScale();

            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, articleTextScale * textSizePrimary);
            textView.setLinksClickable(true);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            //TODO add settings for it
            textView.setTextIsSelectable(true);
            SetTextViewHTML.setText(textView, text, mTextItemsClickListener);
        }
    }

    class ViewHolderSpoiler extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.content)
        TextView content;

        ViewHolderSpoiler(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(String textPart) {
            Context context = itemView.getContext();
            int textSizePrimary = context.getResources().getDimensionPixelSize(R.dimen.text_size_primary);
            float articleTextScale = mMyPreferenceManager.getArticleTextScale();
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, articleTextScale * textSizePrimary);

            ArrayList<String> spoilerParts = ParseHtmlUtils.getSpoilerParts(textPart);

            title.setText(spoilerParts.get(0));
            content.setTextIsSelectable(true);
            content.setLinksClickable(true);
            content.setMovementMethod(LinkMovementMethod.getInstance());
            SetTextViewHTML.setText(content, spoilerParts.get(1), mTextItemsClickListener);

            title.setOnClickListener(v -> {
                if (content.getVisibility() == View.GONE) {
                    title.setCompoundDrawablesWithIntrinsicBounds(AttributeGetter.getDrawableId(context, R.attr.iconArrowUp), 0, 0, 0);
                    content.setVisibility(View.VISIBLE);
                } else {
                    title.setCompoundDrawablesWithIntrinsicBounds(AttributeGetter.getDrawableId(context, R.attr.iconArrowDown), 0, 0, 0);
                    content.setVisibility(View.GONE);
                }
            });
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

            Timber.d("image: %s", imageUrl);

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

                            float multiplier = (float) width / height;
                            width = DimensionUtils.getScreenWidth();
                            height = (int) (width / multiplier);

                            imageView.getLayoutParams().width = width;
                            imageView.getLayoutParams().height = height;

                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                            imageView.setOnClickListener(v -> DialogUtils.showImageDialog(ctx, imageUrl));
                            return false;
                        }
                    })
                    .into(imageView);

            String title = document.getElementsByTag("span").text();
            titleTextView.setTextIsSelectable(true);
            titleTextView.setText(title);
        }
    }

    class ViewHolderTable extends RecyclerView.ViewHolder {
        @BindView(R.id.webView)
        WebView webView;

        ViewHolderTable(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(String tableContent) {
            String fullHtml = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "    <head>\n" +
                    "        <meta charset=\"utf-8\">\n" +
                    "        <style>table.wiki-content-table{border-collapse:collapse;border-spacing:0;margin:.5em auto}table.wiki-content-table td{border:1px solid #888;padding:.3em .7em}table.wiki-content-table th{border:1px solid #888;padding:.3em .7em;background-color:#eee}</style>\n" +
                    "    </head>\n" +
                    "    <body>";
            fullHtml += tableContent;
            fullHtml += "</body>\n" +
                    "</html>";
            webView.loadData(fullHtml, "text/html; charset=UTF-8", null);
        }
    }
}