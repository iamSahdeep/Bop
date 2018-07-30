package com.sahdeepsingh.Bop.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.playerMain.Main;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Main.mainMenuHasNowPlayingItem){
            Intent intent = new Intent(SplashScreen.this,MainScreen.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else {
            Main.initialize(this);
            scanSongs(false);
        }
    }

    void scanSongs(boolean forceScan) {

        // Loading all the songs from the device on a different thread.
        // We'll only actually do it if they weren't loaded already
        //
        // See the implementation right at the end of this class.
        if ((forceScan) || (!Main.songs.isInitialized())) {

            /*SingleToast.show(MainScreen.this,
                    getString(R.string.menu_main_scanning),
                    Toast.LENGTH_LONG);*/

            new SplashScreen.ScanSongs().execute();
        }
    }

    class ScanSongs extends AsyncTask<String, Integer, String> {

        /**
         * The action we'll do in the background.
         */
        @Override
        protected String doInBackground(String... params) {

            try {
                // Will scan all songs on the device
                Main.songs.scanSongs(SplashScreen.this, "external");
                return SplashScreen.this.getString(R.string.menu_main_scanning_ok);
            } catch (Exception e) {
                Log.e("Couldn't execute", e.toString());
                e.printStackTrace();
                return SplashScreen.this.getString(R.string.menu_main_scanning_not_ok);
            }
        }

        /**
         * Called once the background processing is done.
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Intent intent = new Intent(SplashScreen.this,MainScreen.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }



}
