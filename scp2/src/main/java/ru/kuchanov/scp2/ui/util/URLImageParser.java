package ru.kuchanov.scp2.ui.util;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;

/**
 * Created by mohax on 05.01.2017.
 * <p>
 * for scp_ru
 */
public class URLImageParser implements Html.ImageGetter {
    private TextView container;

    public URLImageParser(TextView v) {
        this.container = v;
    }

    @Override
    public Drawable getDrawable(String source) {
        final UrlDrawable urlDrawable = new UrlDrawable();

        Glide.with(container.getContext()).load(source).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String s, Target<GlideDrawable> glideDrawableTarget, boolean b) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable d, String s, Target<GlideDrawable> glideDrawableTarget, boolean b, boolean b2) {
                d.setBounds(0, 0, d.getIntrinsicWidth() * 4, d.getIntrinsicHeight() * 4);
                urlDrawable.setBounds(0, 0, d.getIntrinsicWidth() * 4, d.getIntrinsicHeight() * 4);
                urlDrawable.drawable = d;
                container.invalidate();
                return true;
            }
        }).into(new ViewTarget<TextView, GlideDrawable>(container) {
            @Override
            public void onResourceReady(GlideDrawable glideDrawable, GlideAnimation<? super GlideDrawable> glideAnimation) {
            }
        });
        return urlDrawable;
    }

    private class UrlDrawable extends GlideDrawable {
        public GlideDrawable drawable;

        public UrlDrawable() {
            super();
        }

        @Override
        public boolean isAnimated() {
            if (drawable != null) {
                return drawable.isAnimated();
            }
            return false;
        }

        @Override
        public void setLoopCount(int i) {
            if (drawable != null) {
                drawable.setLoopCount(i);
            }
        }

        @Override
        public void start() {
            if (drawable != null) {
                drawable.start();
            }
        }

        @Override
        public void stop() {
            if (drawable != null) {
                drawable.stop();
            }
        }

        @Override
        public boolean isRunning() {
            if (drawable != null) {
                return drawable.isRunning();
            }
            return false;
        }

        @Override
        public void setAlpha(int alpha) {
            if (drawable != null) {
                drawable.setAlpha(alpha);
            }
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            if (drawable != null) {
                drawable.setColorFilter(cf);
            }
        }

        @Override
        public int getOpacity() {
            if (drawable != null) {
                return drawable.getOpacity();
            }
            return PixelFormat.UNKNOWN;
        }

        @Override
        public void draw(Canvas canvas) {
            if (drawable != null) {
                drawable.draw(canvas);
                drawable.start();
            }
        }
    }
}