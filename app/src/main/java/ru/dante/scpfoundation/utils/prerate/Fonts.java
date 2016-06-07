package ru.dante.scpfoundation.utils.prerate;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by o.leonov on 07.10.2014.
 */
class Fonts {
    private static Typeface light;
    public static Typeface getLightFont(Context cntx)
    {
        if(light==null) light=Typeface.createFromAsset(cntx.getApplicationContext().getAssets(),"fonts/Roboto-Light.ttf");
        return light;
    }

    private static Typeface regular;
    public static Typeface getRegularFont(Context cntx)
    {
        if(regular==null) regular=Typeface.createFromAsset(cntx.getApplicationContext().getAssets(),"fonts/Roboto-Regular.ttf");
        return regular;
    }
}
