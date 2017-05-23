package ru.dante.scpfoundation.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import ru.dante.scpfoundation.db.model.Article;

/**
 * Created by Dante on 17.01.2016.
 * <p>
 * for scp_ru
 */
public class ArticlesListWithSearchRecyclerAdapter extends ArticlesListRecyclerAdapter {

    private List<Article> mSortedData = new ArrayList<>();
    private String mSearchQuery = "";

    public List<Article> getDisplayedData() {
        return mSortedData;
    }

    public void sortArticles(String searchQuery) {
        mSearchQuery = searchQuery;
        if (mData == null) {
            return;
        }
        mSortedData.clear();
        for (Article article : mSortedWithFilterData) {
            if (article.title.toLowerCase().contains(searchQuery.toLowerCase())) {
                mSortedData.add(article);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void sortByType(SortType sortType) {
        super.sortByType(sortType);
        sortArticles(mSearchQuery);
    }

    @Override
    public void onBindViewHolder(HolderSimple holder, int position) {
        holder.bind(mSortedData.get(position));
    }

    @Override
    public int getItemCount() {
        return mSortedData.size();
    }
}