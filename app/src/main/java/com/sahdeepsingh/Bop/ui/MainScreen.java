package com.sahdeepsingh.Bop.ui;


import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.bullhead.equalizer.EqualizerFragment;
import com.sahdeepsingh.Bop.R;
import com.sahdeepsingh.Bop.controls.CircularSeekBar;
import com.sahdeepsingh.Bop.fragments.FragmentAlbum;
import com.sahdeepsingh.Bop.fragments.FragmentGenre;
import com.sahdeepsingh.Bop.fragments.FragmentPlaylist;
import com.sahdeepsingh.Bop.fragments.FragmentSongs;
import com.sahdeepsingh.Bop.notifications.NotificationMusic;
import com.sahdeepsingh.Bop.playerMain.Main;
import com.sahdeepsingh.Bop.playerMain.SingleToast;
import com.sahdeepsingh.Bop.visualizer.barVisuals;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.util.Objects;
import java.util.Random;


public class MainScreen extends ActivityMaster implements MediaController.MediaPlayerControl, ActionBar.TabListener, FragmentSongs.OnListFragmentInteractionListener, FragmentPlaylist.OnListFragmentInteractionListener, FragmentGenre.OnListFragmentInteractionListener, FragmentAlbum.OnListFragmentInteractionListener {


    public static final String BROADCAST_ACTION = "lol";

    /**
     * How long to wait to disable double-pressing to quit
     */
    private static final int BACK_PRESSED_DELAY = 2000;

    private static final float BLUR_RADIUS = 25f;

    CircularSeekBar circularSeekBar;
    ImageView blurimage, centreimage, aa, equalizer;
    TextView name, artist , TopName , TopArttist;
    ImageButton shuffletoggle, previousSong, PlayPause, nextSong, repeatToggle, pp;
    private boolean playbackPaused = false;

    barVisuals barVisualss;

    ChangeSongBR changeSongBR;

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
    SlidingUpPanelLayout slidingUpPanelLayout;
    FloatingActionButton floatingActionButton;

    /**
     * Adds a new item "Now Playing" on the main menu, if
     * it ain't there yet.
     */
    public static void addNowPlayingItem() {

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
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_main_screen);


        slidingUpPanelLayout = findViewById(R.id.sliding_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        name = findViewById(R.id.bottomtextView);
        artist = findViewById(R.id.bottomtextartist);
        pp = findViewById(R.id.bottomImagebutton);
        aa = findViewById(R.id.bottomImageview);
        TopName = findViewById(R.id.songMainTitle);
        TopArttist = findViewById(R.id.songMainArtist);
        equalizer = findViewById(R.id.equalizer);
        barVisualss = findViewById(R.id.barVisuals);


        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        floatingActionButton = findViewById(R.id.fab_Playall);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainScreen.this, PlayingNow.class);
                Main.musicList.clear();
                Main.musicList = Main.songs.songs;
                Main.nowPlayingList = Main.musicList;
                intent.putExtra("songPosition", Main.nowPlayingList.get(new Random().nextInt(Main.nowPlayingList.size())).getTrackNumber());
                if (!Main.musicService.isShuffle())
                    Main.musicService.toggleShuffle();
                startActivity(intent);
            }
        });

        mViewPager = findViewById(R.id.container);

        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
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
                    BottomControls.setVisibility(View.INVISIBLE);
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
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
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
        no
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
        floatingActionButton.setImageResource(R.mipmap.ic_suffle_on);
        if (isPlaying()) {
            Main.musicService.notifyCurrentSong();
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            slidingUpPanelLayout.setCoveredFadeColor(getResources().getColor(R.color.transparent));
        }

        if (isPlaying()) {
            if (playbackPaused) {
                playbackPaused = false;
            }
            workonSlidingPanel();
            barVisualss.setPlayer(getAudioSessionId());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(changeSongBR);
        if (Main.musicService.isPlaying())
        barVisualss.release();
    }

    private void setControllListeners() {


        if (Main.musicService.isShuffle())
            shuffletoggle.setImageResource(R.mipmap.ic_suffle_on);
        else shuffletoggle.setImageResource(R.mipmap.ic_suffle_off);


        if (Main.musicService.isRepeat())
            repeatToggle.setImageResource(R.mipmap.ic_repeat_on);
        else repeatToggle.setImageResource(R.mipmap.ic_repeat_off);


        shuffletoggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main.musicService.toggleShuffle();
                if (Main.musicService.isShuffle())
                    shuffletoggle.setImageResource(R.mipmap.ic_suffle_on);
                else shuffletoggle.setImageResource(R.mipmap.ic_suffle_off);

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
                    PlayPause.setImageResource(R.mipmap.ic_play);
                    pp.setImageResource(R.mipmap.ic_play);
                } else {
                    PlayPause.setImageResource(R.mipmap.ic_pause);
                    pp.setImageResource(R.mipmap.ic_pause);
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
                    repeatToggle.setImageResource(R.mipmap.ic_repeat_on);
                else repeatToggle.setImageResource(R.mipmap.ic_repeat_off);
            }
        });
        pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main.musicService.togglePlayback();
                if (!Main.musicService.isPaused()) {
                    pp.setImageResource(R.mipmap.ic_pause);
                    PlayPause.setImageResource(R.mipmap.ic_pause);
                } else {
                    pp.setImageResource(R.mipmap.ic_play);
                    PlayPause.setImageResource(R.mipmap.ic_play);
                }
            }
        });

        equalizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main.musicService.player.setLooping(true);
                EqualizerFragment equalizerFragment = EqualizerFragment.newBuilder()
                        .setAccentColor(Color.parseColor("#4caf50"))
                        .setAudioSessionId(getAudioSessionId())
                        .build();
                getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, equalizerFragment)
                        .commit();
            }
        });
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
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

    private void workOnImages() {
        File path;
        Log.e("wtr", String.valueOf(Main.songs.getAlbumArt(Main.musicService.currentSong)));
        if (Main.songs.getAlbumArt(Main.musicService.currentSong) != null)
            path = new File(Main.songs.getAlbumArt(Main.musicService.currentSong));
        else path = null;
        Bitmap bitmap;
        if (path != null && path.exists()) {
            bitmap = BitmapFactory.decodeFile(path.getAbsolutePath());
        } else bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        centreimage.setImageBitmap(bitmap);
        Bitmap blurredBitmap = blurMyImage(bitmap);
        blurimage.setImageBitmap(blurredBitmap);


    }

    private void prepareSeekBar() {

        barVisualss.setColor(ContextCompat.getColor(this, R.color.white));
        barVisualss.setDensity(200);
        barVisualss.setPlayer(getAudioSessionId());

        circularSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                if (fromUser)
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
                    circularSeekBar.setProgress(getCurrentPosition());
                handler.postDelayed(this, 1);
            }
        });

        workOnImages();
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
                PlayPause.setImageResource(R.mipmap.ic_play);
                pp.setImageResource(R.mipmap.ic_play);
            } else {
                PlayPause.setImageResource(R.mipmap.ic_pause);
                pp.setImageResource(R.mipmap.ic_pause);
            }
            Bitmap newImage;
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 4;
            newImage = BitmapFactory.decodeFile(Main.songs.getAlbumArt(Main.musicService.currentSong));
            if (newImage != null)
                aa.setImageBitmap(newImage);
            //else aa.setImageResource(R.mipmap.ic_launcher);
        }

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
        return Main.musicService.getAudioSession();
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
            playbackPaused = false;
        }

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
            playbackPaused = false;
        }

    }

}
