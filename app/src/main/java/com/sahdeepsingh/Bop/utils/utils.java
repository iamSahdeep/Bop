package com.sahdeepsingh.Bop.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.playerMain.Main;

public class utils {

    private static final int[] TEMP_ARRAY = new int[1];

    /* For blurring the image*/
    public static Bitmap blurMyImage(Bitmap image, Context context) {
        if (null == image) return null;
        Bitmap bitmaplol = image.copy(image.getConfig(), true);
        RenderScript renderScript = RenderScript.create(context);
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, bitmaplol);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        theIntrinsic.setRadius(77f);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(bitmaplol);
        renderScript.destroy();
        return bitmaplol;

    }

    /*get Themed icons, used in Navigation Drawer in MAinScreen*/
    public static Drawable getThemedIcon(Context c, Drawable drawable) {
        //Need to find the method to get day night values when automatic and System option is selected
        String theme = Main.settings.get("modes", "Day");
        if (theme.equals("Day"))
            drawable.mutate().setColorFilter(ContextCompat.getColor(c, R.color.md_grey_800), PorterDuff.Mode.MULTIPLY);
        return drawable;
    }

    /*Custom Tabs powered by chrome xD*/
    public static void openCustomTabs(Context context, String url) {
        CustomTabsIntent.Builder builderq = new CustomTabsIntent.Builder();
        builderq.setToolbarColor(context.getResources().getColor(R.color.primaryColor));
        builderq.addDefaultShareMenuItem().enableUrlBarHiding();
        CustomTabsIntent customTabsIntent = builderq.build();
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }

    /*Get color from attr but not working*/
    public static int getThemeAttrColor(Context context, int attr) {
        TEMP_ARRAY[0] = attr;
        TypedArray a = context.getTheme().obtainStyledAttributes(TEMP_ARRAY);
        try {
            return a.getColor(3, context.getResources().getColor(R.color.accent));
        } catch (Exception e) {
            return R.color.accent;
        } finally {
            a.recycle();
        }
    }

}
