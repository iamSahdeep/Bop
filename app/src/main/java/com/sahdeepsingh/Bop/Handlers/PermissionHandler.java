package com.sahdeepsingh.Bop.Handlers;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PermissionHandler {

    public static boolean isRecordingPergiven(Context context) {
        return ContextCompat.checkSelfPermission(context, RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;

    }

    public static boolean isStoragePergiven(Context context) {
        int result = ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }


    public static void requestRecording(Activity activity, int PERMISSION_REQUEST_CODE) {
        ActivityCompat.requestPermissions(activity, new String[]{RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
    }

    public static void requestStorage(Activity activity, int PERMISSION_REQUEST_CODE) {
        ActivityCompat.requestPermissions(activity, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    public static void requestBothPermssion(Activity activity, int PERMISSION_REQUEST_CODE) {
        ActivityCompat.requestPermissions(activity, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
    }
}
