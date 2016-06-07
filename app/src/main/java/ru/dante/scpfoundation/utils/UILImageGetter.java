package ru.dante.scpfoundation.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.InputStream;

public class UILImageGetter implements Html.ImageGetter
{
    private Context ctx;
    private TextView container;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private UrlImageDownloader urlDrawable;

    public UILImageGetter(View t, Context c)
    {
        this.ctx = c;
        this.container = (TextView) t;

        imageLoader = MyUIL.get(c);
        options = new DisplayImageOptions.Builder().cacheOnDisk(true).cacheInMemory(false).imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).resetViewBeforeLoading(true).build();
    }

    @Override
    public Drawable getDrawable(String source)
    {
        urlDrawable = new UrlImageDownloader(ctx.getResources(), source);
        imageLoader.loadImage(source, options, new SimpleListener(urlDrawable));
        return urlDrawable;
    }

    private class SimpleListener extends SimpleImageLoadingListener
    {
        UrlImageDownloader urlImageDownloader;

        public SimpleListener(UrlImageDownloader downloader)
        {
            super();
            urlImageDownloader = downloader;
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
        {
            int width = loadedImage.getWidth();
            int height = loadedImage.getHeight();

            int newWidth = width;
            int newHeight = height;

            if (width > container.getWidth())
            {
                newWidth = container.getWidth();
                newHeight = (newWidth * height) / width;
            }

            Drawable result = new BitmapDrawable(ctx.getResources(), loadedImage);
            int screenWidth=ScreenProperties.getWidth((AppCompatActivity) ctx);
            int leftpadding=-50;
            if (newWidth<screenWidth){
                leftpadding+=(screenWidth-newWidth)/2;
            }
            result.setBounds(leftpadding, 0, leftpadding+newWidth, newHeight);

            urlImageDownloader.setBounds(leftpadding, 0, leftpadding+newWidth, newHeight);
            urlImageDownloader.drawable = result;

            container.setText(container.getText());
        }
    }

    public class UrlImageDownloader extends BitmapDrawable
    {
        public Drawable drawable;

        /**
         * Create a drawable by decoding a bitmap from the given input stream.
         *
         * @param res
         * @param is
         */
        public UrlImageDownloader(Resources res, InputStream is)
        {
            super(res, is);
        }

        /**
         * Create a drawable by opening a given file path and decoding the bitmap.
         *
         * @param res
         * @param filepath
         */
        public UrlImageDownloader(Resources res, String filepath)
        {
            super(res, filepath);
            drawable = new BitmapDrawable(res, filepath);
        }

        /**
         * Create drawable from a bitmap, setting initial target density based on
         * the display metrics of the resources.
         *
         * @param res
         * @param bitmap
         */
        public UrlImageDownloader(Resources res, Bitmap bitmap)
        {
            super(res, bitmap);
        }

        @Override
        public void draw(Canvas canvas)
        {
            // override the draw to facilitate refresh function later
            if (drawable != null)
            {
                drawable.draw(canvas);
            }
        }
    }
}
