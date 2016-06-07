package ru.dante.scpfoundation.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.preference.PreferenceManager;
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

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import ru.dante.scpfoundation.Article;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.utils.AttributeGetter;
import ru.dante.scpfoundation.utils.MyUIL;
import ru.dante.scpfoundation.utils.SetTextViewHTML;
import ru.dante.scpfoundation.utils.parsing.DownloadArticle;

/**
 * Created by Dante on 17.01.2016.
 */
public class RecyclerAdapterArticle extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_SPOILER = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_TITLE = 3;
    private static final int TYPE_TABLE = 4;
    private static final String LOG = RecyclerAdapterArticle.class.getSimpleName();

    //    private String articlesText;
    private Article article;
    private ArrayList<String> articlesTextParts;

    public ArrayList<String> getArticlesTextParts()
    {
        return articlesTextParts;
    }

    private ArrayList<DownloadArticle.TextType> articlesTextpartsType;

    public RecyclerAdapterArticle(Article article)
    {
        this.article = article;
        this.articlesTextParts = DownloadArticle.getArticlesTextParts(article.getArticlesText());
        this.articlesTextpartsType = DownloadArticle.getListOfTextTypes(articlesTextParts);
    }

    @Override
    public int getItemViewType(int position)
    {
        if (position == 0)
        {
            return TYPE_TITLE;
        }
        DownloadArticle.TextType type = this.articlesTextpartsType.get(position - 1);
        switch (type)
        {
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder viewHolder = null;
        View view;
        switch (viewType)
        {
            default:
            case TYPE_TEXT:
                view = new TextView(parent.getContext());
                viewHolder = new ViewHolderText(view);
                break;
            case TYPE_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_item_r_img, parent, false);
                viewHolder = new ViewHolderImage(view);
                break;
            case TYPE_SPOILER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_article_spoiler, parent, false);
                view.setBackgroundColor(Color.WHITE);
                viewHolder = new ViewHolderSpoiler(view);
                break;
            case TYPE_TITLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_item_title, parent, false);
                viewHolder = new ViewHolderText(view);
                break;
            case TYPE_TABLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_table, parent, false);
                viewHolder = new ViewHolderTable(view);
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position)
    {
        final Context ctx;
        int textSizePrimary;
        float articleTextScale;
        SharedPreferences pref;
        switch (this.getItemViewType(position))
        {
            case TYPE_TEXT:
                final ViewHolderText viewHolderText = (ViewHolderText) holder;
                ctx = viewHolderText.textView.getContext();
                pref = PreferenceManager.getDefaultSharedPreferences(ctx);
                articleTextScale = pref.getFloat(ctx.getString(R.string.pref_design_key_text_size_article), 0.75f);
                textSizePrimary = ctx.getResources().getDimensionPixelSize(R.dimen.text_size_primary);
                viewHolderText.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, articleTextScale * textSizePrimary);
                viewHolderText.textView.setPadding(50, 0, 50, 0);
                viewHolderText.textView.setLinksClickable(true);
                viewHolderText.textView.setMovementMethod(LinkMovementMethod.getInstance());
                viewHolderText.textView.setAutoLinkMask(Linkify.ALL);
//                viewHolderText.title.setTextIsSelectable(true);
                setTextSelectebleAndFixBug(viewHolderText.textView);
                new SetTextViewHTML(ctx).setText(viewHolderText.textView, articlesTextParts.get(position - 1));
                break;
            case TYPE_IMAGE:
//                Log.d(LOG, "Type image");
                final ViewHolderImage holderImage = (ViewHolderImage) holder;
                String htmlWithImage = articlesTextParts.get(position - 1);
//                Log.d(LOG, htmlWithImage);
                Document document = Jsoup.parse(articlesTextParts.get(position - 1));
                Element imageTag = document.getElementsByTag("img").first();
                String imageUrl = imageTag.attr("src");
                MyUIL.get(holderImage.imageView.getContext()).displayImage(imageUrl, holderImage.imageView, new SimpleImageLoadingListener()
                {
                    @Override
                    public void onLoadingComplete(final String imageUri, View view, Bitmap loadedImage)
                    {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        int imageWidth = loadedImage.getWidth();
                        int imageHeight = loadedImage.getHeight();
//                        Log.d(LOG, "imageWidth: " + imageWidth);
//                        Log.d(LOG, "imageHeight: " + imageHeight);
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
                        params.width = imageWidth;
                        params.height = imageHeight;
                        view.setLayoutParams(params);
                        view.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Dialog nagDialog = new Dialog(holderImage.imageView.getContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                                nagDialog.setCancelable(true);
                                nagDialog.setContentView(R.layout.preview_image);

                                final ImageViewTouch imageViewTouch = (ImageViewTouch) nagDialog.findViewById(R.id.image_view_touch);
                                MyUIL.get(holderImage.imageView.getContext()).loadImage(imageUri, MyUIL.getSimple(), new SimpleImageLoadingListener()
                                {
                                    @Override
                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
                                    {
                                        super.onLoadingComplete(imageUri, view, loadedImage);
                                        Matrix matrix = imageViewTouch.getDisplayMatrix();
                                        imageViewTouch.setImageBitmap(loadedImage, matrix, 0.5f, 2.0f);
                                    }
                                });

                                nagDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
                                {
                                    @Override
                                    public void onCancel(DialogInterface dialog)
                                    {
                                        System.out.println("nagDialog.onCancel ArtActivity");
                                    }
                                });
                                nagDialog.show();
                            }
                        });

                    }
                });
                String title = document.getElementsByTag("span").text();
                setTextSelectebleAndFixBug(holderImage.title);
                holderImage.title.setText(title);
                break;
            case TYPE_SPOILER:
                final ViewHolderSpoiler viewHolderSpoiler = (ViewHolderSpoiler) holder;
                ctx = viewHolderSpoiler.title.getContext();
                pref = PreferenceManager.getDefaultSharedPreferences(ctx);
                articleTextScale = pref.getFloat(ctx.getString(R.string.pref_design_key_text_size_article), 0.75f);
                textSizePrimary = ctx.getResources().getDimensionPixelSize(R.dimen.text_size_primary);
                viewHolderSpoiler.title.setTextSize(TypedValue.COMPLEX_UNIT_PX, articleTextScale * textSizePrimary);
                ArrayList<String> spoilerParts = DownloadArticle.getSpoilerParts(articlesTextParts.get(position - 1));
                setTextSelectebleAndFixBug(viewHolderSpoiler.title);
                viewHolderSpoiler.title.setText(spoilerParts.get(0));
                setTextSelectebleAndFixBug(viewHolderSpoiler.content);
                viewHolderSpoiler.content.setLinksClickable(true);
                viewHolderSpoiler.content.setMovementMethod(LinkMovementMethod.getInstance());
                new SetTextViewHTML(ctx).setText(viewHolderSpoiler.content, spoilerParts.get(1));

                LinearLayout.LayoutParams linerarParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                viewHolderSpoiler.content.setLayoutParams(linerarParams);
                viewHolderSpoiler.title.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        int contentHeight = viewHolderSpoiler.content.getLayoutParams().height;
                        if (contentHeight == 0)
                        {
                            LinearLayout.LayoutParams paramsFullHeight = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            viewHolderSpoiler.content.setLayoutParams(paramsFullHeight);
                            viewHolderSpoiler.title.setCompoundDrawablesWithIntrinsicBounds(AttributeGetter.getDrawableId(ctx, R.attr.iconArrowUp), 0, 0, 0);
                        } else
                        {
                            LinearLayout.LayoutParams linerarParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                            viewHolderSpoiler.content.setLayoutParams(linerarParams);
                            viewHolderSpoiler.title.setCompoundDrawablesWithIntrinsicBounds(AttributeGetter.getDrawableId(ctx, R.attr.iconArrowDown), 0, 0, 0);
                        }
                    }
                });
                break;
            case TYPE_TITLE:
                final ViewHolderText holderTitle = (ViewHolderText) holder;
                ctx = holderTitle.textView.getContext();
                pref = PreferenceManager.getDefaultSharedPreferences(ctx);
                articleTextScale = pref.getFloat(ctx.getString(R.string.pref_design_key_text_size_article), 0.75f);
                textSizePrimary = ctx.getResources().getDimensionPixelSize(R.dimen.text_size_large);
                holderTitle.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, articleTextScale * textSizePrimary);
                setTextSelectebleAndFixBug(holderTitle.textView);
                holderTitle.textView.setText(article.getTitle());
                break;
            case TYPE_TABLE:
//                Log.d(LOG, "Type image");
                final ViewHolderTable holderTable = (ViewHolderTable) holder;
                String fullHtml = "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "    <head>\n" +
                        "        <meta charset=\"utf-8\">\n" +
                        "        <style>table.wiki-content-table{border-collapse:collapse;border-spacing:0;margin:.5em auto}table.wiki-content-table td{border:1px solid #888;padding:.3em .7em}table.wiki-content-table th{border:1px solid #888;padding:.3em .7em;background-color:#eee}</style>\n" +
                        "    </head>\n" +
                        "    <body>";
                fullHtml += articlesTextParts.get(position - 1);
                fullHtml += "</body>\n" +
                        "</html>";
                holderTable.webView.loadData(fullHtml, "text/html; charset=UTF-8", null);
                break;
        }
    }

    @Override
    public int getItemCount()
    {
        return this.articlesTextParts.size() + 1;
    }

    public static class ViewHolderText extends RecyclerView.ViewHolder
    {
        TextView textView;

        public ViewHolderText(View itemView)
        {
            super(itemView);
            textView = (TextView) itemView;
        }
    }

    public static class ViewHolderSpoiler extends RecyclerView.ViewHolder
    {
        TextView title, content;

        public ViewHolderSpoiler(View itemView)
        {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            content = (TextView) itemView.findViewById(R.id.content);
        }
    }

    public static class ViewHolderImage extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        TextView title;

        public ViewHolderImage(View itemView)
        {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }

    public static class ViewHolderTable extends RecyclerView.ViewHolder
    {
        WebView webView;

        public ViewHolderTable(View itemView)
        {
            super(itemView);
            webView = (WebView) itemView;
        }
    }

    public static void setTextSelectebleAndFixBug(TextView textView)
    {
        textView.setTextIsSelectable(true);
//        title.setOnTouchListener(new View.OnTouchListener()
//        {
//            @Override
//            public boolean onTouch(View v, MotionEvent event)
//            {
//                TextView title= (TextView) v;
//                int selectionStart=title.getSelectionStart();
//                if (selectionStart==-1){
//                    title.setTextIsSelectable(false);
//                    title.setTextIsSelectable(true);
//                }
//                return true;
//            }
//        });
    }
}
