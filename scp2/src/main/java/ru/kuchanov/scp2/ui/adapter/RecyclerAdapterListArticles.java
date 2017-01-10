package ru.kuchanov.scp2.ui.adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.kuchanov.scp2.MyApplication;
import ru.kuchanov.scp2.R;
import ru.kuchanov.scp2.db.model.Article;
import ru.kuchanov.scp2.manager.MyPreferenceManager;
import ru.kuchanov.scp2.util.AttributeGetter;

/**
 * Created by Dante on 17.01.2016.
 * <p>
 * for scp_ru
 */
public class RecyclerAdapterListArticles extends RecyclerView.Adapter<RecyclerAdapterListArticles.ViewHolderText> {

    @Inject
    MyPreferenceManager mMyPreferenceManager;

    private List<Article> mData;

    private ArticleClickListener mArticleClickListener;
    private boolean shouldShowPopupOnFavoriteClick;
    private boolean shouldShowPreview;

    public RecyclerAdapterListArticles() {
        MyApplication.getAppComponent().inject(this);
    }

    public void setData(List<Article> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RecyclerAdapterListArticles.ViewHolderText onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerAdapterListArticles.ViewHolderText viewHolder;
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_article, parent, false);
        viewHolder = new ViewHolderText(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapterListArticles.ViewHolderText holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public void setArticleClickListener(ArticleClickListener articleClickListener) {
        mArticleClickListener = articleClickListener;
    }

    public void setShouldShowPopupOnFavoriteClick(boolean show) {
        shouldShowPopupOnFavoriteClick = show;
    }

    public void setShouldShowpreview(boolean show) {
        shouldShowPreview = show;
    }

    class ViewHolderText extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.favorite)
        ImageView favorite;
        @BindView(R.id.read)
        ImageView read;
        @BindView(R.id.offline)
        ImageView offline;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.preview)
        TextView preview;
        @BindView(R.id.root)
        LinearLayout root;

        ViewHolderText(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Article article) {
            Context context = itemView.getContext();
            float uiTextScale = mMyPreferenceManager.getUiTextScale();
            int textSizePrimary = context.getResources().getDimensionPixelSize(R.dimen.text_size_primary);
            int textSizeTertiary = context.getResources().getDimensionPixelSize(R.dimen.text_size_tertiary);

            itemView.setOnClickListener(v -> {
                if (mArticleClickListener != null) {
                    mArticleClickListener.onArticleClicked(article, getAdapterPosition());
                }
            });

            //TODO show them in ViewPager
            //set image
            if (article.imagesUrls != null && !article.imagesUrls.isEmpty()) {
                Glide.with(context)
                        .load(article.imagesUrls.first().val)
                        .centerCrop()
                        .crossFade()
                        .into(image);
            } else {
                Glide.with(context)
                        .load(R.drawable.scp_2)
                        .centerCrop()
                        .crossFade()
                        .into(image);
            }

            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, uiTextScale * textSizePrimary);
            title.setText(Html.fromHtml(article.title));
            //show preview only on siteSearch fragment
            if (shouldShowPreview) {
                preview.setVisibility(View.VISIBLE);
                preview.setTextSize(TypedValue.COMPLEX_UNIT_PX, uiTextScale * textSizeTertiary);
                preview.setText(Html.fromHtml(article.preview));
            } else {
                preview.setVisibility(View.GONE);
            }
            //(отмечание прочитанного)
            int readIconId;
            int readColorId;
            if (article.isInReaden) {
                readColorId = AttributeGetter.getColor(context, R.attr.readTextColor);
                readIconId = AttributeGetter.getDrawableId(context, R.attr.readIconUnselected);
            } else {
                readColorId = AttributeGetter.getColor(context, R.attr.newArticlesTextColor);
                readIconId = AttributeGetter.getDrawableId(context, R.attr.readIcon);
            }
            title.setTextColor(readColorId);
            read.setImageResource(readIconId);
            read.setOnClickListener(v -> {
                if (mArticleClickListener != null) {
                    mArticleClickListener.toggleReadenState(article);
                }
            });
            //(отмтка избранных статей)
            int favsIconId;
            if (article.isInFavorite != Article.ORDER_NONE) {
                favsIconId = AttributeGetter.getDrawableId(context, R.attr.favoriteIcon);
            } else {
                favsIconId = AttributeGetter.getDrawableId(context, R.attr.favoriteIconUnselected);
            }
            favorite.setImageResource(favsIconId);
            favorite.setOnClickListener(v -> {
                if (mArticleClickListener != null) {
                    if (shouldShowPopupOnFavoriteClick && article.isInFavorite != Article.ORDER_NONE) {
                        PopupMenu popup = new PopupMenu(context, favorite);
                        popup.getMenu().add(0, 0, 0, R.string.delete);
                        popup.setOnMenuItemClickListener(item -> {
                            mArticleClickListener.toggleFavoriteState(article);
                            return true;
                        });
                        popup.show();
                    } else {
                        mArticleClickListener.toggleFavoriteState(article);
                    }
                }
            });
            //Кнопки Offline
            int offlineIconId;
            if (article.text != null) {
                offlineIconId = AttributeGetter.getDrawableId(context, R.attr.iconOfflineRemove);
            } else {
                offlineIconId = AttributeGetter.getDrawableId(context, R.attr.iconOfflineAdd);
            }
            offline.animate().cancel();
            offline.setRotation(0f);
            offline.setImageResource(offlineIconId);
            offline.setOnClickListener(v -> {
                if (mArticleClickListener != null) {
                    if (article.text != null) {
                        PopupMenu popup = new PopupMenu(context, offline);
                        popup.getMenu().add(0, 0, 0, R.string.delete);
                        popup.setOnMenuItemClickListener(item -> {
                            mArticleClickListener.onOfflineClicked(article);
                            return true;
                        });
                        popup.show();
                    } else {
//                        offline.animate().rotationBy(360).setDuration(250).setListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                offline.animate().rotationBy(360).setDuration(250).setListener(this);
//                            }
//                        });
                        mArticleClickListener.onOfflineClicked(article);
                    }
                }
            });
        }
    }

    public interface ArticleClickListener {
        void onArticleClicked(Article article, int position);

        void toggleReadenState(Article article);

        void toggleFavoriteState(Article article);

        void onOfflineClicked(Article article);
    }
}