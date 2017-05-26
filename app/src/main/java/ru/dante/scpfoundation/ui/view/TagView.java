package ru.dante.scpfoundation.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.db.model.ArticleTag;

/**
 * Created by mohax on 26.05.2017.
 * <p>
 * for ScpFoundationRu
 */
public class TagView extends FrameLayout {

    @BindView(R.id.root)
    CardView mCardView;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.action)
    ImageView mActionImage;

    private ArticleTag mTag;

    private OnTagClickListener mOnTagClickListener;

    public TagView(Context context) {
        super(context);
        init();
    }

    public TagView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TagView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TagView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.card_tag, this);
        ButterKnife.bind(this);
    }

    @OnClick
    void onCardClick() {
        if (mOnTagClickListener != null) {
            mOnTagClickListener.onTagClicked(this, mTag);
        }
    }

    @Override
    public ArticleTag getTag() {
        return mTag;
    }

    public void setTag(ArticleTag tag) {
        mTag = tag;

        mTitle.setText(tag.title);
    }

    public void setActionImage(@DrawableRes @Action int actionImage) {
        setVisibility(actionImage == Action.NONE ? GONE : VISIBLE);
        mActionImage.setImageResource(actionImage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TagView tagView = (TagView) o;

        return mTag != null ? mTag.equals(tagView.mTag) : tagView.mTag == null;
    }

    @Override
    public int hashCode() {
        return mTag != null ? mTag.hashCode() : 0;
    }

    public OnTagClickListener getOnTagClickListener() {
        return mOnTagClickListener;
    }

    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        mOnTagClickListener = onTagClickListener;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            Action.ADD,
            Action.REMOVE,
            Action.NONE
    })
    public @interface Action {
        @DrawableRes
        int ADD = R.drawable.ic_add;
        @DrawableRes
        int REMOVE = R.drawable.ic_clear;
        @DrawableRes
        int NONE = 0;
    }

    public interface OnTagClickListener {
        void onTagClicked(TagView tagView, ArticleTag tag);
    }
}