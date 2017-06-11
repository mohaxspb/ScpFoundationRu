package ru.dante.scpfoundation.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.api.ParseHtmlUtils;
import ru.dante.scpfoundation.db.model.Article;
import ru.dante.scpfoundation.db.model.RealmString;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import ru.dante.scpfoundation.ui.holder.ArticleImageHolder;
import ru.dante.scpfoundation.ui.holder.ArticleSpoilerHolder;
import ru.dante.scpfoundation.ui.holder.ArticleTableHolder;
import ru.dante.scpfoundation.ui.holder.ArticleTagsHolder;
import ru.dante.scpfoundation.ui.holder.ArticleTextHolder;
import ru.dante.scpfoundation.ui.holder.ArticleTitleHolder;
import ru.dante.scpfoundation.ui.util.SetTextViewHTML;
import timber.log.Timber;

/**
 * Created by Dante on 17.01.2016.
 * <p>
 * for scp_ru
 */
public class ArticleRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TEXT = 0;
    private static final int TYPE_SPOILER = 1;
    private static final int TYPE_IMAGE = 2;
    private static final int TYPE_TITLE = 3;
    private static final int TYPE_TABLE = 4;
    private static final int TYPE_TAGS = 5;

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

    public ArticleRecyclerAdapter() {
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

        Timber.d("mArticlesTextPartsTypes: %s", mArticlesTextPartsTypes);

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TITLE;
        }
        if (position == getItemCount() - 1) {
            return TYPE_TAGS;
        }
        String type = mArticlesTextPartsTypes.get(position - 1);//-1 for title
        switch (type) {
            case ParseHtmlUtils.TextType.TEXT:
                return TYPE_TEXT;
            case ParseHtmlUtils.TextType.IMAGE:
                return TYPE_IMAGE;
            case ParseHtmlUtils.TextType.SPOILER:
                return TYPE_SPOILER;
            case ParseHtmlUtils.TextType.TABLE:
                return TYPE_TABLE;
            default:
                throw new IllegalArgumentException("unexpected type: " + type);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_TITLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_title, parent, false);
                return new ArticleTitleHolder(view);
            case TYPE_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_r_img, parent, false);
                return new ArticleImageHolder(view, mTextItemsClickListener);
            case TYPE_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_text, parent, false);
                return new ArticleTextHolder(view, mTextItemsClickListener);
            case TYPE_SPOILER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_spoiler, parent, false);
                return new ArticleSpoilerHolder(view, mTextItemsClickListener);
            case TYPE_TABLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_table, parent, false);
                return new ArticleTableHolder(view, mTextItemsClickListener);
            case TYPE_TAGS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_tags, parent, false);
                return new ArticleTagsHolder(view, mTextItemsClickListener);
            default:
                throw new IllegalArgumentException("unexpected type: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_TEXT:
                ((ArticleTextHolder) holder).bind(mArticlesTextParts.get(position - 1));
                break;
            case TYPE_IMAGE:
                ((ArticleImageHolder) holder).bind(mArticlesTextParts.get(position - 1));
                break;
            case TYPE_SPOILER:
                ((ArticleSpoilerHolder) holder).bind(mArticlesTextParts.get(position - 1));
                break;
            case TYPE_TITLE:
                ((ArticleTitleHolder) holder).bind(mArticle.title);
                break;
            case TYPE_TABLE:
                ((ArticleTableHolder) holder).bind(mArticlesTextParts.get(position - 1));
                break;
            case TYPE_TAGS:
                ((ArticleTagsHolder) holder).bind(mArticle.tags);
                break;
            default:
                throw new IllegalArgumentException("unexpected item type: " + getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return mArticlesTextParts == null ? 0 : mArticlesTextParts.size() + 1 + 1; //+1 for title and +1 for tags
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}