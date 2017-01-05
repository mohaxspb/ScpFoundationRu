package ru.dante.scpfoundation.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.QuoteSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import ru.dante.scpfoundation.Const;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.activities.ActivityArticles;
import ru.dante.scpfoundation.activities.ActivityMain;
import ru.dante.scpfoundation.fragments.FragmentArticle;
import ru.dante.scpfoundation.otto.BusProvider;
import ru.dante.scpfoundation.otto.EventBibliographyLinkPress;
import ru.dante.scpfoundation.otto.EventSnoskaLinkPress;
import ru.dante.scpfoundation.otto.EventTocLinkPress;

public class SetTextViewHTML {
    private static final String LOG = SetTextViewHTML.class.getSimpleName();

    private Context ctx;

    public SetTextViewHTML(Context context) {
        this.ctx = context;
    }

    public void setText(TextView artTextView, String html) {
        UILImageGetter imgGetter = new UILImageGetter(artTextView, ctx);
        MyHtmlTagHandler myHtmlTagHandler = new MyHtmlTagHandler(artTextView.getContext());
        CharSequence sequence = Html.fromHtml(html, imgGetter, myHtmlTagHandler);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        //////
        ImageSpan[] imgs = strBuilder.getSpans(0, sequence.length(), ImageSpan.class);
        for (ImageSpan span : imgs) {
            makeImgsClickable(strBuilder, span);
        }
        replaceQuoteSpans(artTextView.getContext(), strBuilder);
        artTextView.setText(strBuilder);
    }

    protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View view) {
                Log.d(LOG, "LINK CLICKED: " + span.getURL());

                String link = span.getURL();
                if (link.contains("javascript")) {
                    Toast.makeText(ctx, "Эта ссылка не поддерживается", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isDigitsOnly(link)) {
                    BusProvider.getInstance().post(new EventSnoskaLinkPress(link));
                    return;
                }
                if (link.startsWith("bibitem-")) {
                    BusProvider.getInstance().post(new EventBibliographyLinkPress(link));
                    return;
                }
                if (link.startsWith("#")) {
                    BusProvider.getInstance().post(new EventTocLinkPress(link));
                    return;
                }
                if (!link.startsWith("http")) {
                    link = Const.DOMAIN_NAME + link;
                }
                Log.d(LOG, "LINK CLICKED: " + link);
                for (String pressedLink : Const.Urls.ALL_LINKS_ARRAY) {
                    if (link.equals(pressedLink)) {
                        ActivityMain.startActivityMain(link, ctx);
                        return;
                    }
                }
                if (ctx instanceof ActivityMain) {
                    Intent intent = new Intent(ctx, ActivityArticles.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title", "");
                    bundle.putString("url", link);
                    intent.putExtras(bundle);
                    ctx.startActivity(intent);
                } else {
                    ActivityArticles activityArticles = (ActivityArticles) ctx;
                    FragmentManager fragmentManager = activityArticles.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment fragment = FragmentArticle.newInstance(link, "");
                    fragmentTransaction.replace(R.id.content_frame, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    //	///////////////
    protected void makeImgsClickable(SpannableStringBuilder strBuilder, ImageSpan span) {
        final String image_src = span.getSource();
        final int start = strBuilder.getSpanStart(span);
        final int end = strBuilder.getSpanEnd(span);

        ClickableSpan click_span = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Log.d(LOG, "makeImgsClickable Click: " + image_src);
                Context ctx = widget.getContext();
                if (ctx instanceof ActivityMain) {
                    ActivityMain activityMain = (ActivityMain) ctx;
                    activityMain.setNeedToShowDialog(image_src);
                } else {
                    ActivityArticles activityArticles = (ActivityArticles) ctx;
                    activityArticles.setNeedToShowDialog(image_src);
                }
                showImageDialog(ctx, image_src);
            }
        };
        ClickableSpan[] click_spans = strBuilder.getSpans(start, end, ClickableSpan.class);

        if (click_spans.length != 0) {
            for (ClickableSpan c_span : click_spans) {
                strBuilder.removeSpan(c_span);
            }
        }
        strBuilder.setSpan(click_span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public static void showImageDialog(final Context ctx, final String imgUrl) {
        Log.d("slkdjlksjdlskjd", "showImageDialog");
        Dialog nagDialog = new Dialog(ctx, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        nagDialog.setCancelable(true);
        nagDialog.setContentView(R.layout.preview_image);

        final ImageViewTouch imageViewTouch = (ImageViewTouch) nagDialog.findViewById(R.id.image_view_touch);
        MyUIL.get(ctx).loadImage(imgUrl, MyUIL.getSimple(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                Matrix matrix = imageViewTouch.getDisplayMatrix();
                imageViewTouch.setImageBitmap(loadedImage, matrix, 0.5f, 2.0f);
            }
        });

        nagDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                System.out.println("nagDialog.onCancel ArtActivity");
                if (ctx instanceof ActivityMain) {
                    ActivityMain activityMain = (ActivityMain) ctx;
                    activityMain.setNeedToShowDialog(null);
                } else {
                    ActivityArticles activityArticles = (ActivityArticles) ctx;
                    activityArticles.setNeedToShowDialog(null);
                }
            }
        });
        nagDialog.show();
    }

    //quotes
    //see http://stackoverflow.com/a/29114976/3212712
    private static void replaceQuoteSpans(Context ctx, Spannable spannable) {
        int colorBackground = AttributeGetter.getColor(ctx, R.attr.windowBackgroundDark);
        int colorStripe = AttributeGetter.getColor(ctx, R.attr.colorAccent);

        QuoteSpan[] quoteSpans = spannable.getSpans(0, spannable.length(), QuoteSpan.class);

        for (QuoteSpan quoteSpan : quoteSpans) {
            int start = spannable.getSpanStart(quoteSpan);
            int end = spannable.getSpanEnd(quoteSpan);
            int flags = spannable.getSpanFlags(quoteSpan);
            spannable.removeSpan(quoteSpan);
            spannable.setSpan(new CustomQuoteSpan(
                            colorBackground,
                            colorStripe,
                            5,
                            10),
                    start,
                    end,
                    flags);
        }
    }
}