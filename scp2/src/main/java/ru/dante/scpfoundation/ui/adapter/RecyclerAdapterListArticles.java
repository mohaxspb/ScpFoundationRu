package ru.dante.scpfoundation.ui.adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.ui.dialog.SetttingsBottomSheetDialogFragment;
import ru.dante.scpfoundation.util.AttributeGetter;
import ru.dante.scpfoundation.util.DateUtils;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

/**
 * Created by Dante on 17.01.2016.
 * <p>
 * for scp_ru
 */
public class RecyclerAdapterListArticles extends RecyclerView.Adapter<RecyclerAdapterListArticles.HolderSimple> {

    private static final int TYPE_MIN = 0;
    private static final int TYPE_MIDDLE = 1;
    private static final int TYPE_MAX = 2;

    @Inject
    MyPreferenceManager mMyPreferenceManager;

    private List<Article> mData = new ArrayList<>();

    private ArticleClickListener mArticleClickListener;
    private boolean shouldShowPopupOnFavoriteClick;
    private boolean shouldShowPreview;

    public RecyclerAdapterListArticles() {
        MyApplication.getAppComponent().inject(this);
    }

    public void setData(List<Article> data) {
//        mData = data;
        int previousCount = mData.size();

//        notifyItemRangeRemoved(0, previousCount);

        mData.clear();
        mData.addAll(data);

//        notifyItemRangeInserted(0, mData.size());
        Timber.d("previousCount/mData.size(): %s/%s", previousCount, mData.size());

//        notifyItemRangeChanged(0, mData.size());
        notifyDataSetChanged();

//        if (previousCount != mData.size()) {
//            Timber.d("previousCount/mData.size(): %s/%s", previousCount, mData);
//
//            notifyItemRangeInserted(0, mData.size());
//        } else {
//            notifyItemRangeChanged(0, mData.size());
////            notifyDataSetChanged();
//        }
//        notifyItemRangeInserted(0, mData.size());
//        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return mData.get(position).url.hashCode();
    }

    @Override
    public int getItemViewType(int position) {
        switch (mMyPreferenceManager.getListDesignType()) {
            case SetttingsBottomSheetDialogFragment.ListItemType.MIN:
                return TYPE_MIN;
            default:
            case SetttingsBottomSheetDialogFragment.ListItemType.MIDDLE:
                return TYPE_MIDDLE;
            case SetttingsBottomSheetDialogFragment.ListItemType.MAX:
                return TYPE_MAX;
        }
    }

    @Override
    public HolderSimple onCreateViewHolder(ViewGroup parent, int viewType) {
        HolderSimple viewHolder;
        View view;
        switch (viewType) {
            case TYPE_MIN:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_article_ugly_old_style, parent, false);
                viewHolder = new HolderSimple(view);
                break;
            default:
            case TYPE_MIDDLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_article_medium, parent, false);
                viewHolder = new HolderMedium(view);
                break;
            case TYPE_MAX:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_article, parent, false);
                viewHolder = new HolderWithImage(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HolderSimple holder, int position) {
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

    public void setShouldShowPreview(boolean show) {
        shouldShowPreview = show;
    }

    class HolderSimple extends RecyclerView.ViewHolder {
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

        @BindView(R.id.typeIcon)
        ImageView typeIcon;

        HolderSimple(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Article article) {
            Context context = itemView.getContext();

            float uiTextScale = mMyPreferenceManager.getUiTextScale();
            int textSizePrimary = context.getResources().getDimensionPixelSize(R.dimen.text_size_primary);
            int textSizeTertiary = context.getResources().getDimensionPixelSize(R.dimen.text_size_tertiary);

            CalligraphyUtils.applyFontToTextView(context, title, mMyPreferenceManager.getFontPath());
            CalligraphyUtils.applyFontToTextView(context, preview, mMyPreferenceManager.getFontPath());

            itemView.setOnClickListener(v -> {
                if (mArticleClickListener != null) {
                    mArticleClickListener.onArticleClicked(article, getAdapterPosition());
                }
            });

            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, uiTextScale * textSizePrimary);
            title.setText(Html.fromHtml(article.title));
            //showInterstitial preview only on siteSearch fragment
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

            setTypesIcons(article);
        }

        protected void setTypesIcons(Article article) {
            switch (article.type) {
                default:
                case Article.ObjectType.NONE:
                    typeIcon.setImageResource(R.drawable.ic_none_small);
                    break;
                case Article.ObjectType.NEUTRAL_OR_NOT_ADDED:
                    typeIcon.setImageResource(R.drawable.ic_not_add_small);
                    break;
                case Article.ObjectType.SAFE:
                    typeIcon.setImageResource(R.drawable.ic_safe_small);
                    break;
                case Article.ObjectType.EUCLID:
                    typeIcon.setImageResource(R.drawable.ic_euclid_small);
                    break;
                case Article.ObjectType.KETER:
                    typeIcon.setImageResource(R.drawable.ic_keter_small);
                    break;
                case Article.ObjectType.THAUMIEL:
                    typeIcon.setImageResource(R.drawable.ic_thaumiel_small);
                    break;
            }
        }
    }

    class HolderWithImage extends HolderSimple {
        @BindView(R.id.typeIcon)
        ImageView typeIcon;
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.rating)
        TextView rating;
        @BindView(R.id.date)
        TextView date;

        HolderWithImage(View itemView) {
            super(itemView);
        }

        void bind(Article article) {
            super.bind(article);
            Context context = itemView.getContext();

            CalligraphyUtils.applyFontToTextView(context, rating, mMyPreferenceManager.getFontPath());
            CalligraphyUtils.applyFontToTextView(context, date, mMyPreferenceManager.getFontPath());

            //TODO show them in ViewPager
            //set image
            if (article.imagesUrls != null && !article.imagesUrls.isEmpty()) {
                Glide.clear(image);
                Glide.with(context)
                        .load(article.imagesUrls.first().val)
                        .placeholder(AttributeGetter.getDrawableId(context, R.attr.iconEmptyImage))
                        .error(AttributeGetter.getDrawableId(context, R.attr.iconEmptyImage))
                        .animate(android.R.anim.fade_in)
                        .centerCrop()
                        .into(image);
            } else {
                Glide.clear(image);
                Glide.with(context)
                        .load(R.drawable.ic_default_image_big)
                        .placeholder(AttributeGetter.getDrawableId(context, R.attr.iconEmptyImage))
                        .error(AttributeGetter.getDrawableId(context, R.attr.iconEmptyImage))
                        .centerCrop()
                        .animate(android.R.anim.fade_in)
                        .into(image);
            }

            rating.setText(article.rating != 0 ? context.getString(R.string.rating, article.rating) : null);
            date.setText(article.updatedDate != null ? DateUtils.getArticleDateShortFormat(article.updatedDate) : null);
        }

        protected void setTypesIcons(Article article) {
            switch (article.type) {
                default:
                case Article.ObjectType.NONE:
                    typeIcon.setImageResource(R.drawable.ic_none_big);
                    break;
                case Article.ObjectType.NEUTRAL_OR_NOT_ADDED:
                    typeIcon.setImageResource(R.drawable.ic_not_add_big);
                    break;
                case Article.ObjectType.SAFE:
                    typeIcon.setImageResource(R.drawable.ic_safe_big);
                    break;
                case Article.ObjectType.EUCLID:
                    typeIcon.setImageResource(R.drawable.ic_euclid_big);
                    break;
                case Article.ObjectType.KETER:
                    typeIcon.setImageResource(R.drawable.ic_keter_big);
                    break;
                case Article.ObjectType.THAUMIEL:
                    typeIcon.setImageResource(R.drawable.ic_thaumiel_big);
                    break;
            }
        }
    }

    class HolderMedium extends HolderWithImage {

        HolderMedium(View itemView) {
            super(itemView);
        }

        @Override
        void bind(Article article) {
            super.bind(article);

            Context context = itemView.getContext();
            float uiTextScale = mMyPreferenceManager.getUiTextScale();
            int textSizeLarge = context.getResources().getDimensionPixelSize(R.dimen.text_size_large);
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, uiTextScale * textSizeLarge);
        }

        protected void setTypesIcons(Article article) {
            switch (article.type) {
                default:
                case Article.ObjectType.NONE:
                    typeIcon.setImageResource(R.drawable.ic_none_medium);
                    break;
                case Article.ObjectType.NEUTRAL_OR_NOT_ADDED:
                    typeIcon.setImageResource(R.drawable.ic_not_add_medium);
                    break;
                case Article.ObjectType.SAFE:
                    typeIcon.setImageResource(R.drawable.ic_safe_medium);
                    break;
                case Article.ObjectType.EUCLID:
                    typeIcon.setImageResource(R.drawable.ic_euclid_medium);
                    break;
                case Article.ObjectType.KETER:
                    typeIcon.setImageResource(R.drawable.ic_keter_medium);
                    break;
                case Article.ObjectType.THAUMIEL:
                    typeIcon.setImageResource(R.drawable.ic_thaumiel_medium);
                    break;
            }
        }
    }

    public interface ArticleClickListener {
        void onArticleClicked(Article article, int position);

        void toggleReadenState(Article article);

        void toggleFavoriteState(Article article);

        void onOfflineClicked(Article article);
    }
}