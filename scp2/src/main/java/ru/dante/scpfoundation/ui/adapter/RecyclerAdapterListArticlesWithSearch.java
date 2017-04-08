package ru.dante.scpfoundation.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import ru.dante.scpfoundation.db.model.Article;

/**
 * Created by Dante on 17.01.2016.
 * <p>
 * for scp_ru
 */
public class RecyclerAdapterListArticlesWithSearch extends RecyclerAdapterListArticles {

    private List<Article> mData;

    private List<Article> mSortedData = new ArrayList<>();
    private String mSearchQuery = "";

    public List<Article> getSortedData() {
        return mSortedData;
    }

    public void sortArticles(String searchQuery) {
        mSearchQuery = searchQuery;
        if (mData == null) {
            return;
        }
        int prevCount = mSortedData.size();
        mSortedData.clear();
        for (Article article : mData) {
            if (article.title.toLowerCase().contains(searchQuery.toLowerCase())) {
                mSortedData.add(article);
            }
        }
        notifyDataSetChanged();

//        if (mSortedData.size() < prevCount) {
//            notifyItemRangeRemoved(0, prevCount - mSortedData.size());
//        }
    }

    public void setData(List<Article> data) {
        mData = data;
        sortArticles(mSearchQuery);
    }

    @Override
    public void onBindViewHolder(HolderSimple holder, int position) {
        holder.bind(mSortedData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mSortedData.size();
    }
}