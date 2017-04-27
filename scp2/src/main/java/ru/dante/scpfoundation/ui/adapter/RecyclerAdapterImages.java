package ru.dante.scpfoundation.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dante.scpfoundation.MyApplication;
import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.db.model.VkImage;
import ru.dante.scpfoundation.ui.util.SetTextViewHTML;
import ru.dante.scpfoundation.util.AttributeGetter;
import ru.dante.scpfoundation.util.DialogUtils;
import ru.dante.scpfoundation.util.DimensionUtils;

/**
 * Created by Ivan Semkin on 4/27/2017.
 */
public class RecyclerAdapterImages extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<VkImage> mVkImages;

    private SetTextViewHTML.TextItemsClickListener mImageItemsClickListener;

    public void setImageItemsClickListener(SetTextViewHTML.TextItemsClickListener imageItemsClickListener) {
        mImageItemsClickListener = imageItemsClickListener;
    }

    public RecyclerAdapterImages() {
        MyApplication.getAppComponent().inject(this);
    }

    public void setData(List<VkImage> vkImages) {
        mVkImages = vkImages;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_r_img, parent, false);
        viewHolder = new ViewHolderImage(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((ViewHolderImage) holder).bind(mVkImages.get(position));
    }

    @Override
    public int getItemCount() {
        return mVkImages.size();
    }

    class ViewHolderImage extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        ImageView imageView;
        @BindView(R.id.title)
        TextView titleTextView;

        ViewHolderImage(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(VkImage vkImage) {
            Context context = itemView.getContext();
            String imageUrl = vkImage.allUrls.get(vkImage.allUrls.size() - 1).getVal();
            // String imageUrl = imageTag == null ? null : imageTag.attr("src");

            Glide.with(context)
                    .load(imageUrl)
                    .error(AttributeGetter.getDrawableId(context, R.attr.iconEmptyImage))
                    .fitCenter()
                    .crossFade()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            int width = resource.getIntrinsicWidth();
                            int height = resource.getIntrinsicHeight();

                            float multiplier = (float) width / height;
                            width = DimensionUtils.getScreenWidth();
                            height = (int) (width / multiplier);

                            imageView.getLayoutParams().width = width;
                            imageView.getLayoutParams().height = height;

                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                            imageView.setOnClickListener(v -> DialogUtils.showImageDialog(context, imageUrl));
                            return false;
                        }
                    })
                    .into(imageView);

            titleTextView.setText("zaloopka");
        }
    }
}