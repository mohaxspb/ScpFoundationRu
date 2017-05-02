package ru.dante.scpfoundation.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import ru.dante.scpfoundation.R;
import ru.dante.scpfoundation.ui.activity.GalleryActivity;

import static ru.dante.scpfoundation.util.IntentUtils.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

/**
 * Created by Ivan Semkin on 4/27/2017.
 */

public class StorageUtils {
    public static boolean saveImageToGallery(Activity activity, Bitmap image) {
        int permissionCheck = ContextCompat.checkSelfPermission(
                activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return MediaStore.Images.Media.insertImage(activity.getContentResolver(), image,
                    activity.getString(R.string.i_accept),
                    activity.getString(R.string.ads_app_id)) != null; //todo wtf
        } else {
            ActivityCompat.requestPermissions(
                    activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        return false;
    }
}
