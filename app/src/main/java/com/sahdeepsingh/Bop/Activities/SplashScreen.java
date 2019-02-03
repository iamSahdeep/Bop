package com.sahdeepsingh.Bop.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.playerMain.Main;

import java.io.File;
import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SplashScreen extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPerm()) {
                requestPerm();
            } else {
                if (Main.mainMenuHasNowPlayingItem) {
                    Intent intent = new Intent(SplashScreen.this, MainScreen.class);
                    startActivity(intent);
                } else {
                    Main.initialize(this);
                    scanSongs(false);
                }
            }
        } else {
            if (Main.mainMenuHasNowPlayingItem) {
                Intent intent = new Intent(SplashScreen.this, MainScreen.class);
                startActivity(intent);
            } else {
                Main.initialize(this);
                scanSongs(false);
            }
        }
    }

    private boolean checkPerm() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPerm() {

        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean ReadAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if (ReadAccepted && WriteAccepted && RecordAccepted) {
                        if (Main.mainMenuHasNowPlayingItem) {
                            Intent intent = new Intent(SplashScreen.this, MainScreen.class);
                            startActivity(intent);
                        } else {
                            Main.initialize(this);
                            scanSongs(false);
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
                                showMessageOKCancel(
                                        new DialogInterface.OnClickListener() {
                                            @RequiresApi(api = Build.VERSION_CODES.M)
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                requestPermissions(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, RECORD_AUDIO},
                                                        PERMISSION_REQUEST_CODE);
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }
                break;
        }
    }


    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(SplashScreen.this)
                .setMessage("You need to allow access to both the permissions")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    void scanSongs(boolean forceScan) {
        Activity activity = this;
        if ((forceScan) || (!Main.songs.isInitialized())) {
            new SplashScreen.ScanSongs(this).execute();
        } else if (Intent.ACTION_VIEW.equals(activity.getIntent().getAction())) {
            File file = new File(activity.getIntent().getData().getPath());
            Intent intent = new Intent(activity, PlayingNowList.class);
            intent.putExtra("file", file);
            Main.musicList.clear();
            Main.musicList.add(Main.songs.getSongbyFile(file));
            Main.nowPlayingList = Main.musicList;
            if (Main.nowPlayingList == null) {
                Toast.makeText(this, "Selected Item is not a song OR is not in mediaStore", Toast.LENGTH_SHORT).show();
                return;
            }
            Main.musicService.setList(Main.nowPlayingList);
            activity.startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(activity, MainScreen.class);
            activity.startActivity(intent);
        }
    }

    static class ScanSongs extends AsyncTask<String, Integer, String> {

        private WeakReference<SplashScreen> activityReference;

        ScanSongs(SplashScreen context) {
            activityReference = new WeakReference<>(context);
        }

        /**
         * The action we'll do in the background.
         */
        @Override
        protected String doInBackground(String... params) {

            // get a reference to the activity if it is still there
            SplashScreen activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return "lol";

            try {
                Main.songs.scanSongs(activity, "external");
                return activity.getString(R.string.menu_main_scanning_ok);
            } catch (Exception e) {
                Log.e("Couldn't execute", e.toString());
                e.printStackTrace();
                return activity.getString(R.string.menu_main_scanning_not_ok);
            }
        }

        /**
         * Called once the background processing is done.
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            SplashScreen activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            if (Intent.ACTION_VIEW.equals(activity.getIntent().getAction())) {
                File file = new File(activity.getIntent().getData().getPath());
                Intent intent = new Intent(activity, PlayingNowList.class);
                intent.putExtra("file", file);
                Main.musicList.clear();
                if (Main.songs.getSongbyFile(file) == null){
                    Toast.makeText(activity, "Selected Song is not in mediaStore yet, Cant play for now", Toast.LENGTH_SHORT).show();
                    return;
                }
                Main.musicList.add(Main.songs.getSongbyFile(file));
                Main.nowPlayingList = Main.musicList;
                if (Main.nowPlayingList == null) {
                    Toast.makeText(activity, "Selected Item is not a song OR is not in mediaStore", Toast.LENGTH_SHORT).show();
                    return;
                }
                Main.musicService.setList(Main.nowPlayingList);
                activity.startActivity(intent);
                activity.finish();
            } else {
                Intent intent = new Intent(activity, MainScreen.class);
                activity.startActivity(intent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Main.startMusicService(this);
    }
}
