package com.sahdeepsingh.Bop.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.playerMain.Main;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicBlur;

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
        theIntrinsic.setRadius(25f);
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

    /*Get color from attr, but not working*/
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

    public static void sendFeedback(Context context) {
        String body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                String.valueOf(Main.versionCode) + "\n App Version: " + Main.versionName + "\n Device Brand: " + Build.BRAND +
                "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto: emicladevelopers@gmail.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Query / Feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(emailIntent, "Send feedback"));
    }

    public static HashMap<String, Integer> sortMapByValue(HashMap<String, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer>> list =
                new LinkedList<>(hm.entrySet());

        // Sort the list
        Collections.sort(list, (o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

}
