package ru.dante.scpfoundation.ui.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.db.model.ArticleTag;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.ui.util.SetTextViewHTML;
import ru.dante.scpfoundation.ui.view.TagView;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

/**
 * Created by mohax on 11.06.2017.
 * <p>
 * for ScpFoundationRu
 */
public class ArticleTagsHolder extends RecyclerView.ViewHolder {

    @Inject
    MyPreferenceManager mMyPreferenceManager;

    private SetTextViewHTML.TextItemsClickListener mTextItemsClickListener;

    @BindView(R.id.tags)
    TextView title;
    @BindView(R.id.tagsContainer)
    FlexboxLayout mTagsContainer;

    public ArticleTagsHolder(View itemView, SetTextViewHTML.TextItemsClickListener clickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        MyApplication.getAppComponent().inject(this);

        mTextItemsClickListener = clickListener;
    }

    public void bind(List<ArticleTag> data) {
        Context context = itemView.getContext();
        int textSizePrimary = context.getResources().getDimensionPixelSize(R.dimen.text_size_large);
        float articleTextScale = mMyPreferenceManager.getArticleTextScale();
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, articleTextScale * textSizePrimary);

        CalligraphyUtils.applyFontToTextView(context, title, mMyPreferenceManager.getFontPath());

        mTagsContainer.removeAllViews();
        if (data != null) {
            for (ArticleTag tag : data) {
                TagView tagView = new TagView(context);
                tagView.setTag(tag);
                tagView.setActionImage(TagView.Action.NONE);

                tagView.setOnTagClickListener((tagView1, tag1) -> mTextItemsClickListener.onTagClicked(tag1));

                mTagsContainer.addView(tagView);
            }
        }
    }
}