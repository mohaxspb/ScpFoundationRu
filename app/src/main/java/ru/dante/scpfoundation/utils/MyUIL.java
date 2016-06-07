package ru.dante.scpfoundation.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.L;


public class MyUIL
{
    public static DisplayImageOptions getRoundVKAvatarOptions(Context act)
    {

        return new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer((int) DipToPx.convert(56, act)))
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }
    public static ImageLoader get(Context act)
    {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(10))
//                .showImageOnLoading(R.drawable.ic_refresh_grey600_48dp)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        //switch to true if you want logging
        L.writeLogs(false);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(act)
                .defaultDisplayImageOptions(options)
                .build();

        ImageLoader imageLoader = ImageLoader.getInstance();

        if (!imageLoader.isInited())
        {
            imageLoader.init(config);
        }

        return imageLoader;

    }

    public static DisplayImageOptions getSimple()
    {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public static DisplayImageOptions getSimpleFullSize()
    {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.NONE)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();
    }

    public static DisplayImageOptions getSimple100x100()
    {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }
}
