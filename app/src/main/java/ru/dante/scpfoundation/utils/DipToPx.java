package ru.dante.scpfoundation.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class DipToPx
{
    /**
     *
     * @param dip
     * @param ctx
     * @return convert given dip to px
     */
    public static float convert(int dip, Context ctx)
    {
        //convert given pid to px
        Resources r = ctx.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        return px;
    }
}