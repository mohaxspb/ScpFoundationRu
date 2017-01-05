package ru.kuchanov.scp2.ui.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.QuoteSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ru.kuchanov.scp2.BuildConfig;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.util.AttributeGetter;
import ru.kuchanov.scp2.util.DialogUtils;
import timber.log.Timber;

public class SetTextViewHTML {

    public static void setText(TextView textView, String html) {
        URLImageParser imgGetter = new URLImageParser(textView);
        MyHtmlTagHandler myHtmlTagHandler = new MyHtmlTagHandler();
        CharSequence sequence = Html.fromHtml(html, imgGetter, myHtmlTagHandler);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(textView.getContext(), strBuilder, span);
        }
        ImageSpan[] imgs = strBuilder.getSpans(0, sequence.length(), ImageSpan.class);
        for (ImageSpan span : imgs) {
            makeImgsClickable(strBuilder, span);
        }
        replaceQuoteSpans(textView.getContext(), strBuilder);
        textView.setText(strBuilder);
    }

    private static void makeLinkClickable(Context context, SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View view) {
                Timber.d("LINK CLICKED: %s", span.getURL());

                String link = span.getURL();
                if (link.contains("javascript")) {
                    Toast.makeText(context, "Эта ссылка не поддерживается", Toast.LENGTH_SHORT).show();
                    return;
                }
                //TODO implement snoskas
//                if (TextUtils.isDigitsOnly(link)) {
//                    BusProvider.getInstance().post(new EventSnoskaLinkPress(link));
//                    return;
//                }
//                if (link.startsWith("bibitem-")) {
//                    BusProvider.getInstance().post(new EventBibliographyLinkPress(link));
//                    return;
//                }
//                if (link.startsWith("#")) {
//                    BusProvider.getInstance().post(new EventTocLinkPress(link));
//                    return;
//                }
                if (!link.startsWith("http")) {
                    link = BuildConfig.BASE_API_URL + link;
                }
//                Timber.d("LINK CLICKED: %s", link);
                //TODO implement open articles by link clicked
//                for (String pressedLink : Constants.Urls.ALL_LINKS_ARRAY) {
//                    if (link.equals(pressedLink)) {
//                        ActivityMain.startActivityMain(link, ctx);
//                        return;
//                    }
//                }
//                if (ctx instanceof ActivityMain) {
//                    Intent intent = new Intent(ctx, ActivityArticles.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("title", "");
//                    bundle.putString("url", link);
//                    intent.putExtras(bundle);
//                    ctx.startActivity(intent);
//                } else {
//                    ActivityArticles activityArticles = (ActivityArticles) ctx;
//                    FragmentManager fragmentManager = activityArticles.getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    Fragment fragment = FragmentArticle.newInstance(link, "");
//                    fragmentTransaction.replace(R.id.content_frame, fragment);
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();
//                }
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    //	///////////////
    private static void makeImgsClickable(SpannableStringBuilder strBuilder, ImageSpan span) {
        final String imageSrc = span.getSource();
        final int start = strBuilder.getSpanStart(span);
        final int end = strBuilder.getSpanEnd(span);

        ClickableSpan click_span = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Timber.d("makeImgsClickable Click: %s", imageSrc);
                Context ctx = widget.getContext();
                //TODO think how to restore image dialog Maybe use fragment dialog?..
                DialogUtils.showImageDialog(ctx, imageSrc);
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