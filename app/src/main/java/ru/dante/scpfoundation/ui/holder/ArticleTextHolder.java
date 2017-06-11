package ru.dante.scpfoundation.ui.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.ui.util.SetTextViewHTML;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

/**
 * Created by mohax on 11.06.2017.
 * <p>
 * for ScpFoundationRu
 */
public class ArticleTextHolder extends RecyclerView.ViewHolder {

    @Inject
    MyPreferenceManager mMyPreferenceManager;

    private SetTextViewHTML.TextItemsClickListener mTextItemsClickListener;

    @BindView(R.id.text)
    TextView textView;

    public ArticleTextHolder(View itemView, SetTextViewHTML.TextItemsClickListener clickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        MyApplication.getAppComponent().inject(this);

        mTextItemsClickListener = clickListener;
    }

    public void bind(String text) {
        Context context = itemView.getContext();
        int textSizePrimary = context.getResources().getDimensionPixelSize(R.dimen.text_size_primary);
        float articleTextScale = mMyPreferenceManager.getArticleTextScale();

        CalligraphyUtils.applyFontToTextView(context, textView, mMyPreferenceManager.getFontPath());

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, articleTextScale * textSizePrimary);
        textView.setLinksClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        //TODO add settings for it
//            textView.setTextIsSelectable(true);
        SetTextViewHTML.setText(textView, text, mTextItemsClickListener);
    }
}