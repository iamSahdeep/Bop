package com.sahdeepsingh.Bop.ui;


import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.controls.CircularSeekBar;
import com.sahdeepsingh.Bop.controls.MusicController;
import com.sahdeepsingh.Bop.fragments.FragmentAlbum;
import com.sahdeepsingh.Bop.fragments.FragmentGenre;
import com.sahdeepsingh.Bop.fragments.FragmentPlaylist;
import com.sahdeepsingh.Bop.fragments.FragmentSongs;
import com.sahdeepsingh.Bop.notifications.NotificationMusic;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.sahdeepsingh.Bop.playerMain.SingleToast;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;



public class MainScreen extends ActivityMaster implements MediaController.MediaPlayerControl, ActionBar.TabListener, FragmentSongs.OnListFragmentInteractionListener, FragmentPlaylist.OnListFragmentInteractionListener, FragmentGenre.OnListFragmentInteractionListener, FragmentAlbum.OnListFragmentInteractionListener {


    public static final String BROADCAST_ACTION = "lol";
    static final int USER_CHANGED_THEME = 1;
    /**
     * How long to wait to disable double-pressing to quit
     */
    private static final int BACK_PRESSED_DELAY = 2000;

    private static final float BLUR_RADIUS = 25f;
    CircularSeekBar circularSeekBar;
    ImageView blurimage, centreimage, aa;
    TextView name, artist , TopName , TopArttist;
    ImageButton shuffletoggle, previousSong, PlayPause, nextSong, repeatToggle, pp;
    private MusicController musicController;
    public boolean paused = false;
    private boolean playbackPaused = false;


    ChangeSongBR changeSongBR;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private boolean backPressedOnce = false;
    /**
     * Action that actually disables double-pressing to quit
     */
    private final Runnable backPressedTimeoutAction = new Runnable() {
        @Override
        public void run() {
            backPressedOnce = false;
        }
    };
    private Handler backPressedHandler = new Handler();
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    SlidingUpPanelLayout slidingUpPanelLayout;

    /**
     * Adds a new item "Now Playing" on the main menu, if
     * it ain't there yet.
     */
    public static void addNowPlayingItem(Context c) {

        if (Main.mainMenuHasNowPlayingItem)
            return;

        Main.mainMenuHasNowPlayingItem = true;

        // Refresh ListView
    }

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

        slidingUpPanelLayout = findViewById(R.id.sliding_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        name = findViewById(R.id.bottomtextView);
        artist = findViewById(R.id.bottomtextartist);
        pp = findViewById(R.id.bottomImagebutton);
        aa = findViewById(R.id.bottomImageview);
        TopName = findViewById(R.id.songMainTitle);
        TopArttist = findViewById(R.id.songMainArtist);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab_Playall);

        mViewPager = (ViewPager) findViewById(R.id.container);

        setupViewPager(mViewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        changeSongBR = new ChangeSongBR();
        slidingUpPanelLayoutListen();

    }

    private void slidingUpPanelLayoutListen() {

        final LinearLayout songNameDisplay , BottomControls;
        songNameDisplay = findViewById(R.id.SongNameTop);
        BottomControls = findViewById(R.id.layout_item);
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED)
                {
                    BottomControls.setVisibility(View.GONE);
                    songNameDisplay.setAlpha(0f);
                    songNameDisplay.setVisibility(View.VISIBLE);
                    songNameDisplay.animate().alpha(1.0f).setDuration(300).setListener(null);
                }else if (newState == SlidingUpPanelLayout.PanelState.DRAGGING){
                    BottomControls.setAlpha(0f);
                    songNameDisplay.setAlpha(0f);
                }else{
                    songNameDisplay.setVisibility(View.GONE);
                    BottomControls.setAlpha(0f);
                    BottomControls.setVisibility(View.VISIBLE);
                    BottomControls.animate().alpha(1.0f).setDuration(300).setListener(null);
                }

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        viewPager.setAdapter(mSectionsPagerAdapter);
    }


    @Override
    public void onListFragmentInteraction(int position, String type) {

        Intent intent = new Intent(this, PlayingNow.class);

        switch (type) {
            case "singleSong":
                Main.musicList.clear();
                Main.musicList.add(Main.songs.songs.get(position));
                Main.nowPlayingList = Main.musicList;
                intent.putExtra("songPosition", position);
                startActivity(intent);


                break;
            case "playlist":
                Main.musicList.clear();
                String selectedPlaylist = Main.songs.playlists.get(position).getName();
                Main.musicList = Main.songs.getSongsByPlaylist(selectedPlaylist);
                Main.nowPlayingList = Main.musicList;
                intent.putExtra("playlistName", selectedPlaylist);
                startActivity(intent);

                break;
            case "GenreList":
                Main.musicList.clear();
                String selectedGenre = Main.songs.getGenres().get(position);
                Main.musicList = Main.songs.getSongsByGenre(selectedGenre);
                Main.nowPlayingList = Main.musicList;
                intent.putExtra("genreName", selectedGenre);
                startActivity(intent);
                break;
            case "AlbumList":
                Main.musicList.clear();
                String selectedAlbum = Main.songs.getAlbums().get(position);
                Main.musicList = Main.songs.getSongsByAlbum(selectedAlbum);
                Main.nowPlayingList = Main.musicList;
                intent.putExtra("albumName", selectedAlbum);
                startActivity(intent);
                break;
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
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        else {
            if (this.backPressedOnce) {
                // Default behavior, quit it
                super.onBackPressed();
                Main.forceExit(this);
                finishAffinity();
                return;
            }

            this.backPressedOnce = true;

            SingleToast.show(this, getString(R.string.menu_main_back_to_exit), Toast.LENGTH_SHORT);

            backPressedHandler.postDelayed(backPressedTimeoutAction, BACK_PRESSED_DELAY);
        }
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
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_ACTION);
        registerReceiver(changeSongBR, intentFilter);

        if (Main.mainMenuHasNowPlayingItem) {
            Main.musicService.notifyCurrentSong();
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            slidingUpPanelLayout.setCoveredFadeColor(getResources().getColor(R.color.transparent));
        }

        if (Main.mainMenuHasNowPlayingItem) {
            setMusicController();

            if (playbackPaused) {
                setMusicController();
                playbackPaused = false;
            }
            workonSlidingPanel();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(changeSongBR);
    }

    class ChangeSongBR extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            name.setText(Main.musicService.currentSong.getTitle());
            artist.setText(Main.musicService.currentSong.getArtist());
            TopName.setText(Main.musicService.currentSong.getTitle());
            TopArttist.setText(Main.musicService.currentSong.getArtist());
            name.setSelected(true);
            artist.setSelected(true);
            TopName.setSelected(true);
            TopArttist.setSelected(true);
            workOnImages();
            if (Main.musicService.isPaused()) {
                PlayPause.setImageResource(R.drawable.ic_play_white);
                pp.setImageResource(R.drawable.ic_play_white);
            } else {
                PlayPause.setImageResource(R.drawable.ic_pause_white);
                pp.setImageResource(R.drawable.ic_pause_white);
            }
            Bitmap newImage;
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 4;
            newImage = BitmapFactory.decodeFile(Main.songs.getAlbumArt(Main.musicService.currentSong));
            aa.setImageBitmap(newImage);
        }

    }

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
                    return new FragmentSongs();
                case 1:
                    return new FragmentPlaylist();
                case 2:
                    return new FragmentGenre();
                case 3:
                    return new FragmentAlbum();

                default:
                    return new FragmentSongs();


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


    private void workonSlidingPanel() {

        circularSeekBar = findViewById(R.id.circularSeekBar);
        blurimage = findViewById(R.id.BlurImage);
        centreimage = findViewById(R.id.CircleImage);
        shuffletoggle = findViewById(R.id.shuffle);
        previousSong = findViewById(R.id.previous);
        PlayPause = findViewById(R.id.playPause);
        nextSong = findViewById(R.id.skip_next);
        repeatToggle = findViewById(R.id.repeat);

        setControllListeners();
        prepareSeekBar();


    }


    private void setControllListeners() {


        if (Main.musicService.isShuffle())
            shuffletoggle.setImageResource(R.drawable.ic_menu_shuffle_on);
        else shuffletoggle.setImageResource(R.drawable.ic_menu_shuffle_off);


        if (Main.musicService.isRepeat())
            repeatToggle.setImageResource(R.drawable.ic_menu_repeat_on);
        else repeatToggle.setImageResource(R.drawable.ic_menu_repeat_off);


        shuffletoggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main.musicService.toggleShuffle();
                if (Main.musicService.isShuffle())
                    shuffletoggle.setImageResource(R.drawable.ic_menu_shuffle_on);
                else shuffletoggle.setImageResource(R.drawable.ic_menu_shuffle_off);

            }
        });
        previousSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPrevious();
            }
        });
        PlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main.musicService.togglePlayback();
                if (Main.musicService.isPaused()) {
                    PlayPause.setImageResource(R.drawable.ic_play_white);
                    pp.setImageResource(R.drawable.ic_play_white);
                } else {
                    PlayPause.setImageResource(R.drawable.ic_pause_white);
                    pp.setImageResource(R.drawable.ic_pause_white);
                }
            }
        });
        nextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playNext();
            }
        });
        repeatToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main.musicService.toggleRepeat();
                if (Main.musicService.isRepeat())
                    repeatToggle.setImageResource(R.drawable.ic_menu_repeat_on);
                else repeatToggle.setImageResource(R.drawable.ic_menu_repeat_off);
            }
        });
        pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main.musicService.togglePlayback();
                if (!Main.musicService.isPaused()) {
                    pp.setImageResource(R.drawable.ic_pause_white);
                    PlayPause.setImageResource(R.drawable.ic_pause_white);
                } else {
                    pp.setImageResource(R.drawable.ic_play_white);
                    PlayPause.setImageResource(R.drawable.ic_play_white);
                }
            }
        });

    }

    private void prepareSeekBar() {

        circularSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                if (musicController != null && fromUser)
                    seekTo(progress);
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }
        });


        circularSeekBar.setMax((int) Main.musicService.currentSong.getDuration());
        final Handler handler = new Handler();
        MainScreen.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isPlaying())
                    circularSeekBar.setProgress((int) getCurrentPosition());

                handler.postDelayed(this, 1);
            }
        });

        workOnImages();
    }

    private void workOnImages() {
        File path;
        Log.e("wtr", String.valueOf(Main.songs.getAlbumArt(Main.musicService.currentSong)));
        if (Main.songs.getAlbumArt(Main.musicService.currentSong) != null)
            path = new File(Main.songs.getAlbumArt(Main.musicService.currentSong));
        else path = null;
        Bitmap bitmap;
        if (path != null && path.exists()) {
            bitmap = BitmapFactory.decodeFile(path.getAbsolutePath());
        } else bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_cancel_dark);
        centreimage.setImageBitmap(bitmap);
        Bitmap blurredBitmap = blurMyImage(bitmap);
        blurimage.setImageBitmap(blurredBitmap);


    }

    private Bitmap blurMyImage(Bitmap image) {
        if (null == image) return null;

        Bitmap bitmaplol = image.copy(image.getConfig(), true);
        RenderScript renderScript = RenderScript.create(this);
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, bitmaplol);

//Intrinsic Gausian blur filter
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(bitmaplol);
        renderScript.destroy();
        return bitmaplol;

    }

    private void setMusicController() {

        musicController = new MusicController(MainScreen.this);

        // What will happen when the user presses the
        // next/previous buttons?
        musicController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Calling method defined on ActivityNowPlaying
                playNext();
            }
        }, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Calling method defined on ActivityNowPlaying
                playPrevious();
            }
        });

        // Binding to our media player
        musicController.setMediaPlayer(this);
        musicController.setEnabled(true);
    }


    @Override
    public void start() {
        Main.musicService.unpausePlayer();
    }

    /**
     * Callback to when the user pressed the `pause` button.
     */
    @Override
    public void pause() {
        Main.musicService.pausePlayer();
    }

    @Override
    public int getDuration() {
        if (Main.musicService != null && Main.musicService.musicBound
                && Main.musicService.isPlaying())
            return Main.musicService.getDuration();
        else
            return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (Main.musicService != null && Main.musicService.musicBound
                && Main.musicService.isPlaying())
            return Main.musicService.getPosition();
        else
            return 0;
    }

    @Override
    public void seekTo(int position) {
        Main.musicService.seekTo(position);
    }

    @Override
    public boolean isPlaying() {
        return Main.musicService != null && Main.musicService.musicBound && Main.musicService.isPlaying();

    }

    @Override
    public int getBufferPercentage() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        // TODO Auto-generated method stub
        return 0;
    }

    // Back to the normal methods

    /**
     * Jumps to the next song and starts playing it right now.
     */
    public void playNext() {
        Main.musicService.next(true);
        Main.musicService.playSong();
        // To prevent the MusicPlayer from behaving
        // unexpectedly when we pause the song playback.
        if (playbackPaused) {
            setMusicController();
            playbackPaused = false;
        }

/*
        musicController.show();
*/
    }

    /**
     * Jumps to the previous song and starts playing it right now.
     */
    public void playPrevious() {
        Main.musicService.previous(true);
        Main.musicService.playSong();

        // To prevent the MusicPlayer from behaving
        // unexpectedly when we pause the song playback.
        if (playbackPaused) {
            setMusicController();
            playbackPaused = false;
        }

/*
        musicController.show();
*/
    }

}
