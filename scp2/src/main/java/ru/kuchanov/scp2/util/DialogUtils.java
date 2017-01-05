package ru.kuchanov.scp2.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import ru.kuchanov.scp2.R;
import timber.log.Timber;

/**
 * Created by mohax on 05.01.2017.
 * <p>
 * for scp_ru
 */
public class DialogUtils {

    public static void showImageDialog(final Context ctx, final String imgUrl) {
        Timber.d("showImageDialog");
        Dialog nagDialog = new Dialog(ctx, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        nagDialog.setCancelable(true);
        nagDialog.setContentView(R.layout.preview_image);

        final ImageViewTouch imageViewTouch = (ImageViewTouch) nagDialog.findViewById(R.id.image_view_touch);

        Glide.with(ctx)
                .load(imgUrl)
                .asBitmap()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        Matrix matrix = imageViewTouch.getDisplayMatrix();
                        imageViewTouch.setImageBitmap(resource, matrix, 0.5f, 2.0f);
                        return true;
                    }
                });

        nagDialog.setOnCancelListener(dialog -> {
            System.out.println("nagDialog.onCancel ArtActivity");
            //TODO think how to restore image dialog Maybe use fragment dialog?..
        });
        nagDialog.show();
    }
}