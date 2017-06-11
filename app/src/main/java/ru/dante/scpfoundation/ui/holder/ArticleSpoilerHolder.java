package ru.dante.scpfoundation.ui.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.api.ParseHtmlUtils;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.ui.util.SetTextViewHTML;
import ru.dante.scpfoundation.util.AttributeGetter;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

/**
 * Created by mohax on 11.06.2017.
 * <p>
 * for ScpFoundationRu
 */
public class ArticleSpoilerHolder extends RecyclerView.ViewHolder {

    @Inject
    MyPreferenceManager mMyPreferenceManager;

    private SetTextViewHTML.TextItemsClickListener mTextItemsClickListener;

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.content)
    TextView content;

    public ArticleSpoilerHolder(View itemView, SetTextViewHTML.TextItemsClickListener clickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        MyApplication.getAppComponent().inject(this);

        mTextItemsClickListener = clickListener;
    }

    public void bind(String textPart) {
        Context context = itemView.getContext();
        int textSizePrimary = context.getResources().getDimensionPixelSize(R.dimen.text_size_primary);
        float articleTextScale = mMyPreferenceManager.getArticleTextScale();
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, articleTextScale * textSizePrimary);

        CalligraphyUtils.applyFontToTextView(context, title, mMyPreferenceManager.getFontPath());
        CalligraphyUtils.applyFontToTextView(context, content, mMyPreferenceManager.getFontPath());

        List<String> spoilerParts = ParseHtmlUtils.getSpoilerParts(textPart);

        title.setText(spoilerParts.get(0));
        //TODO add settings for it
//            mContent.setTextIsSelectable(true);
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