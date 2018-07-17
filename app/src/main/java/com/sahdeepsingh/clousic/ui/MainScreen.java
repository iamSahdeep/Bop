package com.sahdeepsingh.clousic.ui;


import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.sahdeepsingh.clousic.R;
import com.sahdeepsingh.clousic.controls.BottomControlsView;
import com.sahdeepsingh.clousic.fragments.FragmentAlbum;
import com.sahdeepsingh.clousic.fragments.FragmentGenre;
import com.sahdeepsingh.clousic.fragments.FragmentPlaylist;
import com.sahdeepsingh.clousic.fragments.FragmentSongs;
import com.sahdeepsingh.clousic.fragments.dummy.DummyContent;
import com.sahdeepsingh.clousic.notifications.NotificationMusic;
import com.sahdeepsingh.clousic.playerMain.Main;
import com.sahdeepsingh.clousic.playerMain.SingleToast;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

public class MainScreen extends ActivityMaster implements ActionBar.TabListener,FragmentSongs.OnListFragmentInteractionListener , FragmentPlaylist.OnListFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;


    static final int USER_CHANGED_THEME = 1;

    private boolean backPressedOnce = false;
    private Handler backPressedHandler = new Handler();

    /** How long to wait to disable double-pressing to quit */
    private static final int BACK_PRESSED_DELAY = 2000;

    /** Action that actually disables double-pressing to quit */
    private final Runnable backPressedTimeoutAction = new Runnable() {
        @Override
        public void run() {
            backPressedOnce = false;
        }
    };



    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Toolbar toolbar;
    private TabLayout tabLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // We need to load the settings right before creating
        // the first activity so that the user-selected theme
        // will be applied to the first screen.
        //
        // Loading default settings at the first time the app;
        // is loaded.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        Main.settings.load(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);


        if (Main.mainMenuHasNowPlayingItem)
        {


        }

        Main.initialize(this);

        scanSongs(false);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab_Playall);

        mViewPager = (ViewPager) findViewById(R.id.container);

        setupViewPager(mViewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



    }

    private void setupViewPager(ViewPager viewPager) {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        viewPager.setAdapter(mSectionsPagerAdapter);
    }


    /**
     * Starts the background process of scanning the songs.
     *
     * @param forceScan If we should scan again. You should set
     *                  this to true if you want to scan again
     *                  the database.
     *                  Otherwise, leave it `false` so we don't
     *                  rescan the songs when this Activity
     *                  is created again for some reason.
     */
    void scanSongs(boolean forceScan) {

        // Loading all the songs from the device on a different thread.
        // We'll only actually do it if they weren't loaded already
        //
        // See the implementation right at the end of this class.
        if ((forceScan) || (! Main.songs.isInitialized())) {

            /*SingleToast.show(MainScreen.this,
                    getString(R.string.menu_main_scanning),
                    Toast.LENGTH_LONG);*/

            new ScanSongs().execute();
        }
    }

    @Override
    public void onListFragmentInteraction(int position , String type) {
        Intent intent = new Intent(this, PlayingNow.class);

        if(type.equals("songs")){
            Main.nowPlayingList = Main.musicList;
            intent.putExtra("songs", position);

        }else if(type.equals("playlist")){
            String selectedPlaylist =  Main.songs.playlists.get(position).toString();
            Main.musicList = Main.songs.getSongsByPlaylist(selectedPlaylist);


        }




        startActivity(intent);

    }

    /**
     * Does an action on another Thread.
     *
     * On this case, we'll scan the songs on the Android device
     * without blocking the main Thread.
     *
     * It gives a nice pop-up when finishes.
     *
     * Source:
     * http://answers.oreilly.com/topic/2699-how-to-handle-threads-in-android-and-what-you-need-to-watch-for/
     */
    class ScanSongs extends AsyncTask<String, Integer, String> {

        /**
         * The action we'll do in the background.
         */
        @Override
        protected String doInBackground(String... params) {

            try {
                // Will scan all songs on the device
                Main.songs.scanSongs(MainScreen.this, "external");
                return MainScreen.this.getString(R.string.menu_main_scanning_ok);
            }
            catch (Exception e) {
                Log.e("Couldn't execute", e.toString());
                e.printStackTrace();
                return MainScreen.this.getString(R.string.menu_main_scanning_not_ok);
            }
        }

        /**
         * Called once the background processing is done.
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            /*SingleToast.show(MainScreen.this,
                    result,
                    Toast.LENGTH_LONG);*/
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Let's start the settings screen.
            // While doing so, we need to know if the user have
            // changed the theme.
            // If he did, we'll refresh the screen.
            // See `onActivityResult()`
            Intent settingsIntent = new Intent(this, ActivityMenuSettings.class);
            startActivityForResult(settingsIntent, USER_CHANGED_THEME);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Activity is about to become visible - let's start the music
     * service.
     */
    @Override
    protected void onStart() {
        super.onStart();

        Main.startMusicService(this);
    }

    @Override
    public void onBackPressed() {

        if (this.backPressedOnce) {
            // Default behavior, quit it
            super.onBackPressed();
            Main.forceExit(this);

            return;
        }

        this.backPressedOnce = true;

        SingleToast.show(this, getString(R.string.menu_main_back_to_exit), Toast.LENGTH_SHORT);

        backPressedHandler.postDelayed(backPressedTimeoutAction, BACK_PRESSED_DELAY);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

   /* *//**
     * A placeholder fragment containing a simple view.
     *//*
    public static class PlaceholderFragment extends Fragment {
        *//**
         * The fragment argument representing the section number for this
         * fragment.
         *//*
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        *//**
         * Returns a new instance of this fragment for the given section
         * number.
         *//*
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_screen, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    *//**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
/*
            return PlaceholderFragment.newInstance(position + 1);
*/
            switch (position) {
                case 0:
                    Main.musicList = Main.songs.songs;
                    return new FragmentSongs();
                case 1:
                    return new FragmentPlaylist();
                case 2:
                    return new FragmentGenre();
                case 3 :
                    return new FragmentAlbum();

                default: return new FragmentSongs();


            }
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Songs";
                case 1:
                    return "PlayList";
                case 2:
                    return "Genre";
                case 3:
                    return "Albums";
            }
            return null;
        }
    }

    /**
     * Adds a new item "Now Playing" on the main menu, if
     * it ain't there yet.
     */
    public static void addNowPlayingItem(Context c) {

        if (Main.mainMenuHasNowPlayingItem)
            return;

        ActivityMenuMain.items.add(c.getString(R.string.menu_main_now_playing));

        Main.mainMenuHasNowPlayingItem = true;

        // Refresh ListView
    }

    /**
     * When destroying the Activity.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (backPressedHandler != null)
            backPressedHandler.removeCallbacks(backPressedTimeoutAction);

        // Need to clear all the items otherwise
        // they'll keep adding up.
        // Cancell all thrown Notifications
        NotificationMusic.cancelAll(this);

/*
        Main.stopMusicService(this);
*/
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
