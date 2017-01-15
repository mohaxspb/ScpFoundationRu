package ru.dante.scpfoundation.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.monetization.model.Subscription;
import ru.dante.scpfoundation.manager.MyPreferenceManager;
import timber.log.Timber;

/**
 * Created by Dante on 17.01.2016.
 * <p>
 * for scp_ru
 */
public class RecyclerAdapterSubscriptions extends RecyclerView.Adapter<RecyclerAdapterSubscriptions.ViewHolderText> {

    @Inject
    MyPreferenceManager mMyPreferenceManager;

    private List<Subscription> mData;

    private SubscriptionClickListener mArticleClickListener;

    public void setArticleClickListener(SubscriptionClickListener articleClickListener) {
        mArticleClickListener = articleClickListener;
    }

    public RecyclerAdapterSubscriptions() {
        MyApplication.getAppComponent().inject(this);
    }

    public void setData(List<Subscription> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public RecyclerAdapterSubscriptions.ViewHolderText onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerAdapterSubscriptions.ViewHolderText viewHolder;
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_subscription, parent, false);
        viewHolder = new ViewHolderText(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapterSubscriptions.ViewHolderText holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ViewHolderText extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
//        @BindView(R.id.preview)
//        TextView preview;

        ViewHolderText(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Subscription article) {
            Timber.d("binf with date: %s", article);
            Context context = itemView.getContext();
            float uiTextScale = mMyPreferenceManager.getUiTextScale();
            int textSizePrimary = context.getResources().getDimensionPixelSize(R.dimen.text_size_primary);
            int textSizeTertiary = context.getResources().getDimensionPixelSize(R.dimen.text_size_tertiary);

            itemView.setOnClickListener(v -> {
                if (mArticleClickListener != null) {
                    mArticleClickListener.onSubscriptionClicked(article);
                }
            });

            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, uiTextScale * textSizePrimary);
            String text = article.title.replace("(" + context.getString(R.string.app_name) + ")", "") + " - " + article.price;
            title.setText(text);
        }
    }

    public interface SubscriptionClickListener {
        void onSubscriptionClicked(Subscription article);
    }
}