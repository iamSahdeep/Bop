package com.sahdeepsingh.Bop.BopUtils;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.SongData.Song;
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

public class ExtraUtils {

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
        else
            drawable.mutate().setColorFilter(ContextCompat.getColor(c, R.color.white), PorterDuff.Mode.MULTIPLY);
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
    public static int getThemeAttrColor(Context context, int NumberfromThemeStylable) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(R.styleable.Theme);
        return ta.getColor(NumberfromThemeStylable, context.getResources().getColor(R.color.colorAccent));
    }

    public static void sendFeedback(Context context) {
        String body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                String.valueOf(android.os.Build.VERSION.SDK_INT) + "\n App Version: " + Main.versionName + "\n Device Brand: " + Build.BRAND +
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

    public static Bitmap getBitmapfromAlbumId(Context context,Song localItem){
        Bitmap bitmap;
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri,Long.parseLong(localItem.getAlbumid()));
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_foreground);
        }
        return bitmap;
    }
    public static Uri getUrifromAlbumID(Song song){
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        return ContentUris.withAppendedId(sArtworkUri,Long.parseLong(song.getAlbumid()));
    }

    public static int getThemeColor(Context context, int colorPrimary, int dkgray) {
        int themeColor = 0;
        String packageName = context.getPackageName();
        try {
            Context packageContext = context.createPackageContext(packageName, 0);
            ApplicationInfo applicationInfo =
                    context.getPackageManager().getApplicationInfo(packageName, 0);
            packageContext.setTheme(applicationInfo.theme);
            Resources.Theme theme = packageContext.getTheme();
            TypedArray ta = theme.obtainStyledAttributes(new int[]{colorPrimary});
            themeColor = ta.getColor(0, dkgray);
            ta.recycle();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return themeColor;
    }

    public static void shareSong(Context context, Song song) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("content:///" + song.getFilePath()));
        context.startActivity(Intent.createChooser(share, "Share Sound File"));
        context.getContentResolver().notifyChange(Uri.parse("content://media"), null);
    }

    public static void showSongDetails(Context context, Song song) {

    }
}
