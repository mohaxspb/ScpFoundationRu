package ru.kuchanov.scp2.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import ru.kuchanov.scp2.db.model.Article;

/**
 * Created by Dante on 17.01.2016.
 * <p>
 * for scp_ru
 */
public class RecyclerAdapterListArticlesWithSearch extends RecyclerAdapterListArticles {

    private List<Article> mData;

    private List<Article> mSortedData = new ArrayList<>();
    private String mSearchQuery = "";

    public void sortArticles(String searchQuery) {
        mSearchQuery = searchQuery;
        if (mData == null) {
            return;
        }
        mSortedData.clear();
        for (Article article : mData) {
            if (article.title.toLowerCase().contains(searchQuery.toLowerCase())) {
                mSortedData.add(article);
            }
        }
        notifyDataSetChanged();
    }

    public void setData(List<Article> data) {
        mData = data;
        sortArticles(mSearchQuery);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerAdapterListArticles.ViewHolderText holder, int position) {
        holder.bind(mSortedData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mSortedData.size();
    }
}