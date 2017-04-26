package ru.dante.scpfoundation.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import ru.dante.scpfoundation.R;

/**
 * Created by Ivan Semkin on 4/27/2017.
 */

public class StorageUtils {
    public static boolean saveImageToGallery(Context context, Bitmap image) {
        return MediaStore.Images.Media.insertImage(context.getContentResolver(), image,
                context.getString(R.string.i_accept),
                context.getString(R.string.ads_app_id)) != null;
    }

}
